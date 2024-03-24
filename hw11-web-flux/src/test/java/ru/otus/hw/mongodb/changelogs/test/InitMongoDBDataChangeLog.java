package ru.otus.hw.mongodb.changelogs.test;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@ChangeLog
public class InitMongoDBDataChangeLog {

    private Genre genre1;

    private Genre genre2;

    private Genre genre3;

    private Author author1;

    private Author author2;

    private Author author3;

    @ChangeSet(order = "000", id = "dropDB", author = "DShadrin", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initGenres", author = "DShadrin", runAlways = true)
    public void initGenres(GenreRepository repository) {
        genre1 = repository.save(new Genre("id_1", "genre_1")).block();
        genre2 = repository.save(new Genre("id_2", "genre_2")).block();
        genre3 = repository.save(new Genre("id_3", "genre_3")).block();
    }

    @ChangeSet(order = "002", id = "initAuthors", author = "DShadrin", runAlways = true)
    public void initAuthors(AuthorRepository repository) {
        author1 = repository.save(new Author("id_1", "author_1")).block();
        author2 = repository.save(new Author("id_2", "author_2")).block();
        author3 = repository.save(new Author("id_3", "author_3")).block();
    }

    @ChangeSet(order = "003", id = "initBooks", author = "DShadrin", runAlways = true)
    public void initBooksWithComments(BookRepository bookRepository, CommentRepository commentRepository) {
        Book book1 = bookRepository.save(new Book("id_1", "title_1", author1, List.of(genre1))).block();
        Book book2 = bookRepository.save(new Book("id_2", "title_2", author2, List.of(genre2))).block();
        Book book3 = bookRepository.save(new Book("id_3", "title_3", author3, List.of(genre1, genre2, genre3))).block();

        commentRepository.save(new Comment("id_1", "comment_1", book1)).block();
        commentRepository.save(new Comment("id_2", "comment_2", book1)).block();
        commentRepository.save(new Comment("id_3", "comment_3", book1)).block();
        commentRepository.save(new Comment("id_4", "comment_4", book2)).block();
        commentRepository.save(new Comment("id_5", "comment_5", book2)).block();
        commentRepository.save(new Comment("id_6", "comment_6", book2)).block();
    }

    public static <T> void initData(ReactiveMongoTemplate reactiveMongoTemplate, List<T> list) {
        for (T value : list) {
            reactiveMongoTemplate.save(value).block();
        }
    }
}
