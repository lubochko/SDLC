package view;

import model.LifeCalculatorModel;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame
        implements LifeCalculatorModel.ModelListener {

    private final LifeCalculatorModel model;

    private final JLabel yearsLabel;
    private final JLabel hoursPerDayLabel;
    private final JLabel totalHoursLabel;
    private final JLabel totalDaysLabel;
    private final JLabel cursorDistanceLabel;
    private final JLabel mouseClicksLabel;
    private final JLabel keyPressesLabel;

    private final JButton inputButton;

    public MainView(LifeCalculatorModel model) {
        this.model = model;

        setTitle("Калькулятор Жизни");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        yearsLabel = new JLabel("Не введено");
        hoursPerDayLabel = new JLabel("Не введено");
        totalHoursLabel = new JLabel("Не рассчитано");
        totalDaysLabel = new JLabel("Не рассчитано");
        cursorDistanceLabel = new JLabel("Не рассчитано");
        mouseClicksLabel = new JLabel("Не рассчитано");
        keyPressesLabel = new JLabel("Не рассчитано");

        inputButton = new JButton("Ввести данные");

        createInterface();

        model.setListener(this);
    }

    private void createInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
                "Калькулятор Жизни",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel(
                new GridLayout(7, 2, 8, 8)
        );

        resultPanel.add(new JLabel("Количество лет:"));
        resultPanel.add(yearsLabel);

        resultPanel.add(new JLabel("Часов в день:"));
        resultPanel.add(hoursPerDayLabel);

        resultPanel.add(new JLabel("Всего часов:"));
        resultPanel.add(totalHoursLabel);

        resultPanel.add(new JLabel("Всего суток:"));
        resultPanel.add(totalDaysLabel);

        resultPanel.add(new JLabel("Пробег курсора:"));
        resultPanel.add(cursorDistanceLabel);

        resultPanel.add(new JLabel("Количество кликов:"));
        resultPanel.add(mouseClicksLabel);

        resultPanel.add(new JLabel("Нажатий клавиш:"));
        resultPanel.add(keyPressesLabel);

        mainPanel.add(resultPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(inputButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public JButton getInputButton() {
        return inputButton;
    }

    @Override
    public void modelChanged() {
        yearsLabel.setText(String.valueOf(model.getYears()));
        hoursPerDayLabel.setText(
                String.format("%.2f", model.getHoursPerDay())
        );
        totalHoursLabel.setText(
                String.format("%.2f", model.getTotalHours())
        );
        totalDaysLabel.setText(
                String.format("%.2f", model.getTotalDays())
        );
        cursorDistanceLabel.setText(
                String.format("%.2f км", model.getCursorDistanceKm())
        );
        mouseClicksLabel.setText(
                String.format("%.0f", model.getMouseClicks())
        );
        keyPressesLabel.setText(
                String.format("%.0f", model.getKeyPresses())
        );
    }
}