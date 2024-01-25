package io.feedpulse.repository;

import io.feedpulse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(io.feedpulse.model.enums.Role name);
}
