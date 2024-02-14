package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.data.AuthorTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoRepository для работы с авторами книг ")
@DataMongoTest
class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository repository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = AuthorTestData.getDbAuthors();
    }

    @DisplayName("должен загружать автора книги по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldReturnCorrectAuthorById(Author expectedAuthor) {
        var actualAuthor = repository.findById(expectedAuthor.getId());
        assertThat(actualAuthor).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов книг")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repository.findAll();
        var expectedAuthors = dbAuthors;

        assertThat(actualAuthors)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthors);
        actualAuthors.forEach(System.out::println);
    }

    private static List<Author> getDbAuthors() {
        return AuthorTestData.getDbAuthors();
    }
}