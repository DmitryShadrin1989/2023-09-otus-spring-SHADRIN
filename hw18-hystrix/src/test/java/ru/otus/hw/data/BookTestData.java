package ru.otus.hw.data;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

public class BookTestData {

    public static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {

        return List.of(new Book("id_1", "title_1", dbAuthors.get(0), List.of(dbGenres.get(0))),
                new Book("id_2", "title_2", dbAuthors.get(1), List.of(dbGenres.get(1))),
                new Book("id_3", "title_3", dbAuthors.get(2), dbGenres));
    }

    public static List<Book> getDbBooks() {
        var dbAuthors = AuthorTestData.getDbAuthors();
        var dbGenres = GenreTestData.getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    public static Book getNewBook() {
        return new Book("id_4", "title_new_book", AuthorTestData.getDbAuthors().get(0), GenreTestData.getDbGenres());
    }

    public static Book getChangeBook() {
        return new Book("id_1", "title_change_book", AuthorTestData.getDbAuthors().get(1), GenreTestData.getDbGenres());
    }
}
