package by.bsuir.typepulse.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MetricsServiceTest {

    private final MetricsService metricsService = new MetricsService();

    @Test
    void calculatesWpmAndAccuracyForPerfectInput() {
        // 50 correct chars in 60_000 ms => (50/5)/1 = 10 WPM, 100% accuracy
        MetricsSnapshot snapshot = metricsService.calculate(
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwx",
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwx",
                60_000L);

        assertEquals(10.0, snapshot.wpm(), 0.1);
        assertEquals(100.0, snapshot.accuracy(), 0.5);
        assertEquals(0, snapshot.errors());
    }

    @Test
    void countsErrorsAndAccuracy() {
        MetricsSnapshot snapshot = metricsService.calculate("abcdef", "abXdef", 30_000L);
        assertEquals(1, snapshot.errors());
        assertEquals(5, snapshot.correctChars());
        assertEquals(83.3, snapshot.accuracy(), 0.5);
    }
}
