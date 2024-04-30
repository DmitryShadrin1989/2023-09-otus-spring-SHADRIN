package ru.otus.hw.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookJdbcDto;
import ru.otus.hw.dto.RelationOfBookAndJdbcIdDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookJdbcDtoRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BookStepConfig {

    private static final int CHUNK_SIZE = 3;

    private final Map<String, Sort.Direction> sorts = Map.of("id", Sort.Direction.ASC);

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final DataSource dataSource;

    private final MongoTemplate mongoTemplate;

    private final BookJdbcDtoRepository bookJdbcDtoRepository;

    private final BookConverter bookConverter;

    @Bean
    public RepositoryItemReader<BookJdbcDto> bookItemReader() {
        return new RepositoryItemReaderBuilder<BookJdbcDto>()
                .name("bookRepositoryItemReader")
                .sorts(sorts)
                .repository(bookJdbcDtoRepository)
                .methodName("findAll")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<BookJdbcDto, RelationOfBookAndJdbcIdDto> bookProcessor() {
        return bookConverter::convertToRelationOfDomainAndIdDto;
    }

    @Bean
    public CastomMongoItemWriter<RelationOfBookAndJdbcIdDto> bookCastomMongoItemWriter() {
        CastomMongoItemWriter<RelationOfBookAndJdbcIdDto> castomMongoItemWriter =
                new CastomMongoItemWriter<>();
        castomMongoItemWriter.setCollection("books");
        castomMongoItemWriter.setTemplate(mongoTemplate);

        return castomMongoItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<RelationOfBookAndJdbcIdDto> tempBookIdsJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<RelationOfBookAndJdbcIdDto>()
                .dataSource(dataSource)
                .sql("insert into temp_book_ids(id_table, id_document) values (?, ?)")
                .itemPreparedStatementSetter(
                        (item, ps) -> {
                            ps.setLong(1, item.getJdbcId());
                            ps.setString(2, item.getBook().getId());
                        }).build();
    }

    @Bean
    public CompositeItemWriter<RelationOfBookAndJdbcIdDto> bookCompositeItemWriter(
            MongoItemWriter<RelationOfBookAndJdbcIdDto> bookMongoItemWriter,
            JdbcBatchItemWriter<RelationOfBookAndJdbcIdDto> tempBookIdsJdbcBatchItemWriter) {
        CompositeItemWriter<RelationOfBookAndJdbcIdDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(bookMongoItemWriter, tempBookIdsJdbcBatchItemWriter));
        return writer;
    }

    @Bean
    public Step migrateBooksStep(RepositoryItemReader<BookJdbcDto> reader,
                                 ItemProcessor<BookJdbcDto, RelationOfBookAndJdbcIdDto> itemProcessor,
                                 CompositeItemWriter<RelationOfBookAndJdbcIdDto> writer) {
        return new StepBuilder("migrateBooksStep", jobRepository)
                .<BookJdbcDto, RelationOfBookAndJdbcIdDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep createBookTempTable(JobRepository jobRepository) {
        return new StepBuilder("createBookTempTable", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            "create table temp_book_ids (id_table bigint, id_document varchar(255))");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep dropBookTempTable(JobRepository jobRepository) {
        return new StepBuilder("dropBookTempTable", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            "drop table temp_book_ids");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    public static class CastomMongoItemWriter<T> extends MongoItemWriter<T> {

        @Override
        public void write(Chunk<? extends T> chunk) throws Exception {
            List<? extends T> items = chunk.getItems();
            List<Book> books = new ArrayList<>();
            for (T item : items) {
                RelationOfBookAndJdbcIdDto relation = (RelationOfBookAndJdbcIdDto) item;
                books.add(relation.getBook());
            }
            super.write(new Chunk(books));
        }
    }
}
