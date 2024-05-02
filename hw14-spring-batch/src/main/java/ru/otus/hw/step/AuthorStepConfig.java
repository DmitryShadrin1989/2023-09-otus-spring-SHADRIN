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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorJdbcDto;
import ru.otus.hw.models.Author;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class AuthorStepConfig {

    private static final int CHUNK_SIZE = 3;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final DataSource dataSource;

    private final MongoTemplate mongoTemplate;

    private final AuthorConverter authorConverter;

    @Bean
    public ItemReader<AuthorJdbcDto> authorItemReader() {
        JdbcCursorItemReader<AuthorJdbcDto> reader = new JdbcCursorItemReader<>();
        reader.setName("authorItemReader");
        reader.setDataSource(dataSource);
        reader.setSql("select a.id, a.full_name from authors a");
        reader.setRowMapper((rs, rowNum) -> {
            long id = rs.getLong("id");
            String fullName = rs.getString("full_name");
            return new AuthorJdbcDto(id, fullName);
        });
        return reader;
    }

    @Bean
    public ItemProcessor<AuthorJdbcDto, Author> authorProcessor() {
        return authorConverter::convertToDomain;
    }

    @Bean
    public MongoItemWriter<Author> authorItemWriter() {
        return new MongoItemWriterBuilder<Author>()
                .collection("authors")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step migrateAuthorsStep(ItemReader<AuthorJdbcDto> reader,
                                   ItemProcessor<AuthorJdbcDto, Author> itemProcessor,
                                   MongoItemWriter<Author> writer) {
        return new StepBuilder("migrateAuthorsStep", jobRepository)
                .<AuthorJdbcDto, Author>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
