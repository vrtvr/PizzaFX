package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.*;
import service.FileService;
import service.LoginService;
import service.PizzaPortalService;
import service.WindowService;
import utility.InMemoryDb;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PizzaPortalController {

    // obiekty globalne
    private WindowService windowService;
    private PizzaPortalService pizzaPortalService;
    private PizzaList pizzaOFDay;
    private Basket basket;
    // przechowuje liste pizz
    private ObservableList pizzas = FXCollections.observableArrayList();
    @FXML
    private Label lblLogin;
    @FXML
    private Tab tabMenu;
    @FXML
    private Tab tabBasket;
    @FXML
    private Tab tabAboutItaly;
    @FXML
    private Tab tabBasketStatus;
    @FXML
    private Tab tabEditMenu;
    // ----------------------------------- LIST PIZZA ----------------------------------
    @FXML
    private TableView<PizzaList> tblPizza;
    @FXML
    private TableColumn<PizzaList, String> tcName;
    @FXML
    private TableColumn<PizzaList, String> tcIngredients;
    @FXML
    private TableColumn<PizzaList, String> tcDescription;
    @FXML
    private TableColumn<PizzaList, Double> tcPrice;
    @FXML
    private TableColumn<PizzaList, Integer> tcQuantity;
    @FXML
    private Label lblPizzaOfDay;
    @FXML
    private Label lblSum;
    @FXML
    private ProgressBar pBar;
    // TAB 2 - BASKET --------------------------------------------------------------------
    @FXML
    private ListView<String> lvBasket;
    @FXML
    private TableView<Basket> tblBasket;
    @FXML
    private TableColumn<Basket, Map> tcBasket;
    @FXML
    private TableColumn<Basket, Status> tcStatus;
    @FXML
    private Label lblBasketAmount;
    // TAB 4 - BASKET STATUS --------------------------------------------------------------------
    @FXML
    private TableView<Basket> tblOrders;
    @FXML
    private TableColumn<Basket, String> tcLogin;
    @FXML
    private TableColumn<Basket, Map> tcOrder;
    @FXML
    private TableColumn<Basket, Status> tcStatusOrder;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private Spinner<Integer> sTime;
    @FXML
    private CheckBox cNew;
    @FXML
    private CheckBox cInProgress;
    @FXML
    private CheckBox cDelivered;
    @FXML
    private ImageView ivStatusConfirmation;
    // metoda dodajaca dane do tabelki
    private void addDataToOrderTable() {
        // konfiguracja kolumn
        tcLogin.setCellValueFactory(new PropertyValueFactory<>("userLogin"));
        tcOrder.setCellValueFactory(new PropertyValueFactory<>("order"));
        tcStatusOrder.setCellValueFactory(new PropertyValueFactory<>("status"));
        // edycja wyswietlania wartosci
        tcOrder.setCellFactory(order -> new TableCell<Basket, Map>() {
            @Override
            protected void updateItem(Map basket, boolean empty) {
                super.updateItem(basket, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(basket.toString()
                            .replace("{", "")
                            .replace("}", "")
                            .replace("=", " x")
                    );
                }
            }
        });
        tcStatusOrder.setCellFactory(order -> new TableCell<Basket, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(status.getStatusName());
                }
            }
        });
        tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
        // dodanie koszyka bez statusu dostarczone
