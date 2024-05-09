package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Role;

@RepositoryRestResource(path = "role")
public interface RoleRepository extends MongoRepository<Role, String> {

}
