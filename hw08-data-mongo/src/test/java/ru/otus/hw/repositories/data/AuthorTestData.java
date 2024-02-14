package ru.otus.hw.repositories.data;

import ru.otus.hw.models.Author;

import java.util.List;

public class AuthorTestData {

    public static List<Author> getDbAuthors() {
        return List.of(new Author("id_1", "author_1"),
                new Author("id_2", "author_2"),
                new Author("id_3", "author_3"));
    }
}
