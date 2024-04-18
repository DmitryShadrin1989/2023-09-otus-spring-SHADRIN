package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

}
