package com.example.flippedclass.repository;

import com.example.flippedclass.entity.Role;
import com.example.flippedclass.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);

    Set<Role> findByNameIn(Set<String> names);
}
