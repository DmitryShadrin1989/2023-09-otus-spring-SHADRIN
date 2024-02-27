package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public String getStringGenres() {
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.joining("; "));
    }

    public static List<BookDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(BookDto::new)
                .toList();
    }
}
