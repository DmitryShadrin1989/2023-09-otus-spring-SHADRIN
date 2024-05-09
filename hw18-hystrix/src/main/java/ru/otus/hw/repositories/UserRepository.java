package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.User;

import java.util.Optional;

@RepositoryRestResource(path = "user")
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
}
