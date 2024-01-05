package dev.feder.repository;

import dev.feder.model.Role;
import dev.feder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(dev.feder.model.enums.Role name);
}