//        tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets.stream()
//        .filter(basket1 -> !basket1.getStatus().equals(Status.DONE))
//        .collect(Collectors.toList())
//        ));
    }

    @FXML
    void ConfirmStatusAction(MouseEvent event) throws IOException {
        // pobranie statusu z listy rozwijalnej
        String statusName = cbStatus.getValue();
        // zmiana statusu wybranego obiektu na wybrany z listy rozwijanej
//        basket.setStatus(Status.valueOf(statusName)); - wyrzuca blad, ponizej prawidlowo
        InMemoryDb.baskets.stream().forEach(basket1 -> {
            if(basket1.equals(basket)){
                basket1.setStatus(Arrays.stream(Status.values())
                        .filter(status -> status.getStatusName().equals(statusName))
                        .findAny().get());
            }
        });
//        System.out.println(InMemoryDb.baskets);
//        ^ sprawdzenie kontrolnie na konsoli

        // okno alertowe potwierdzajace zmiane statusu
        WindowService.getAlertWindow(
                Alert.AlertType.INFORMATION,
                "Zmiana statusu zamówienia",
                "Zmieniono status zamówienia",
                "Status zamówienia: " + statusName);
        // aktualizacja tabelki
//        tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
//        tblBasket.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
        addDataToOrderTable();
        addDataToBasketsTable();
        // aktualizacja pliku
        FileService.updateBasket();
        // auto-mailing


    }
    @FXML
    void selectOrderAction(MouseEvent event) {
        basket = tblOrders.getSelectionModel().getSelectedItem();
        if(basket != null) {
            cbStatus.setDisable(false);
            sTime.setDisable(false);
            ivStatusConfirmation.setDisable(false);
            // pobranie aktualnego statusu i ustawienie go na combobox
            Status status = basket.getStatus();
            cbStatus.setValue(status.getStatusName());
        } else {
            cbStatus.setDisable(true);
            sTime.setDisable(true);
            ivStatusConfirmation.setDisable(true);
        }
    }
    @FXML
    void SelectDeliveredAction(ActionEvent event) {
//            if(cDelivered.isSelected()) {
//            List<Basket> deliveredOrders = InMemoryDb.baskets.stream()
//                    .filter(basket -> basket.getStatus().equals(Status.DONE))
//                    .collect(Collectors.toList());
//            tblOrders.setItems(FXCollections.observableArrayList(deliveredOrders));
//        } else {
//            tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
//        }
        selectCheckBox();
    }
    @FXML
    void SelectInProgressAction(ActionEvent event) {
//        if(cInProgress.isSelected()) {
//            List<Basket> inProgressOrders = InMemoryDb.baskets.stream()
//                    .filter(basket -> basket.getStatus().equals(Status.IN_PROGRESS))
//                    .collect(Collectors.toList());
//            tblOrders.setItems(FXCollections.observableArrayList(inProgressOrders));
//        } else {
//            tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
//        }
        selectCheckBox();
    }
    @FXML
    void SelectNewAction(ActionEvent event) {
//        if(cNew.isSelected()) {
//            List<Basket> newOrders = InMemoryDb.baskets.stream()
//                    .filter(basket -> basket.getStatus().equals(Status.NEW))
//                    .collect(Collectors.toList());
//            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
//        } else {
//            tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
//        }
        selectCheckBox();
    }
    private void selectCheckBox(){
//        List<Basket> filteredBaskets = InMemoryDb.baskets.stream()
//                .filter(basket1 -> !basket1.getStatus().equals(Status.DONE))
//                .collect(Collectors.toList());
        if(cInProgress.isSelected() && cNew.isSelected() && cDelivered.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW)
                            || basket.getStatus().equals(Status.IN_PROGRESS)
                            || basket.getStatus().equals(Status.DONE))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if (cNew.isSelected() && cDelivered.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW)
                            || basket.getStatus().equals(Status.DONE))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if (cInProgress.isSelected() && cDelivered.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.IN_PROGRESS)
                            || basket.getStatus().equals(Status.DONE))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if (cInProgress.isSelected() && cNew.isSelected()){
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW)
                            || basket.getStatus().equals(Status.IN_PROGRESS))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if(cInProgress.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.IN_PROGRESS))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if(cNew.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else if(cDelivered.isSelected()) {
            List<Basket> newOrders = InMemoryDb.baskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.DONE))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        }else {
            tblOrders.setItems(FXCollections.observableArrayList(InMemoryDb.baskets));
        }
    }
    private void clearOrder() {
        List<PizzaList> pizzaLists = pizzaPortalService.clearPizzaOrder();
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        // wyczyszczenie tabelki
        tblPizza.setItems(pizzas);          // aktualizacja tabelki
        lblSum.setText("do zapłaty: 0,00 zł");
    }

    @FXML
    void addToBasketAction(ActionEvent event) throws IOException {
        if (pizzaPortalService.calculatePizzaOrder() > 0) {
            pizzaPortalService.addOrderToBasket(LoginService.loggedUser.getLogin());
            clearOrder();
            WindowService.getAlertWindow(
                    Alert.AlertType.INFORMATION,
                    "Dodawanie do koszyka",
                    "Złożono zamówienie",
                    "Dziękujemy za złożenie zamówienia\nW zakładce koszyk możesz śledzić jego status");
            // aktualizacja tablicy koszykow
            addDataToBasketsTable();
            addDataToOrderTable();
        } else {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Dodawanie do koszyka");
//            alert.setHeaderText("Nie wybrano żadnego produktu");
//            alert.setContentText("Musisz wybrać jakiś produkt aby zrealizować zamówienie");
//            alert.show();                                               // zatrzymanie okna alertu na ekranie
            WindowService.getAlertWindow(
                    Alert.AlertType.WARNING,
                    "Dodawanie do koszyka",
                    "Nie wybrano żadnego produktu",
                    "Musisz wybrać jakiś produkt aby zrealizować zamówienie");
        }
    }


    @FXML
    void clearAction(ActionEvent event) {
        clearOrder();
    }

    @FXML
    void exitAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void logoutAction(ActionEvent event) throws IOException {
        windowService.createWindow("loginView", "panel logowania 2");       // otwarcie okna logowania
        windowService.closeWindow(lblLogin);                                                // zamkniecie aktualnie otwarteo okna
        clearOrder();
    }

    @FXML
    void selectPizzaAction(MouseEvent event) {
        List<PizzaList> pizzaLists = pizzaPortalService.selectPizza(tblPizza);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        // wyczyszczenie tabelki
        tblPizza.setItems(pizzas);      // aktualizacja tabelki
        // aktualizacja ceny do zapłaty
        lblSum.setText(String.format("do zapłaty: %.2f zł ", pizzaPortalService.calculatePizzaOrder()));
    }

    @FXML
    void showDetailsAction(MouseEvent event) {
        // zaznaczamy rekord w tabelce i pobieramy z niego obiekt klasy Basket
        Basket basket = tblBasket.getSelectionModel().getSelectedItem();
        // wypisanie szczegolowych informacji dot zaznaczonego koszyka
        ObservableList<String> detailBasket = FXCollections.observableArrayList();
        detailBasket.add("STATUS: " + basket.getStatus().getStatusName());
        for(String name : basket.getOrder().keySet()) {
            detailBasket.add("Pizza: " + name + " : " + basket.getOrder().get(name) + " szt.");

        }
        lvBasket.setItems(detailBasket);
        // aktualizacja kwoty do zaplaty
        lblBasketAmount.setText(String.format("SUMA: %.2f zł", basket.getBasketAmount()));
    }
    // metoda wprowadzajaca dane z pliku basket.csv do koszyka, ale dotyczace tylko zalogowanego uzytkownika
    private List<Basket> getUserBasket(String login) {
        // 1. z listy koszykow odfiltruj tylko dane dot. usera zidentyfikowanego po loginie
        List<Basket> userBaskets = InMemoryDb.baskets.stream()
                .filter(basket -> basket.getUserLogin().equals(login))
                .sorted(Comparator.comparing(Basket::getStatus))
                .collect(Collectors.toList());
        return userBaskets;
    }
    // metoda do konfiguracji kolumn w tblBasket i wprowadzenia danych dot. koszyków użytkownika
    private void addDataToBasketsTable(){
        // konfiguracja wartosci przekazywanych do kolumn z modelu
        tcBasket.setCellValueFactory(new PropertyValueFactory<>("order"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // formatowanie zawartosci tabeli
        tcBasket.setCellFactory(order -> new TableCell<Basket, Map>() {
            @Override
            protected void updateItem(Map basket, boolean empty) {
                super.updateItem(basket, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(basket.toString()
                    .replace("{", "")
                    .replace("}", "")
                    .replace("=", " x")
                    );
                }
            }
        });
        tcStatus.setCellFactory(order -> new TableCell<Basket, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(status.getStatusName());
                }
            }
        });
        // wprowadzenie danych do tabeli z listy baskets
        tblBasket.setItems(FXCollections.observableArrayList(
                getUserBasket(LoginService.loggedUser.getLogin())
        ));
    }

    public void initialize() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 101; i++) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double range = (double)i / 100;
                    pBar.setProgress(range);
                }
                Platform.exit();
