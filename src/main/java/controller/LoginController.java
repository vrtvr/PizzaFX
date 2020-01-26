package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.User;
import service.LoginService;
import service.WindowService;

import java.io.IOException;
import java.util.Optional;

// klasa obslugujaca zadania uzytkownika
// mapowanie zadan uzytkownika na logike aplikacji
public class LoginController {
    // obiekty globalne
    LoginService loginService;
    WindowService windowService;

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblInfo;

    @FXML
    void keyLoginAction(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            loginService.login(tfLogin, pfPassword, lblInfo);
        }
    }
    @FXML
    void loginAction(ActionEvent event) {
        loginService.login(tfLogin, pfPassword, lblInfo);

    }

    @FXML
    void registerAction(ActionEvent event) throws IOException {
        // otwarcie nowego okna rejestracji
        windowService.createWindow("registrationView", "panel rejestracji");
        // zamkniecie okna logowania
        windowService.closeWindow(lblInfo); // podajemy dowolona kontrolke znajdujaca sie na oknie aplikacji w argumencie

    }

    // metoda ktora zostanie wykonana jako pierwsza po wyswietleniu widoku loginView.fxml
    public void initialize() {
        // inicjalizacja logiki biznesowej
        loginService = new LoginService();
        windowService = new WindowService();

    }



}
