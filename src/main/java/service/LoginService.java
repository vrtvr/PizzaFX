package service;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import utility.InMemoryDb;

import java.io.IOException;
import java.util.Optional;

// klasa implementujaca logike biznesowa aplikacji
public class LoginService {
    // metoda do logowania uzytkownika
    public Optional<User> loginUser(String login, String password) {
        return InMemoryDb.users.stream()                                                                    // stream
                .filter(user -> user.getLogin().equals(login) && user.getPassword().equals(password))       // logowanie
                .findAny();                                                                                 // Optional
    }

    // metoda do blokowania statusu uzytkownika
    public void decrementProbes(String login) {
        // wyszukanie uzytkownika po loginie
        Optional<User> userOpt = InMemoryDb.users.stream().filter(user -> user.getLogin().equals(login)).findAny();
        if (userOpt.isPresent()) {
            userOpt.get().setProbes(userOpt.get().getProbes() - 1);               // zmniejsza o 1 ilosc prob logowania
        }
        if (userOpt.isPresent()) {
            if (userOpt.get().getProbes() == 0) {
                System.out.println("ZABLOKOWANO KONTO");
                userOpt.get().setStatus(false);
            }
        }
    }

    // metoda restartujaca ilosc prob logowania
    public void clearLoginProbes(User user) {
        user.setProbes(3);
    }

    // metoda zwracajaca pozostala ilosc prob logowania lub nic w przypadku podanego login ktory nie istnieje w users
    public String getLoginProbes(String login) {
        Optional<User> userOpt = InMemoryDb.users.stream().filter(user -> user.getLogin().equals(login)).findAny();
        if (userOpt.isPresent()) {
            if (userOpt.get().getProbes() > 0) {
                return "błędne hasło - pozostało: " + userOpt.get().getProbes() + " próby/-a logowania";
            } else {
                return "Twoje konto jest zablokowane";
            }

        }
        return "błędny login";
    }
    public static User loggedUser;

    // metoda "wlasna" - nie jest wywolywana z widoku FXML
    public void login(TextField tfLogin, PasswordField pfPassword, Label lblInfo) {
        Optional<User> userOpt = loginUser(tfLogin.getText(), pfPassword.getText());
        if (userOpt.isPresent()) {
            if (userOpt.get().isStatus()) {
                try {
                    // obiekt aktualnie zalogowanego uzytkownika
                    loggedUser = userOpt.get();
                    // -----------------------------------------
                    lblInfo.setText("zalogowano");
                    WindowService windowService = new WindowService();
                    // utworzenie okna Pizza Portal
                    windowService.createWindow("pizzaPortalView", "Pizza Portal");
                    // zamkniecie aktualnego okna
                    windowService.closeWindow(lblInfo);
                    clearLoginProbes(userOpt.get());
                    FileService.updateUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                lblInfo.setText("Twoje konto jest zablokowane");
            }
        } else {
//            lblInfo.setText("błąd logowania");
            decrementProbes(tfLogin.getText());
            lblInfo.setText(getLoginProbes(tfLogin.getText()));
            try {
                FileService.updateUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    // metoda testujaca poprawnosc hasla - MOJA NIEUDANA PRÓBA
//    public String passwordValidator(String password) {
//        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_!.,#-])[^\\s\t]{4,}$";
//        // altenatywnie "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_!.,#-])(?=\\S+$).{8,}$"
//        return InMemoryDb.users.stream()                                                                    // stream
//                .filter(user -> user.getPassword().matches(regex))
//                .get();
//    }
}