//                try {
//                    logoutAction(new ActionEvent());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
        thread.start();
        // zarzadzanie widocznosia tabow w zaleznosci od roli
        Set<Role> roles = LoginService.loggedUser.getRoles();
        System.out.println(roles);
        if(!roles.contains(Role.ROLE_USER)) {
            tabMenu.setDisable(true);
            tabBasket.setDisable(true);
            tabAboutItaly.setDisable(true);
        }
        if(!roles.contains(Role.ROLE_ADMIN)) {
            tabBasketStatus.setDisable(true);
            tabEditMenu.setDisable(true);
        }
        // wypisanie loginu zalogowanego uzytkownika na lbl
        lblLogin.setText("zalogowano: " + LoginService.loggedUser.getLogin());
        pizzaPortalService = new PizzaPortalService();          // nowa instancja klasy PPS
        // mapowanie enuma na PizzaList
        PizzaPortalService.mapPizzaToPizzaList();
        pizzas.clear();
        pizzas.addAll(InMemoryDb.pizzaLists);
//        System.out.println(pizzas.size());
        windowService = new WindowService();
        // generowanie pizzy dnia, aktualizacja ceny i wypisanie na lbl
        pizzaOFDay = pizzaPortalService.generatePizzaOfDay();
        List<PizzaList> pizzaLists = pizzaPortalService.setDiscount(pizzaOFDay, 30);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        lblPizzaOfDay.setText("PIZZA DNIA TO " + pizzaOFDay.getName().toUpperCase() + " !!!");
        // konfiguracja wartosci wprowadzanych do kolumn tblPizza
        tcName.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("name"));
        tcIngredients.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("ingredients"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("description"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<PizzaList, Double>("price"));
        tcQuantity.setCellValueFactory(new PropertyValueFactory<PizzaList, Integer>("quantity"));
        // formatownie do typu NumberFormat
        Locale locale = new Locale("pl", "PL");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        tcPrice.setCellFactory(tc -> new TableCell<PizzaList, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
        // wprowadzenie wartosci do tbl
        tblPizza.setItems(pizzas);
        // TAB 2 - BASKET --------------------------------------------------------------------
        addDataToBasketsTable();
        // TAB 4 - BASKET STATUS -------------------------------------------------------------
        addDataToOrderTable();
        // wporwadzenie danych do combobox
        cbStatus.setItems(FXCollections.observableArrayList(
                Arrays.stream(Status.values()).map(Status::getStatusName).collect(Collectors.toList())
        ));
        // wprowadzenie danych do spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 150, 30, 5);
        sTime.setValueFactory(valueFactory);
    }


}
