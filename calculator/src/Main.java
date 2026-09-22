import controller.LifeCalculatorController;
import model.LifeCalculatorModel;
import view.MainView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LifeCalculatorModel model = new LifeCalculatorModel();

            MainView view = new MainView(model);

            new LifeCalculatorController(model, view);

            view.setVisible(true);
        });
    }
}