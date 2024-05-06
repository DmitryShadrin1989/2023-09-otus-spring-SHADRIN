package ru.otus.hw.dto;

import lombok.Data;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookDto {

    private String id;

    private String title;

    private Author author;

    private List<Genre> genres;

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genres = new ArrayList<>();
        this.genres.addAll(book.getGenres());
    }
}
