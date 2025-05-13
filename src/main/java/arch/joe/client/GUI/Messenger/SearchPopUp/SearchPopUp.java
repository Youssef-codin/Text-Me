package arch.joe.client.GUI.Messenger.SearchPopUp;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SearchPopUp {

    public static void showPopUp() throws Exception {

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

        FXMLLoader loader = new FXMLLoader(SearchPopUp.class.getResource("/arch/joe/client/UI/addContacts.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, Color.PINK);
        String css = SearchPopUp.class.getResource("/arch/joe/client/CSS/addContacts.css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}
