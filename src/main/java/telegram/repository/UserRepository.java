package telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telegram.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
