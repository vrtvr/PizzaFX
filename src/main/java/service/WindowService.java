package service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

// klasa do obslugi okien aplikacji
public class WindowService {
    // metoda do otwierania nowego okna aplikacji
    public void createWindow(String viewName, String title) throws IOException {
        Stage stage = new Stage();                                                       // utworzenie obiektu okna
        Parent root = FXMLLoader.load(getClass().getResource("/view/"+viewName+".fxml"));
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setResizable(false);                                               // blokowanie zmiany rozmiaru okna
        stage.show();
    }
    // metoda do zamykania aktualnie otwartego okna aplikacji
    public void closeWindow(Node node) {
        // Node klasa po ktorej dziedzicza wszystkie kontrolki
        Stage stageToClose = (Stage) node.getScene().getWindow();
        stageToClose.close();
    }
    // uniwerslana metoda do generowania okien alertu typu: Information, Warning, Error, None
    public static void getAlertWindow(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
