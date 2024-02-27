package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;

import java.util.List;

public interface BookService {
    BookDto findById(String id);

    List<BookDto> findAll();

    BookDto insert(String title, String authorId, List<String> genresIds);

    BookDto insert(BookInsertUpdateDto book);

    BookDto update(String id, String title, String authorId, List<String> genresIds);

    BookDto update(BookInsertUpdateDto bookInsertUpdateDto);

    void deleteById(String id);

}
