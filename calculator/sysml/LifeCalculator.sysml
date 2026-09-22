package LifeCalculator {

    part def LifeCalculatorModel;
    part def MainView;
    part def InputDialog;
    part def LifeCalculatorController;

    part application {
        part model : LifeCalculatorModel;
        part view : MainView;
        part dialog : InputDialog;
        part controller : LifeCalculatorController;

        connect view to controller;
        connect controller to model;
        connect model to view;
    }
}