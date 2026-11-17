package by.bsuir.typepulse.training;

import by.bsuir.typepulse.domain.Difficulty;
import by.bsuir.typepulse.domain.Language;
import by.bsuir.typepulse.domain.Result;
import by.bsuir.typepulse.domain.SessionStatus;
import by.bsuir.typepulse.domain.TextSnippet;
import by.bsuir.typepulse.domain.TrainingSession;
import by.bsuir.typepulse.metrics.MetricsService;
import by.bsuir.typepulse.metrics.MetricsSnapshot;
import by.bsuir.typepulse.repository.ResultRepository;
import by.bsuir.typepulse.repository.TextSnippetRepository;
import by.bsuir.typepulse.repository.TrainingSessionRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService {

    private final TextSnippetRepository textSnippetRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final ResultRepository resultRepository;
    private final MetricsService metricsService;

    public TrainingService(TextSnippetRepository textSnippetRepository,
                           TrainingSessionRepository trainingSessionRepository,
                           ResultRepository resultRepository,
                           MetricsService metricsService) {
        this.textSnippetRepository = textSnippetRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.resultRepository = resultRepository;
        this.metricsService = metricsService;
    }

    @Transactional
    public TrainingSession start(Long userId, Language language, Difficulty difficulty, int durationSec) {
        TextSnippet text = textSnippetRepository
                .findFirstByLanguageAndDifficultyAndOwnerUserIdIsNull(language, difficulty)
                .orElseThrow(() -> new IllegalStateException("Нет текста для выбранных параметров"));

        TrainingSession session = new TrainingSession();
        session.setUserId(userId);
        session.setTextId(text.getId());
        session.setDurationSec(durationSec);
        session.setStartedAt(Instant.now());
        session.setStatus(SessionStatus.RUNNING);
        session.setLanguage(language);
        session.setDifficulty(difficulty);
        return trainingSessionRepository.save(session);
    }

    public TextSnippet requireText(Long textId) {
        return textSnippetRepository.findById(textId)
                .orElseThrow(() -> new IllegalArgumentException("Текст не найден"));
    }

    public TrainingSession requireSession(Long sessionId) {
        return trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Сеанс не найден"));
    }

    @Transactional
    public Result finish(Long sessionId, Long userId, String typedText, long elapsedMs) {
        Optional<Result> existing = resultRepository.findBySessionId(sessionId);
        if (existing.isPresent()) {
            return existing.get();
        }

        TrainingSession session = requireSession(sessionId);
        if (!session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Чужой сеанс недоступен");
        }
        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw new IllegalStateException("Сеанс отменён, результат не сохраняется");
        }
        if (session.getStatus() != SessionStatus.RUNNING && session.getStatus() != SessionStatus.CREATED) {
            throw new IllegalStateException("Сеанс уже завершён");
        }

        TextSnippet text = requireText(session.getTextId());
        MetricsSnapshot metrics = metricsService.calculate(text.getBody(), typedText, elapsedMs);

        session.setStatus(SessionStatus.FINISHED);
        trainingSessionRepository.save(session);

        Result result = new Result();
        result.setSessionId(session.getId());
        result.setUserId(userId);
        result.setWpm(metrics.wpm());
        result.setAccuracy(metrics.accuracy());
        result.setErrors(metrics.errors());
        result.setElapsedMs(elapsedMs);
        result.setFinishedAt(Instant.now());
        return resultRepository.save(result);
    }

    @Transactional
    public void cancel(Long sessionId, Long userId) {
        TrainingSession session = requireSession(sessionId);
        if (!session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Чужой сеанс недоступен");
        }
        if (session.getStatus() == SessionStatus.RUNNING || session.getStatus() == SessionStatus.CREATED) {
            session.setStatus(SessionStatus.CANCELLED);
            trainingSessionRepository.save(session);
        }
    }
}
