package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import service.LoginService;

import java.util.Optional;

// klasa obslugujaca zadania uzytkownika
// mapowanie zadan uzytkownika na logike aplikacji
public class LoginController {
    // obiekty globalne
    LoginService loginService;

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblInfo;

    @FXML
    void loginAction(ActionEvent event) {
        Optional<User> userOpt = loginService.loginUser(tfLogin.getText(), pfPassword.getText());
        if(userOpt.isPresent()) {
            if (userOpt.get().isStatus()) {
                lblInfo.setText("zalogowano");
                loginService.clearLoginProbes(userOpt.get());
            } else {
                lblInfo.setText("Twoje konto jest zablokowane");
            }
        } else {
//            lblInfo.setText("błąd logowania");
            loginService.decrementProbes(tfLogin.getText());
            lblInfo.setText(loginService.getLoginProbes(tfLogin.getText()));
        }
    }

    @FXML
    void registerAction(ActionEvent event) {

    }

    // metoda ktora zostanie wykonana jako pierwsza po wyswietleniu widoku loginView.fxml
    public void initialize() {
        // inicjalizacja logiki biznesowej
        loginService = new LoginService();

    }

}
