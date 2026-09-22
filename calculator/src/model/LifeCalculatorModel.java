package model;

public class LifeCalculatorModel {

    // Статистические данные
    private static final double MOUSE_DISTANCE_PER_DAY_KM = 1.29;
    private static final double CLICKS_PER_DAY = 1045;
    private static final double KEY_PRESSES_PER_DAY = 5086;

    // Продолжительность условного рабочего дня
    private static final double WORKING_HOURS_PER_DAY = 8.0;

    // Введённые пользователем данные
    private int years;
    private double hoursPerDay;

    // Результаты расчёта
    private double totalHours;
    private double totalDays;
    private double cursorDistanceKm;
    private double mouseClicks;
    private double keyPresses;

    // Слушатель активной модели
    private ModelListener listener;

    public void setListener(ModelListener listener) {
        this.listener = listener;
    }

    public void setData(int years, double hoursPerDay) {
        validateData(years, hoursPerDay);

        this.years = years;
        this.hoursPerDay = hoursPerDay;

        calculate();

        if (listener != null) {
            listener.modelChanged();
        }
    }

    private void validateData(int years, double hoursPerDay) {
        if (years <= 0 || years > 120) {
            throw new IllegalArgumentException(
                    "Количество лет должно быть от 1 до 120."
            );
        }

        if (hoursPerDay <= 0 || hoursPerDay > 24) {
            throw new IllegalArgumentException(
                    "Количество часов за компьютером должно быть больше 0 и не превышать 24."
            );
        }
    }

    private void calculate() {
        double totalComputerDays = years * 365.25;

        // Общее количество часов
        totalHours = totalComputerDays * hoursPerDay;

        totalDays = totalHours / 24.0;

        // Расстояние курсора
        double cursorDistancePerHour =
                MOUSE_DISTANCE_PER_DAY_KM / WORKING_HOURS_PER_DAY;

        cursorDistanceKm = totalHours * cursorDistancePerHour;

        // Количество кликов мышью
        double clicksPerHour =
                CLICKS_PER_DAY / WORKING_HOURS_PER_DAY;

        mouseClicks = totalHours * clicksPerHour;

        // Количество нажатий клавиш
        double keyPressesPerHour =
                KEY_PRESSES_PER_DAY / WORKING_HOURS_PER_DAY;

        keyPresses = totalHours * keyPressesPerHour;
    }

    public int getYears() {
        return years;
    }

    public double getHoursPerDay() {
        return hoursPerDay;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getTotalDays() {
        return totalDays;
    }

    public double getCursorDistanceKm() {
        return cursorDistanceKm;
    }

    public double getMouseClicks() {
        return mouseClicks;
    }

    public double getKeyPresses() {
        return keyPresses;
    }

    public interface ModelListener {
        void modelChanged();
    }
}