package by.bsuir.typepulse.config;

import by.bsuir.typepulse.domain.Difficulty;
import by.bsuir.typepulse.domain.Language;
import by.bsuir.typepulse.domain.TextSnippet;
import by.bsuir.typepulse.repository.TextSnippetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TextSnippetRepository textSnippetRepository;

    public DataInitializer(TextSnippetRepository textSnippetRepository) {
        this.textSnippetRepository = textSnippetRepository;
    }

    @Override
    public void run(String... args) {
        if (textSnippetRepository.count() > 0) {
            return;
        }
        save(Language.EN, Difficulty.EASY,
                "the quick brown fox jumps over the lazy dog and runs across the quiet green field near the river");
        save(Language.EN, Difficulty.MEDIUM,
                "Practice builds muscle memory. Keep your wrists straight, eyes on the screen, and rhythm steady while typing longer phrases without looking down.");
        save(Language.EN, Difficulty.HARD,
                "Concurrency, idempotency and observability matter when finishing a session: compute WPM server-side, persist an immutable result, and never store passwords in plain text.");
        save(Language.RU, Difficulty.EASY,
                "быстрая рыжая лиса прыгает через ленивого пса и бежит по тихому зелёному полю около реки утром");
        save(Language.RU, Difficulty.MEDIUM,
                "Регулярные короткие тренировки помогают улучшить скорость и точность набора. Следите за осанкой и не смотрите на клавиатуру во время печати.");
        save(Language.RU, Difficulty.HARD,
                "Сервер остаётся источником истины для метрик: WPM считается по формуле правильные символы делённые на пять и на время в минутах, а точность — доля верных нажатий.");
    }

    private void save(Language language, Difficulty difficulty, String body) {
        TextSnippet snippet = new TextSnippet();
        snippet.setLanguage(language);
        snippet.setDifficulty(difficulty);
        snippet.setBody(body);
        textSnippetRepository.save(snippet);
    }
}
