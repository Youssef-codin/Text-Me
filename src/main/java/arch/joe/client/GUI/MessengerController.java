package arch.joe.client.GUI;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialicons.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MessengerController implements Initializable {

    @FXML
    private MFXButton sendButton;
    @FXML
    private MFXButton emojiButton;
    @FXML
    private MFXButton shareButton;
    @FXML
    private MFXButton chatButton;
    @FXML
    private MFXButton profileButton;
    @FXML
    private MFXButton logoutButton;
    @FXML
    private MFXButton groupButton;
    @FXML
    private MFXButton settingsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // MaterialIcon.SETTINGS_CELL
        // FontAwesomeIcon.gear

        Utils.clipToRounded(sendButton, 20, 20);
        Utils.clipToRounded(shareButton, 20, 20);
        Utils.clipToRounded(emojiButton, 20, 20);
        Utils.clipToRounded(chatButton, 20, 20);
        Utils.clipToRounded(profileButton, 20, 20);
    }
}
