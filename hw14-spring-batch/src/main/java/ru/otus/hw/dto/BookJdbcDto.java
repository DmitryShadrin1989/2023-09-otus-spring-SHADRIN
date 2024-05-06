package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookJdbcDto {

    private Long id;

    private String title;

    private Long authorId;

    private List<Long> genreIds;
}
