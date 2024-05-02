package ru.otus.hw.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentJdbcDto;
import ru.otus.hw.models.Comment;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class CommentStepConfig {

    private static final int CHUNK_SIZE = 3;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final DataSource dataSource;

    private final MongoTemplate mongoTemplate;

    private final CommentConverter bookConverter;

    @Bean
    public ItemReader<CommentJdbcDto> commentItemReader() {
        JdbcCursorItemReader<CommentJdbcDto> reader = new JdbcCursorItemReader<>();
        reader.setName("commentItemReader");
        reader.setDataSource(dataSource);
        reader.setSql("select c.id, c.content, t.id_document " +
                "from comments c join temp_book_ids t on t.id_table = c.book_id");
        reader.setRowMapper((rs, rowNum) -> {
            long id = rs.getLong("id");
            String content = rs.getString("content");
            String bookId = rs.getString("id_document");
            return new CommentJdbcDto(id, content, bookId);
        });
        return reader;
    }

    @Bean
    public ItemProcessor<CommentJdbcDto, Comment> commentProcessor() {
        return bookConverter::convertToDomain;
    }

    @Bean
    public MongoItemWriter<Comment> commentItemWriter() {
        return new MongoItemWriterBuilder<Comment>()
                .collection("comments")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step migrateCommentsStep(ItemReader<CommentJdbcDto> reader,
                                   ItemProcessor<CommentJdbcDto, Comment> itemProcessor,
                                   MongoItemWriter<Comment> writer) {
        return new StepBuilder("migrateCommentsStep", jobRepository)
                .<CommentJdbcDto, Comment>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
