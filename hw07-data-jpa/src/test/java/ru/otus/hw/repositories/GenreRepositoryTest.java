package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами книг ")
@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    GenreRepository repository;

    @Autowired
    private TestEntityManager em;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров книг")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repository.findAll();
        var expectedGenres = dbGenres;

        assertThat(actualGenres)
                .hasSize(6)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("должен загружать список жанров книг по ids")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        var actualGenres = repository.findAllById(List.of(1L, 2L, 3L));
        var expectedGenres = List.of(dbGenres.get(0), dbGenres.get(1), dbGenres.get(2));

        assertThat(actualGenres)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
        actualGenres.forEach(System.out::println);
    }

    public List<Genre> getDbGenres() {
        return List.of(em.find(Genre.class, 1L), em.find(Genre.class, 2L),
                em.find(Genre.class, 3L), em.find(Genre.class, 4L),
                em.find(Genre.class, 5L), em.find(Genre.class, 6L));
    }
}