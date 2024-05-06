package ru.otus.hw.data;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

public class BookTestData {

    public static List<Book> getExpectedBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return List.of(new Book("id_1", "BookTitle_1", dbAuthors.get(0),
                        List.of(dbGenres.get(0), dbGenres.get(1))),
                new Book("id_2", "BookTitle_2", dbAuthors.get(1),
                        List.of(dbGenres.get(2), dbGenres.get(3))),
                new Book("id_3", "BookTitle_3", dbAuthors.get(2),
                        List.of(dbGenres.get(4), dbGenres.get(5))));
    }
}
