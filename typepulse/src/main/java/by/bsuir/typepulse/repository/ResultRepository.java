package by.bsuir.typepulse.repository;

import by.bsuir.typepulse.domain.Result;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findBySessionId(Long sessionId);

    List<Result> findTop50ByUserIdOrderByFinishedAtDesc(Long userId);
}
