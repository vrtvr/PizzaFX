package service;

import controller.RegistrationController;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Role;
import model.User;
import utility.InMemoryDb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

// logika biznesowa rejestracji
public class RegistrationService {
    // metoda do generowania sprawdzenia czy jestes robotem
    public int generateRandomEquation(Label lblEquation) {
        Random random = new Random();
        int x1 = random.nextInt(10);
        int x2 = random.nextInt(10);
        lblEquation.setText(x1 + " + " + x2 + " = ");
        return x1 + x2;
    }

    public boolean isHuman(int userResult, int generatedResult) {
        return userResult == generatedResult;
    }

    public boolean isLoginCorrect(String login) {
        return login.length() >= 6;
    }

    public boolean isLoginUnique(String login) {
        return InMemoryDb.users.stream().noneMatch(user -> user.getLogin().equals(login));
    }

    public boolean isPasswordCorrect(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_!.,#-])[^\\s\\t]{8,}$");
    }

    public boolean isPasswordConfirmed(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }
// metoda wywolywana do rejestracji uzytkownika -> zapis w tablicy i pliku users
    public void registerUser(String login, String password) throws IOException {
        // utworz obiekt uzytkownika
        User user = new User(
                login,
                password,
                new HashSet<>(Arrays.asList(Role.ROLE_USER)),
                LocalDateTime.now(),
                true,
                3);
        // dodajemy obiekt do listy uzytkownikow
        InMemoryDb.users.add(user);
        // aktualizacja pliku
        System.out.println("MMM");
        FileService.updateUsers();
    }
    public void registration(TextField tfLogin, PasswordField pfPassword, PasswordField pfPasswordConfirmation,
                             Label lblInfo, TextField lblResult, Label lblEquation) {
        try{
            if (isHuman(Integer.valueOf(lblResult.getText()), RegistrationController.result)) {
                if (!isLoginCorrect(tfLogin.getText())) {
                    lblInfo.setText("niepoprawny login");
                } else if (!isLoginUnique(tfLogin.getText())) {
                    lblInfo.setText("istnieje już taki login w bazie");
                } else if (!isPasswordCorrect(pfPassword.getText())) {
                    lblInfo.setText("hasło musi zawierać (A-Z a-z 0-9 _!,.#- i mieć min. 8 znaków)");
                } else if (!isPasswordConfirmed(pfPassword.getText(),
                        pfPasswordConfirmation.getText())) {
                    lblInfo.setText("hasła nie są jednakowe");
                } else {
//                    System.out.println(tfLogin.getText());
//                    System.out.println(pfPassword.getText());
//                    System.out.println(pfPasswordConfirmation.getText());
                    registerUser(tfLogin.getText(), pfPassword.getText());
                    lblInfo.setText("");
                    lblResult.clear();
                    tfLogin.clear();
                    pfPassword.clear();
                    pfPasswordConfirmation.clear();
                }
                RegistrationController.result = generateRandomEquation(lblEquation);
                lblResult.clear();
            } else {
                lblInfo.setText("jesteś robotem!");
                // wygenerowanie nowego rownania i aktualizacja wyniku
                RegistrationController.result = generateRandomEquation(lblEquation);
                lblResult.clear();
            }
        // System.out.println(lblResult.getText());
        // System.out.println("Poprawny wynik równania: " + result);
        } catch (Exception e) {
            lblInfo.setText("błąd rejestracji");
        }
    }
}
