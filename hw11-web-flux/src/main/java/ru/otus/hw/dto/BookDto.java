package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String id;

    @NotBlank
    private String title;

    @NotNull
    private String authorId;

    @NotNull
    private List<String> genreIds;

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.authorId = book.getAuthor().getId();
        this.genreIds = book.getGenres().stream()
                .map(Genre::getId)
                .toList();
    }

    public static List<BookDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(BookDto::new)
                .toList();
    }
}
