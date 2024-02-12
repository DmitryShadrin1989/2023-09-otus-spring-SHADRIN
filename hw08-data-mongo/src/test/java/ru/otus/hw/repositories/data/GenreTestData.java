package ru.otus.hw.repositories.data;

import ru.otus.hw.models.Genre;

import java.util.List;

public class GenreTestData {

    public static List<Genre> getDbGenres() {
        return List.of(new Genre("id_1", "genre_1"),
                new Genre("id_2", "genre_2"),
                new Genre("id_3", "genre_3"));
    }
}
