package arch.joe.client.GUI;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Login extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        CSSFX.start();
        Parent root = FXMLLoader.load(getClass().getResource("/arch/joe/client/UI/Login.fxml"));
        Scene scene = new Scene(root, Color.PINK);
        String css = this.getClass().getResource("/arch/joe/client/CSS/Login.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
