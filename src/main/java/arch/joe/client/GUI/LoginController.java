package arch.joe.client.GUI;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class LoginController implements Initializable {

    @FXML
    private MFXButton loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clipToPill(loginButton);
    }

    public void login(ActionEvent e) {
        System.out.println("login");
    }

    private void clipToPill(Region button) {
        Rectangle clip = new Rectangle();
        button.setClip(clip);

        button.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            clip.setWidth(width);
            clip.setHeight(height);
            clip.setArcWidth(height);
            clip.setArcHeight(height);
        });
    }
}
