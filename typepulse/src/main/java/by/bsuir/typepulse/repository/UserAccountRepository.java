package by.bsuir.typepulse.repository;

import by.bsuir.typepulse.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByLogin(String login);

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
