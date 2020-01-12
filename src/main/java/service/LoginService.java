package service;

import model.User;
import utility.InMemoryDb;

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
}
