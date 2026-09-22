package controller;

import model.LifeCalculatorModel;
import view.InputDialog;
import view.MainView;

import javax.swing.*;

public class LifeCalculatorController {

    private final LifeCalculatorModel model;
    private final MainView view;

    public LifeCalculatorController(
            LifeCalculatorModel model,
            MainView view
    ) {
        this.model = model;
        this.view = view;

        view.getInputButton().addActionListener(e -> openInputDialog());
    }

    private void openInputDialog() {
        InputDialog dialog = new InputDialog(
                view,
                model.getYears(),
                model.getHoursPerDay()
        );

        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            return;
        }

        try {
            int years = Integer.parseInt(
                    dialog.getYearsText()
            );

            double hoursPerDay = Double.parseDouble(
                    dialog.getHoursPerDayText()
                            .replace(',', '.')
            );

            model.setData(years, hoursPerDay);

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    view,
                    "Введите корректные числовые значения.",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    view,
                    exception.getMessage(),
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}