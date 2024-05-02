package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Book;

@Data
@AllArgsConstructor
public class RelationOfBookAndJdbcIdDto {

    private Long jdbcId;

    private Book book;
}
