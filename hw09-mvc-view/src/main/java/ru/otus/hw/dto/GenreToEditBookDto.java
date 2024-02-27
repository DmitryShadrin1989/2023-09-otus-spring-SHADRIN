package ru.otus.hw.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import ru.otus.hw.models.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreToEditBookDto {

    private String id;

    private String name;

    private boolean picked;

    public static List<GenreToEditBookDto> toDtoList(List<Genre> allGenres, List<String> currentBookGenresIds) {
        Set<String> setCurrentBookGenresIds =
                currentBookGenresIds != null ? new HashSet<>(currentBookGenresIds) : new HashSet<>();
        return allGenres.stream()
                .map(g -> new GenreToEditBookDto(g.getId(), g.getName(), setCurrentBookGenresIds.contains(g.getId())))
                .toList();
    }
}
