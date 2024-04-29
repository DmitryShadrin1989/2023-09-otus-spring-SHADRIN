package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookJdbcDto;
import ru.otus.hw.dto.RelationOfBookAndJdbcIdDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookConverter {

    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

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

    public RelationOfBookAndJdbcIdDto convertToRelationOfDomainAndIdDto(BookJdbcDto bookJdbcDto) {
        String bookId = new ObjectId().toString();
        Author author = authorConverter.getAuthor(bookJdbcDto.getAuthorId());
        List<Genre> genres = bookJdbcDto.getGenreIds().stream()
                .map(genreConverter::getGenre)
                .toList();
        return new RelationOfBookAndJdbcIdDto(bookJdbcDto.getId(),
                new Book(bookId, bookJdbcDto.getTitle(), author, genres));
    }
}
