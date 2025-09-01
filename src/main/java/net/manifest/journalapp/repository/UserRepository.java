package net.manifest.journalapp.repository;

import lombok.NonNull;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findById(@NonNull ObjectId objectId);
    Optional<User> findByUsername(String username);
    Optional<Object> findByEmail(@NonNull String email);
    boolean existsByRolesContains(Role role);
}
