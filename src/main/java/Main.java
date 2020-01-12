import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/loginView.fxml"));
        primaryStage.setTitle("panel logowania");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);                   // blokowanie zmiany rozmiaru okna
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
