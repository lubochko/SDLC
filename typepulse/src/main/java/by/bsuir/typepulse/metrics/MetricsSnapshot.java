package by.bsuir.typepulse.metrics;

public record MetricsSnapshot(double wpm, double accuracy, int errors, int correctChars, int typedChars) {
}
