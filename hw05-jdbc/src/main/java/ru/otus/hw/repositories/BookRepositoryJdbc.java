package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class BookRepositoryJdbc implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Book> books = namedParameterJdbcOperations.query(
                "select " +
                        "b.id, " +
                        "b.title, " +
                        "b.author_id, " +
                        "a.full_name as author_full_name, " +
                        "bg.genre_id, " +
                        "g.name as genre_name " +
                        "from books b " +
                        "left join authors a on a.id = b.author_id " +
                        "left join books_genres bg on bg.book_id = b.id " +
                        "left join genres g on g.id = bg.genre_id " +
                        "where b.id = :id",
                params, new BookResultSetExtractor());
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update("delete from books b where b.id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query(
                "select b.id, b.title, b.author_id, a.full_name as author_full_name " +
                        "from books b join authors a on a.id = b.author_id",
                new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query(
                "select bg.book_id, bg.genre_id from books_genres bg", new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> bookMap = new HashMap<>();
        for (Book book : booksWithoutGenres) {
            book.setGenres(new ArrayList<>());
            bookMap.put(book.getId(), book);
        }
        Map<Long, Genre> genreMap = new HashMap<>();
        for (Genre genre : genres) {
            genreMap.put(genre.getId(), genre);
        }
        for (BookGenreRelation relation : relations) {
            bookMap.get(relation.bookId()).getGenres().add(genreMap.get(relation.genreId()));
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put("title", book.getTitle());
        params.put("authorId", book.getAuthor().getId());
        namedParameterJdbcOperations.update(
                "insert into books(title, author_id) values (:title, :authorId)",
                new MapSqlParameterSource(params), keyHolder, new String[]{"id"});

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        params.put("authorId", book.getAuthor().getId());
        namedParameterJdbcOperations.update(
                "update books b set b.title = :title, b.author_id = :authorId where b.id = :id", params);

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        SqlParameterSource[] batchArgs = book.getGenres().stream()
                .map(g -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("bookId", book.getId());
                    params.put("genreId", g.getId());
                    return new MapSqlParameterSource(params);
                }).toArray(SqlParameterSource[]::new);
        namedParameterJdbcOperations.batchUpdate(
                "insert into books_genres(book_id, genre_id) values (:bookId, :genreId)", batchArgs);
    }

    private void removeGenresRelationsFor(Book book) {
        Map<String, Object> params = new HashMap<>();
        params.put("bookId", book.getId());
        namedParameterJdbcOperations.update("delete from books_genres b where b.book_id = :bookId", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            long authorId = rs.getLong("author_id");
            String authorFullName = rs.getString("author_full_name");
            Author author = new Author(authorId, authorFullName);

            return new Book(id, title, author, null);
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> bookMap = new HashMap<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                Book book;
                if (bookMap.containsKey(id)) {
                    book = bookMap.get(id);
                } else {
                    String title = rs.getString("title");
                    long authorId = rs.getLong("author_id");
                    String authorFullName = rs.getString("author_full_name");
                    book = new Book(id, title, new Author(authorId, authorFullName), new ArrayList<>());
                    bookMap.put(id, book);
                }
                long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");
                book.getGenres().add(new Genre(genreId, genreName));
            }
            return bookMap.values().stream().toList();
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }
}
