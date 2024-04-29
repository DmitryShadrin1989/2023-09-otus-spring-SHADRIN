package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.dto.BookJdbcDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookJdbcDtoRepository implements PagingAndSortingRepository<BookJdbcDto, Long> {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    // the method is not implemented
    @Override
    public Iterable<BookJdbcDto> findAll(Sort sort) {
        return new ArrayList<>();
    }

    @Override
    public Page<BookJdbcDto> findAll(Pageable pageable) {
        var books = getAllBooksWithoutGenres(pageable);
        if (!books.isEmpty()) {
            var relations = getAllGenreRelations(books.stream().map(BookJdbcDto::getId).toList());
            mergeBooksInfo(books, relations);
        }
        return new PageImpl<>(books);
    }

    private List<BookJdbcDto> getAllBooksWithoutGenres(Pageable pageable) {
        String querySql = "select b.id, b.title, b.author_id from books b %s"
                .formatted("LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset());
        return namedParameterJdbcOperations.query(
                querySql, new BookJdbcDtoRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations(List<Long> bookIds) {
        return namedParameterJdbcOperations.query(
                "select bg.book_id, bg.genre_id from books_genres bg where bg.book_id in (:bookIds)",
                Map.of("bookIds", bookIds),
                new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<BookJdbcDto> booksWithoutGenres, List<BookGenreRelation> relations) {
        Map<Long, BookJdbcDto> bookMap = new HashMap<>();
        for (BookJdbcDto book : booksWithoutGenres) {
            book.setGenreIds(new ArrayList<>());
            bookMap.put(book.getId(), book);
        }
        for (BookGenreRelation relation : relations) {
            bookMap.get(relation.bookId()).getGenreIds().add(relation.genreId());
        }
    }

    private static class BookJdbcDtoRowMapper implements RowMapper<BookJdbcDto> {

        @Override
        public BookJdbcDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            long authorId = rs.getLong("author_id");

            return new BookJdbcDto(id, title, authorId, null);
        }
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
