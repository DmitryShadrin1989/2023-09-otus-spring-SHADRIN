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
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreJdbcDto;
import ru.otus.hw.models.Genre;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class GenreStepConfig {

    private static final int CHUNK_SIZE = 3;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final DataSource dataSource;

    private final MongoTemplate mongoTemplate;

    private final GenreConverter genreConverter;

    @Bean
    public ItemReader<GenreJdbcDto> genreItemReader() {
        JdbcCursorItemReader<GenreJdbcDto> reader = new JdbcCursorItemReader<>();
        reader.setName("genreItemReader");
        reader.setDataSource(dataSource);
        reader.setSql("select g.id, g.name from genres g");
        reader.setRowMapper((rs, rowNum) -> {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new GenreJdbcDto(id, name);
        });
        return reader;
    }

    @Bean
    public ItemProcessor<GenreJdbcDto, Genre> genreProcessor() {
        return genreConverter::convertToDomain;
    }

    @Bean
    public MongoItemWriter<Genre> genreItemWriter() {
        return new MongoItemWriterBuilder<Genre>()
                .collection("genres")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step migrateGenresStep(ItemReader<GenreJdbcDto> reader,
                                  ItemProcessor<GenreJdbcDto, Genre> itemProcessor,
                                  MongoItemWriter<Genre> writer) {
        return new StepBuilder("migrateGenresStep", jobRepository)
                .<GenreJdbcDto, Genre>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
