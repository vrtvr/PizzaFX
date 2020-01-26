package service;

import model.Basket;
import model.Role;
import model.Status;
import model.User;
import utility.InMemoryDb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// logika biznesowa do obslugi plikow
public class FileService {
    // sciezka bezposrednia do pliku users.csv
    // Paths.get("").toAbsolutePath().toString() - generowanie sciezki bezposredniej do katalogu glownego projektu -> PizzaPortal
    private static String pathToUsers =
            Paths.get("").toAbsolutePath().toString()+"\\src\\main\\resources\\file\\users.csv";
    private static String pathToBaskets =
            Paths.get("").toAbsolutePath().toString()+"\\src\\main\\resources\\file\\baskets.csv";
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // metoda pobierajaca zawartosc z pliku i wprowadzajaca ja do listy users
    public static void selectUsers () throws FileNotFoundException {
        File file = new File(pathToUsers);
        Scanner scanner = new Scanner(file);
// alternatywnie do scannera: FileReader fileReader = new FileReader(file);
        while (scanner.hasNext()) {
            String user = scanner.nextLine();                               // pobrana jedna linijka z pliku
            String [] userSplitBySeparator = user.split("; ");
            // wpisanie zawartosci pobranej z pliku do listy podrecznej users
            InMemoryDb.users.add(new User(
                    userSplitBySeparator[0],                                // login
                    userSplitBySeparator[1],                                // password
                    new HashSet<>(                                          // konwersja do Set<Role>
                            Arrays.stream(userSplitBySeparator[2].split(","))
                            .map(Role::valueOf)
                            .collect(Collectors.toSet())),
                    LocalDateTime.parse(userSplitBySeparator[3], dtf),      // parsowanie do LocalDateTime
                    Boolean.valueOf(userSplitBySeparator[4]),               // konwersja do boolean
                    Integer.valueOf(userSplitBySeparator[5]))               // konwersja do Integer
                    );
        }
        scanner.close();

    }
    // metoda zapisujaca zawartosc z listy users do pliku
    public static void updateUsers () throws IOException {
        FileWriter fileWriter = new FileWriter (new File(pathToUsers));
        for (User u : InMemoryDb.users) {
            fileWriter.write(String.format("%s; %s; %s; %s; %s; %d\n",
                    u.getLogin(),
                    u.getPassword(),
                    u.getRoles().stream().map(Enum::name).collect(Collectors.joining(",")),
                    u.getRegistrationDateTime().format(dtf),
                    u.isStatus(),
                    u.getProbes())
            );
        }
        fileWriter.close();
    }
    // metoda do zapisu i odczytu zamówienia w oparciu o plik baskets.csv
    public static void updateBasket() throws IOException {
        FileWriter fileWriter = new FileWriter (new File(pathToBaskets));           // obiekt do zapiu pliku o adresie URL jak w pathToBaskets
        Locale locale = Locale.ENGLISH;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);         // wymuszenie kropki
        numberFormat.setMinimumFractionDigits(2);                                   // wymuszenie min 2 liczb po przecinku
        numberFormat.setMaximumFractionDigits(2);                                   // wymuszenie max 2 liczb po przecinku
        for (Basket basket : InMemoryDb.baskets) {
            fileWriter.write(
                    String.format("%s; %s; %s; %s\n",
                            basket.getUserLogin(),
                            basket.getOrder().toString()
                                    .replace("{","")
                                    .replace("}",""),                   // usuniecie nawiasow {}
                            numberFormat.format(basket.getBasketAmount()).replace(",", ""),
                            basket.getStatus().getStatusName()
                            ));                                                            // przepisanie zamowien z listy do pliku baskets.csv
        }
        fileWriter.close();
    }
    // metoda pobierajaca zawartosc z pliku i wpisujaca do listy baskets
    public static void selectBaskets() throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(pathToBaskets));
        // odczyt zawartosci linijka po linijce z pliku baskets.csv
        while (scanner.hasNext()){
            String [] line = scanner.nextLine().split("; ");                // kazda linijka pliku jest dzielona po ";
                                                                                   // co daje nam tablice String[4]
            Map<String, Integer> order = new HashMap<>();                          // mapa zamowien
            for (String element : line[1].split(", ")) {                    // kazde zamowienie w mapie jest dzielone po ", "
                String[] key_value = element.split("=");                    // pojedyncze zamowienie to key=value
                order.put(key_value[0], Integer.valueOf(key_value[1]));            // dodanie zamowienia do mapy
            }
            // w pliku "do realizacji"
            // Enum -> NEW("nowe zamówienie")
            InMemoryDb.baskets.add(
                    new Basket(
                            line[0],
                            order,
                            Double.valueOf(line[2]),
                            Arrays.stream(Status.values())                                  // lista statusow
                            .filter(status -> status.getStatusName().equals(line[3]))       // filtrujemy po nazwie
                            .findAny().get()                                                // bierzemy pierwszy obiekt Status znaleziony
                    )
            );
        }
        scanner.close();
    }
}
