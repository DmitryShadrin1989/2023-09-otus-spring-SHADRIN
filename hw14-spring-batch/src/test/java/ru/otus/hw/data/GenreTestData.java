package ru.otus.hw.data;

import ru.otus.hw.models.Genre;

import java.util.List;

public class GenreTestData {

    public static List<Genre> getExpectedGenres() {
        return List.of(new Genre("id_1", "Genre_1"),
                new Genre("id_2", "Genre_2"),
                new Genre("id_3", "Genre_3"),
                new Genre("id_4", "Genre_4"),
                new Genre("id_5", "Genre_5"),
                new Genre("id_6", "Genre_6"));
    }
}
