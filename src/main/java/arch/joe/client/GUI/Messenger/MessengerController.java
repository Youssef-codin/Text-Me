package arch.joe.client.GUI.Messenger;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Components.ChatBubble;
import arch.joe.client.GUI.Components.ContactBox;
import arch.joe.db.UserDao;
import de.jensd.fx.glyphs.materialicons.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @FXML
    private MFXTextField searchField;

    private boolean isAnimating = false;

    private ArrayList<Contact> userContacts = new ArrayList<>(); // make smth that gets contacts
    private ArrayList<Contact> searchContacts = new ArrayList<>();

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

        contactsView.getChildren()
                .add(new ContactBox(new Contact("joe", "ur mama so fat she couldnt even eat the earch", "19/01/2021")));
    }

    public void addUser() {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Search Contacts");

        VBox searchPane = new VBox(15);
        searchPane.setPadding(new Insets(15));
        searchPane.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label("Add a Contact");
        titleLabel.setFont(Font.font("Inter Display SemiBold", 18));
        titleLabel.setTextFill(Color.web("#212121"));
        searchPane.getChildren().add(titleLabel);

        HBox searchBox = new HBox(10); // Spacing between search field and button
        searchBox.setAlignment(Pos.CENTER_LEFT);

        MFXTextField searchField = new MFXTextField();
        searchField.setPromptText("Search for contacts...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-font-size: 14px; -fx-background-radius: 10; -fx-border-color: #b0bec5;");

        MFXButton searchButton = new MFXButton("Search");
        searchButton.setStyle(
                "-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        searchButton.setPrefHeight(30);
        searchButton.setPrefWidth(80);

        searchBox.getChildren().addAll(searchField, searchButton);
        searchPane.getChildren().add(searchBox);

        VBox contactContainer = new VBox(10);

        contactContainer.getChildren()
                .add(new ContactBox(new Contact("joe", "ur mama so fat she couldnt even eat the earch", "19/01/2021")));
        contactContainer
                .setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");
        searchPane.getChildren().add(contactContainer);

        searchButton.setOnAction(event -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                contactContainer.getChildren().clear();
                ArrayList<Contact> searchResults = fetchContactsFromDatabase(searchTerm);
                for (Contact contact : searchResults) {
                    ContactBox con = new ContactBox(contact);
                    contactContainer.getChildren().add(con);
                }
            }
        });

        Scene popupScene = new Scene(searchPane, 350, 500);
        String css = this.getClass().getResource("/arch/joe/client/CSS/Messenger.css").toExternalForm();
        popupScene.getStylesheets().add(css);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private ArrayList<Contact> fetchContactsFromDatabase(String searchTerm) {
        return UserDao.searchUser(searchTerm);
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
