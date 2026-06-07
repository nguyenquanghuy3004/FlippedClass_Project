package com.example.flippedclass.repository;

import com.example.flippedclass.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(com.example.flippedclass.enums.RoleName name);

    Set<Role> findByNameIn(Set<com.example.flippedclass.enums.RoleName> names);
}
