package ru.otus.hw.mongodb.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
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

    private Genre genreHistoricalNovel;

    private Genre genreScienceFiction;

    private Genre genreFantasy;

    private Genre genreDetective;

    private Genre genreNovel;

    private Author authorAlexandrPushkin;

    private Author authorSergeiLukyanenko;

    private Author authorTolkien;

    private Author authorAgathaChristie;

    @ChangeSet(order = "000", id = "dropDB", author = "DShadrin", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initGenres", author = "DShadrin", runAlways = true)
    public void initGenres(GenreRepository repository) {
        genreHistoricalNovel = repository.save(new Genre(null, "Historical novel"));
        genreScienceFiction = repository.save(new Genre(null, "Science fiction"));
        genreFantasy = repository.save(new Genre(null, "Fantasy"));
        genreDetective = repository.save(new Genre(null, "Detective"));
        genreNovel = repository.save(new Genre(null, "Novel"));
    }

    @ChangeSet(order = "002", id = "initAuthors", author = "DShadrin", runAlways = true)
    public void initAuthors(AuthorRepository repository) {
        authorAlexandrPushkin = repository.save(new Author(null, "Alexandr Pushkin"));
        authorSergeiLukyanenko = repository.save(new Author(null, "Sergei Lukyanenko"));
        authorTolkien = repository.save(new Author(null, "John Ronald Reuel Tolkien"));
        authorAgathaChristie = repository.save(new Author(null, "Agatha Christie"));
    }

    @ChangeSet(order = "003", id = "initBooks", author = "DShadrin", runAlways = true)
    public void initBooksWithComments(BookRepository bookRepository, CommentRepository commentRepository) {
        Book book1 = bookRepository.save(new Book(null, "The captain's daughter", authorAlexandrPushkin,
                List.of(genreHistoricalNovel)));
        Book book2 = bookRepository.save(new Book(null, "Draft and finishing", authorSergeiLukyanenko,
                List.of(genreScienceFiction, genreNovel, genreFantasy)));
        Book book3 = bookRepository.save(new Book(null, "The Silmarillion", authorTolkien,
                List.of(genreNovel, genreFantasy)));
        Book book4 = bookRepository.save(new Book(null, "Five piglets", authorAgathaChristie,
                List.of(genreNovel, genreDetective)));

        commentRepository.save(new Comment(null, "This is my favorite book in Russian classical literature.", book1));
        commentRepository.save(new Comment(null, "Is this a story based on real events?", book1));
        commentRepository.save(new Comment(null, "I wasn't interested.", book1));
        commentRepository.save(new Comment(null, "This is one of my favorite fantasy novels.", book2));
        commentRepository.save(new Comment(null, "It is very similar to the works of the Strugatsky brothers.", book2));
        commentRepository.save(new Comment(null, "Can you recommend more books by this author?", book2));
    }
}
