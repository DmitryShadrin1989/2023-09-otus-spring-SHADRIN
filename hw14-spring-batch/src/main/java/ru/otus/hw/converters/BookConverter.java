package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookJdbcDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookJdbcDtoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookConverter {

    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final BookJdbcDtoRepository bookJdbcDtoRepository;

    public String bookToString(BookDto book) {
        var genresString = book.getGenres().stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString);
    }

    public Book convertToDomain(BookJdbcDto bookJdbcDto) {
        String bookId = new ObjectId().toString();
        bookJdbcDtoRepository.insertIntoTempTable(bookJdbcDto.getId(), bookId);
        Author author = authorConverter.getAuthor(bookJdbcDto.getAuthorId());
        List<Genre> genres = bookJdbcDto.getGenreIds().stream()
                .map(genreConverter::getGenre)
                .toList();
        return new Book(bookId, bookJdbcDto.getTitle(), author, genres);
    }
}
