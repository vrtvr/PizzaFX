package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import service.LoginService;
import service.RegistrationService;
import service.WindowService;

import java.io.IOException;

public class RegistrationController {
    // obiekty globalne
    private WindowService windowService;
    private RegistrationService registrationService;
    public static int result;

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private PasswordField pfPasswordConfirmation;

    @FXML
    private Label lblEquation;

    @FXML
    private TextField lblResult;

    @FXML
    private Label lblInfo;

    @FXML
    void backAction(ActionEvent event) throws IOException {
        windowService.createWindow("loginView", "panel logowania1");
        windowService.closeWindow(lblInfo);

    }
    @FXML
    void registerAction (ActionEvent event) {
        registrationService.registration(tfLogin, pfPassword, pfPasswordConfirmation,
                lblInfo, lblResult, lblEquation);
    }
    public void initialize() {
        // inicjalizacja zadeklarowanych obiektów
        windowService = new WindowService();
        registrationService = new RegistrationService();
        // generowanie randomowego rownania do sprawdzenia czy jestem robotem
        // 1. wypisanie rownania na lblEquation
        // 2. zwrocenie oczekiwanej wartosci do pola result
        result = registrationService.generateRandomEquation(lblEquation);



    }

}
