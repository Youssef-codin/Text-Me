package arch.joe.client.GUI;

import java.net.URL;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Components.ChatBubble;
import arch.joe.client.GUI.Components.ContactBox;
import de.jensd.fx.glyphs.materialicons.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
    @FXML
    private MFXButton searchButton;
    @FXML
    private MFXButton addButton;
    @FXML
    private MaterialIconView settingsIcon;
    @FXML
    private MaterialIconView groupIcon;
    @FXML
    private MaterialIconView logoutIcon;
    @FXML
    private MaterialIconView profileIcon;
    @FXML
    private MaterialIconView chatIcon;
    @FXML
    private Label currentUser;
    @FXML
    private VBox contactsView;
    @FXML
    private MFXScrollPane chatScroll;
    @FXML
    private VBox chatBox;
    @FXML
    private MFXTextField messageField;

    private static boolean isAnimating = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // MaterialIcon.
        // FontAwesomeIcon.u

        Utils.clipToRounded(sendButton, 20, 20);
        Utils.clipToRounded(shareButton, 20, 20);
        Utils.clipToRounded(emojiButton, 20, 20);

        Utils.clipToRounded(chatButton, 20, 20);
        Utils.clipToRounded(profileButton, 20, 20);
        Utils.clipToRounded(logoutButton, 20, 20);
        Utils.clipToRounded(groupButton, 20, 20);
        Utils.clipToRounded(settingsButton, 20, 20);

        Utils.clipToRounded(searchButton, 20, 20);
        Utils.clipToRounded(addButton, 20, 20);

        scaleAnimations(chatIcon, chatButton, 1.2);
        scaleAnimations(settingsIcon, settingsButton, 1.3);
        scaleAnimations(profileIcon, profileButton, 1.3);
        scaleAnimations(groupIcon, groupButton, 1.3);
        scaleAnimations(logoutIcon, logoutButton, 1.3);

        chatScroll.setFitToWidth(true);
        chatScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        chatScroll.setPannable(true);
        chatScroll.layout();
        chatScroll.setVvalue(1.0);
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > oldVal.doubleValue()) {
                // Height increased - new content added
                Platform.runLater(() -> Utils.animateScrollToBottom(chatScroll, 200));
            }
        });

        for (int i = 0; i <= 10; i++) {

            HBox contactItem = new ContactBox(new Contact("Dad", true,
                    "hi again did u do the groceries i told u to do? and the dishes? ah ofc u forgot u dumb bimbo",
                    "16/09/2024"));
            contactsView.getChildren().addAll(contactItem);
        }

    }

    public void sendMessage() {

        String msg = messageField.getText().trim();

        if (!msg.isEmpty()) {
            chatBox.getChildren().addAll(new ChatBubble(msg, true, "10:00 AM"));
            chatBox.getChildren().addAll(new ChatBubble(msg, false, "10:00 AM"));

            messageField.clear();

            // Platform.runLater(() -> {
            // Platform.runLater(() -> {
            // Utils.animateScrollToBottom(chatScroll, 150); // 150ms animation
            // });
            // });

        }
    }

    public void sendMessageEnter(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            sendMessage();
        }
    }

    // Animations
    private void scaleAnimations(Node animatedNode, Node triggerNode, double scale) {
        scaleInNode(animatedNode, triggerNode, scale);
        scaleOutNode(animatedNode, triggerNode);

    }

    private void scaleInNode(Node animatedNode, Node triggerNode, double scale) {
        triggerNode.setOnMouseEntered(event -> {
            if (!isAnimating) {

                ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), animatedNode);
                scaleIn.setToX(scale);
                scaleIn.setToY(scale);
                scaleIn.setInterpolator(Interpolator.EASE_BOTH);
                scaleIn.play();
            }
        });
    }

    private void scaleOutNode(Node animatedNode, Node triggerNode) {
        triggerNode.setOnMouseExited(event -> {
            if (!isAnimating) {

                ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), animatedNode);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.setInterpolator(Interpolator.EASE_BOTH);
                scaleOut.play();

            }
        });
    }
}
