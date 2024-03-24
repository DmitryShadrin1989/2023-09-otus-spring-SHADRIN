package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе ReactiveMongoRepository для работы с авторами книг ")
@SpringBootTest
class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository repository;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = AuthorTestData.getAuthors();
    }

    @DisplayName("должен загружать автора книги по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldReturnCorrectAuthorById(Author expectedAuthor) {
        var actualAuthor = repository.findById(expectedAuthor.getId());
        StepVerifier
                .create(actualAuthor)
                .assertNext(author -> assertThat(author)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedAuthor))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех авторов книг")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repository.findAll();
        var expectedAuthors = authors;
        StepVerifier
                .create(actualAuthors)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    private static List<Author> getDbAuthors() {
        return AuthorTestData.getAuthors();
    }
}