package service;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import model.*;
import utility.InMemoryDb;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PizzaPortalService {
    // metoda mapujaca enum Pizza na liste PizzaList
    public static void mapPizzaToPizzaList() {
        List<PizzaList> pizzas = Arrays.stream(Pizza.values())                            // tablica wartosci z enum Pizza [ ]
                .map(pizza -> new PizzaList(
                        pizza.getName(),                                                            // pobranie nazwy
                        pizza.getIngredients().stream()
                                .map(ingredient -> ingredient.getName())
                                .collect(Collectors.joining(", ")),                         // zwrocenie skladnikow z sep-','
                        (pizza.getIngredients().stream()
                                .anyMatch(ingredient -> ingredient.isSpicy()) ? "ostra " : "")
                        +
                        (pizza.getIngredients().stream()
                                .noneMatch(ingredient -> ingredient.isMeat()) ? "wege" : ""),       // zwrocenie opisu ostra i wege
                        pizza.getIngredients().stream()
                                .mapToDouble(Ingredient::getPrice)
                                .sum(),                                                             // obliczenie ceny skladnikow
                        0
                        )
                )
                .collect(Collectors.toList());
        InMemoryDb.pizzaLists = new ArrayList<>();
        InMemoryDb.pizzaLists.addAll(pizzas);
    }
    // metoda wyboru pizzy z TableView
    public List<PizzaList> selectPizza(TableView tblPizza) {
        PizzaList selectedPizza = (PizzaList) tblPizza.getSelectionModel().getSelectedItem();
        if(selectedPizza != null) {
            // TextInputDialog dialog = new TextInputDialog(selectedPizza.getQuantity().toString());
            TextInputDialog dialog = new TextInputDialog(
                    // domyslna ilosc wprowadzona w TextInput
                    selectedPizza.getQuantity() > 0 ? selectedPizza.getQuantity().toString() : "1"
            );
            dialog.setTitle("Wybierz ilość");
            dialog.setHeaderText("Podaj ilość zamawianego produktu");
            dialog.setContentText("Aby zamówić określoną ilość produktu, \nnależy ją wprowadzić do pola tekstowego:");
            Optional<String> result = dialog.showAndWait();
//            if (result.isPresent()){
//                System.out.println("Wprowadź ilość: " + result.get());
//            }

            // jesli wprowadzilismy wartosc to aktualizujemy ja w liscie pizz
//            result.ifPresent(quantity -> System.out.println("Wybrałeś: " + quantity));
            result.ifPresent(quantity -> InMemoryDb.pizzaLists.stream()
                    .filter(pizza -> pizza.equals(selectedPizza))
                    .forEach(pizza -> pizza.setQuantity(Integer.valueOf(result.get())))
            );
        }
//        tblPizza.setItems(null);                                                            // wyczyszczenie tabelki
//        tblPizza.setItems(FXCollections.observableArrayList(InMemoryDb.pizzaLists));        // aktualizacja tabelki
        return  InMemoryDb.pizzaLists;
    }
    // metoda do obliczania ceny zamowionych pizz
    public Double calculatePizzaOrder() {
        return InMemoryDb.pizzaLists.stream()
                .mapToDouble(pizza -> pizza.getQuantity() * pizza.getPrice())
                .sum();
    }
    // czyszczenie zamowienia
    public List<PizzaList> clearPizzaOrder() {
        InMemoryDb.pizzaLists.stream().forEach(pizza -> pizza.setQuantity(0));
        return InMemoryDb.pizzaLists;
    }
    // generator pizzy dnia
    public PizzaList generatePizzaOfDay(){
        return InMemoryDb.pizzaLists.get(new Random().nextInt(InMemoryDb.pizzaLists.size()));
    }
    // przypisanie rabatu 30% na pizze dnia
    public List<PizzaList> setDiscount (PizzaList pizza, double discount) {
        InMemoryDb.pizzaLists.stream()
                .filter(pizzaList -> pizzaList.equals(pizza))
                .forEach(pizzaList -> pizzaList.setPrice(pizzaList.getPrice() * (1 - (discount/100))));
        return InMemoryDb.pizzaLists;
    }
    // metoda przekazujaca dane do koszyka
    // loginUsera; listaPizz -> quantity > 0; cena do zaplaty
    public void addOrderToBasket(String userLogin) throws IOException {
        List<PizzaList> pizzaLists = InMemoryDb.pizzaLists.stream()
                .filter(pizza -> pizza.getQuantity() > 0)
                .collect(Collectors.toList());
        Double toPay = calculatePizzaOrder();
        System.out.println(String.format("%s; %s; %.2f; %s",
            userLogin,
            pizzaLists.stream()
                .map(pizzaList -> pizzaList.getName() + ": " + pizzaList.getQuantity())
                .collect(Collectors.joining(", ")),
            toPay,
            "nowe zamówienie"
            )
        );
        // zapis zamowienia w podrecznym koszyku
        Map<String, Integer> order = new HashMap<>();
        for(PizzaList pizzaList : pizzaLists) {
            if(pizzaList.getQuantity() > 0) {                               // wprowadzam tylko te pizze, ktore sa zamowione w ilosci >0
                order.put(pizzaList.getName(), pizzaList.getQuantity());
            }
        }
        InMemoryDb.baskets.add(new Basket(
                userLogin,
                order,
                toPay,
                Status.NEW));
        // aktualizacja pliku w oparciu o InMemoryDB.baskets
        FileService.updateBasket();
    }

}
