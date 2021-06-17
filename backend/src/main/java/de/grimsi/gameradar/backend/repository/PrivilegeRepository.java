package de.grimsi.gameradar.backend.repository;

import de.grimsi.gameradar.backend.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByAuthority(String authority);
}
