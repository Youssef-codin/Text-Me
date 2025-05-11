package arch.joe.client.GUI.Messenger;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.Components.ChatBubble;
import arch.joe.client.GUI.Messenger.Components.ContactBox;
import arch.joe.client.GUI.Messenger.SearchPopUp.SearchPopUp;
import de.jensd.fx.glyphs.materialicons.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    public VBox contactsView;
    @FXML
    private MFXScrollPane chatScroll;
    @FXML
    private VBox chatBox;
    @FXML
    private MFXTextField messageField;
    @FXML
    private MFXTextField searchField;

    private boolean isAnimating = false;

    private static LinkedHashSet<ContactBox> userContacts = new LinkedHashSet<>(); // make smth that gets contacts

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
                Platform.runLater(() -> Utils.animateScrollToBottom(chatScroll, 200));
            }
        });
    }

    public void addUser() throws Exception {
        SearchPopUp.showPopUp(this);
    }

    public void updateContactsView(ContactBox contactBox) {
        userContacts.add(contactBox);
        for (ContactBox box : userContacts) {
            System.out.println(box.getContactInfo().getName());
        }
        contactsView.getChildren().clear();
        contactsView.getChildren().addAll(userContacts);
        contactsView.layout();

    }

    public void sendMessage() {

        String msg = messageField.getText().trim();

        if (!msg.isEmpty()) {
            chatBox.getChildren().addAll(new ChatBubble(msg, true, "10:00 AM"));
            chatBox.getChildren().addAll(new ChatBubble(msg, false, "10:00 AM"));

            messageField.clear();
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
