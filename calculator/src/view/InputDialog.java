package view;

import javax.swing.*;
import java.awt.*;

public class InputDialog extends JDialog {

    private final JTextField yearsField;
    private final JTextField hoursPerDayField;

    private boolean confirmed;

    public InputDialog(
            JFrame parent,
            int currentYears,
            double currentHoursPerDay
    ) {
        super(parent, "Ввод данных", true);

        yearsField = new JTextField(
                currentYears > 0 ? String.valueOf(currentYears) : ""
        );

        hoursPerDayField = new JTextField(
                currentHoursPerDay > 0
                        ? String.valueOf(currentHoursPerDay)
                        : ""
        );

        confirmed = false;

        createInterface();

        setSize(400, 220);
        setLocationRelativeTo(parent);
    }

    private void createInterface() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
                "Введите данные для расчёта",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(
                new GridLayout(2, 2, 8, 8)
        );

        fieldsPanel.add(
                new JLabel("Количество лет:")
        );
        fieldsPanel.add(yearsField);

        fieldsPanel.add(
                new JLabel("Часов в день:")
        );
        fieldsPanel.add(hoursPerDayField);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        setContentPane(panel);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getYearsText() {
        return yearsField.getText().trim();
    }

    public String getHoursPerDayText() {
        return hoursPerDayField.getText().trim();
    }
}