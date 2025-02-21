package de.feedpulse.repository;

import de.feedpulse.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<de.feedpulse.model.Role, UUID> {

    Optional<de.feedpulse.model.Role> findByName(Role name);
}
