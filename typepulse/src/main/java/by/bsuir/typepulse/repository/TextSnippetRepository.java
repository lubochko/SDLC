package by.bsuir.typepulse.repository;

import by.bsuir.typepulse.domain.Difficulty;
import by.bsuir.typepulse.domain.Language;
import by.bsuir.typepulse.domain.TextSnippet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextSnippetRepository extends JpaRepository<TextSnippet, Long> {
    List<TextSnippet> findByLanguageAndDifficultyAndOwnerUserIdIsNull(Language language, Difficulty difficulty);

    Optional<TextSnippet> findFirstByLanguageAndDifficultyAndOwnerUserIdIsNull(Language language, Difficulty difficulty);
}
