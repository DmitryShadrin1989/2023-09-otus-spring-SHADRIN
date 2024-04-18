package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto findById(String id);

    List<CommentDto> findAllByBookId(String bookId);

    CommentDto insert(CommentDto commentDto);

    CommentDto update(CommentDto commentDto);

    void deleteById(String id);
}
