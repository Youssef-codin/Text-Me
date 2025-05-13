package arch.joe.client.GUI.Messenger;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import animatefx.animation.SlideInRight;
import arch.joe.app.Msg;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MessengerController implements Initializable {

    @FXML
    private HBox anchorHbox;
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
    private Label currentReceiver;
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

    Stage stage;

    private boolean isAnimating = false;

    // name, box
    private static LinkedHashMap<String, ContactBox> userContacts = new LinkedHashMap<>(); // TODO: save to file and get
                                                                                           // it from there

    private static ContactBox focusedBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // MaterialIcon.
        // FontAwesomeIcon.u

        Utils.mController = this;

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

        sendButton.setDisable(true);
    }

    public void updateContactsView(ContactBox contactBox) {
        if (!userContacts.containsKey(contactBox.getContactInfo().getName())) {
            userContacts.put(contactBox.getContactInfo().getName(), contactBox);
            contactsView.getChildren().clear();
            contactsView.getChildren().addAll(userContacts.values());
            contactsView.layout();
            SlideInRight anim = new SlideInRight(contactBox);
            anim.setSpeed(1.0);
            anim.play();

        }
    }

    // buttons
    public void sendMessage() {

        String msg = messageField.getText().trim();

        if (!msg.isEmpty()) {
            chatBox.getChildren().addAll(new ChatBubble(msg, true, "10:00 AM"));

            messageField.clear();
        }
    }

    public void addUser() throws Exception {
        SearchPopUp.showPopUp();
    }

    public void searchUserContacts() {

        String searchTerm = searchField.getText();
        contactsView.getChildren().clear();

        if (searchTerm.isEmpty()) {
            contactsView.getChildren().addAll(userContacts.values());

        } else {
            for (String name : userContacts.keySet()) {
                if (name.contains(searchTerm)) {
                    contactsView.getChildren().add(userContacts.get(name));
                }
            }
        }
    }

    public void logout() {
        stage = (Stage) anchorHbox.getScene().getWindow();
        stage.close();
    }

    // misc
    public LinkedHashMap<String, ContactBox> getUserContacts() {
        return userContacts;
    }

    public void setFocusedBox(ContactBox box) {
        if (focusedBox == null) {
            sendButton.setDisable(false);
            box.getStyleClass().set(0, "focused-contact-box");
            Utils.c.setCurrentReceiver(box.getContactInfo().getName());
            focusedBox = box;

        } else {
            focusedBox.getStyleClass().set(0, "contact-box");
            box.getStyleClass().set(0, "focused-contact-box");
            Utils.c.setCurrentReceiver(box.getContactInfo().getName());
            focusedBox = box;
        }

        String name = box.getContactInfo().getName();
        Platform.runLater(() -> {
            currentReceiver.setText(name);
            try {
                getMsgHistory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getMsgHistory() throws Exception {
        ArrayList<Msg> history = Utils.c.msgHistory();
        chatBox.getChildren().clear();
        if (!history.isEmpty()) {
            for (Msg msg : history) {
                if (msg.getMsgSender().equals(Utils.c.getUsername())) {
                    chatBox.getChildren().add(new ChatBubble(msg.getMsg(), true, Utils.timeFormat(msg.msgTime())));
                } else {
                    chatBox.getChildren().add(new ChatBubble(msg.getMsg(), false, Utils.timeFormat(msg.msgTime())));
                }
            }
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
