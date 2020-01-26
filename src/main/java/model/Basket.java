package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class Basket {
    private String userLogin;
    private Map<String, Integer> order;         // mapa ktora bedzie przechowywacszczegoly zamowienia tj. nazwaPizzy : ilosc
    private double basketAmount;
    private Status status;
}
