package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Book;

@RepositoryRestResource(path = "book")
public interface BookRepository extends MongoRepository<Book, String> {

}
