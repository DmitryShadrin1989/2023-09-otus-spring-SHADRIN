package ru.otus.hw.data;

import ru.otus.hw.models.Author;

import java.util.List;

public class AuthorTestData {

    public static List<Author> getExpectedAuthors() {
        return List.of(new Author("id_1", "Author_1"),
                new Author("id_2", "Author_2"),
                new Author("id_3", "Author_3"));
    }
}
