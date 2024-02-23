package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(String id) {
        return new BookDto(bookRepository.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return BookDto.toDtoList(bookRepository.findAll());
    }

    @Override
    @Transactional
    public BookDto insert(String title, String authorId, List<String> genresIds) {
        return new BookDto(save(null, title, authorId, genresIds));
    }

    @Override
    @Transactional
    public BookDto insert(BookInsertUpdateDto book) {
        return new BookDto(save(null, book.getTitle(), book.getAuthorId(), book.getGenreIds()));
    }

    @Override
    @Transactional
    public BookDto update(String id, String title, String authorId, List<String> genresIds) {
        return new BookDto(save(id, title, authorId, genresIds));
    }

    @Override
    @Transactional
    public BookDto update(BookInsertUpdateDto bookInsertUpdateDto) {
        return new BookDto(save(bookInsertUpdateDto.getId(), bookInsertUpdateDto.getTitle(),
                bookInsertUpdateDto.getAuthorId(), bookInsertUpdateDto.getGenreIds()));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        findById(id);
        bookRepository.deleteById(id);
        commentRepository.deleteByBookId(id);
    }

    private Book save(String id, String title, String authorId, List<String> genresIds) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres)) {
            throw new EntityNotFoundException("Genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, author, genres);
        return bookRepository.save(book);
    }
}
