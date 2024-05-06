package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Genre;
import ru.otus.hw.data.GenreTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoRepository для работы с жанрами книг ")
@DataMongoTest
class GenreRepositoryTest {

    @Autowired
    GenreRepository repository;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = GenreTestData.getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров книг")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repository.findAll();
        var expectedGenres = genres;

        assertThat(actualGenres)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("должен загружать список жанров книг по ids")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        var actualGenres = repository.findAllById(genres.stream().map(Genre::getId).toList());
        var expectedGenres = genres;

        assertThat(actualGenres)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
        actualGenres.forEach(System.out::println);
    }
}