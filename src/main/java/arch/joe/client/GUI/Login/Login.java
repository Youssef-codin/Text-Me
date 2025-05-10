package arch.joe.client.GUI.Login;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Login extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA) // Optional if you don't need JavaFX's default theme, still recommended
                                             // though
                .themes(MaterialFXStylesheets.forAssemble(true)) // Adds the MaterialFX's default theme. The boolean
                                                                 // argument is to include legacy controls
                .setDeploy(true) // Whether to deploy each theme's assets on a temporary dir on the disk
                .setResolveAssets(true) // Whether to try resolving @import statements and resources urls
                .build() // Assembles all the added themes into a single CSSFragment (very powerful class
                         // check its documentation)
                .setGlobal(); // Finally, sets the produced stylesheet as the global User-Agent stylesheet

        CSSFX.start();
        Parent root = FXMLLoader.load(getClass().getResource("/arch/joe/client/UI/Login.fxml"));
        Scene scene = new Scene(root, Color.PINK);
        String css = this.getClass().getResource("/arch/joe/client/CSS/Login.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
