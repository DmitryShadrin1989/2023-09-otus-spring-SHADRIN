package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Genre;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookInsertUpdateDto {

    private String id;

    @NotBlank
    private String title;

    @NotNull
    private String authorId;

    @NotNull
    private List<String> genreIds;

    public static BookInsertUpdateDto toDto(BookDto bookDto) {
        return new BookInsertUpdateDto(bookDto.getId(), bookDto.getTitle(), bookDto.getAuthor().getId(),
                bookDto.getGenres().stream()
                        .map(Genre::getId)
                        .toList());
    }
}
