package repository;

import entity.User;
import entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRole(UserRole role);

    boolean existsByEmail(String email);
}
