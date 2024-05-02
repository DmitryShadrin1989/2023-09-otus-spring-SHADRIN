package ru.otus.hw;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.config.JobConfig.JOB_NAME;

@SpringBootTest
@SpringBatchTest
public class MigrateLibraryJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private AuthorRepository authorsRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    private List<Author> expectedAuthors;

    private List<Genre> expectedGenres;

    private List<Book> expectedBooks;

    private List<Comment> expectedComments;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
        expectedAuthors = AuthorTestData.getExpectedAuthors();
        expectedGenres = GenreTestData.getExpectedGenres();
        expectedBooks = BookTestData.getExpectedBooks(expectedAuthors, expectedGenres);
        expectedComments = CommentTestData.getExpectedBooksComments(expectedBooks);
    }

    @DisplayName("должен выполнить миграцию данных")
    @Test
    void shouldMigrateTheData() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(JOB_NAME);

        JobParameters parameters = new JobParametersBuilder().toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        RecursiveComparisonConfiguration recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("id",
                        "author.id", "genres.id", "book.id",
                        "book.id", "book.author.id", "book.genres.id")
                .build();

        var actualAuthors = authorsRepository.findAll();
        assertThat(actualAuthors)
                .hasSize(3)
                .usingRecursiveComparison(recursiveComparisonConfiguration)
                .isEqualTo(expectedAuthors);

        var actualGenres = genreRepository.findAll();
        assertThat(actualGenres)
                .hasSize(6)
                .usingRecursiveComparison(recursiveComparisonConfiguration)
                .isEqualTo(expectedGenres);

        var actualBook = bookRepository.findAll();
        assertThat(actualBook)
                .hasSize(3)
                .usingRecursiveComparison(recursiveComparisonConfiguration)
                .isEqualTo(expectedBooks);

        var actualComment = commentRepository.findAll();
        assertThat(actualComment)
                .hasSize(6)
                .usingRecursiveComparison(recursiveComparisonConfiguration)
                .isEqualTo(expectedComments);

    }
}
