package arch.joe.client.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Messenger extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // CSSFX.start();
        Parent root = FXMLLoader.load(getClass().getResource("/arch/joe/client/UI/Messenger.fxml"));
        Scene scene = new Scene(root, Color.PINK);
        String css = this.getClass().getResource("/arch/joe/client/CSS/Messenger.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}
