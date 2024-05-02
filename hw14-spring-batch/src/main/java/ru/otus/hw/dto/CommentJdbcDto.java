package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentJdbcDto {

    private Long id;

    private String content;

    private String bookId;
}
