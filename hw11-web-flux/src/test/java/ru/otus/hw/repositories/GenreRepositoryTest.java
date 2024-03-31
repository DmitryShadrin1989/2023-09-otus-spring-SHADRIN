package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.models.Genre;

import java.util.List;

@DisplayName("Репозиторий на основе ReactiveMongoRepository для работы с жанрами книг ")
@SpringBootTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = GenreTestData.getGenres();
    }

    @DisplayName("должен загружать список всех жанров книг")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repository.findAll();
        var expectedGenres = genres;
        StepVerifier
                .create(actualGenres)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список жанров книг по ids")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        var actualGenres = repository.findAllById(genres.stream().map(Genre::getId).toList());
        var expectedGenres = genres;
        StepVerifier
                .create(actualGenres)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }
}