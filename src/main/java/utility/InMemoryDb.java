package utility;

import model.Basket;
import model.PizzaList;
import model.Role;
import model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InMemoryDb {
    public static List<User> users = new ArrayList<>(
//            Arrays.asList(
////                    new User("m",
////                            "m",
////                            new HashSet<>(Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_USER)),
////                            LocalDateTime.now(),
////                            true,
////                            3
////                    ),
////                    new User("k",
////                            "k",
////                            new HashSet<>(Arrays.asList(Role.ROLE_USER)),
////                            LocalDateTime.now(),
////                            true,
////                            2
////                    )
//            )
    );
    public static List<PizzaList> pizzaLists = new ArrayList<>(
//            Arrays.asList(new PizzaList (
//                    "Test", "Test1,Test2,Test3", "nijaka", 15.0, 0))
    );
    public static List<Basket> baskets = new ArrayList<>();
}
