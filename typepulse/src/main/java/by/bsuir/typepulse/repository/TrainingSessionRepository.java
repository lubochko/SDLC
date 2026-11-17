package by.bsuir.typepulse.repository;

import by.bsuir.typepulse.domain.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}
