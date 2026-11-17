package by.bsuir.typepulse.metrics;

import org.springframework.stereotype.Service;

/**
 * REQ-FUNC-004 / VIEW-006:
 * WPM = (correctChars / 5) / (elapsedMs / 60000)
 * accuracy% = (correctChars / max(typedChars, 1)) * 100
 */
@Service
public class MetricsService {

    public MetricsSnapshot calculate(String reference, String typed, long elapsedMs) {
        if (reference == null) {
            reference = "";
        }
        if (typed == null) {
            typed = "";
        }
        int typedChars = typed.length();
        int correctChars = 0;
        int errors = 0;
        int limit = Math.min(reference.length(), typedChars);
        for (int i = 0; i < limit; i++) {
            if (reference.charAt(i) == typed.charAt(i)) {
                correctChars++;
            } else {
                errors++;
            }
        }
        if (typedChars > reference.length()) {
            errors += typedChars - reference.length();
        }

        double minutes = Math.max(elapsedMs, 1L) / 60000.0;
        double wpm = (correctChars / 5.0) / minutes;
        double accuracy = (correctChars * 100.0) / Math.max(typedChars, 1);
        return new MetricsSnapshot(round1(wpm), round1(accuracy), errors, correctChars, typedChars);
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
