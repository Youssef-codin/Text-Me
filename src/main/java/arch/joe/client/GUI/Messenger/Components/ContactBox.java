package arch.joe.client.GUI.Messenger.Components;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class ContactBox extends HBox {

    private Contact contactInfo;
    private Label username;
    private Label lastMsg;
    private Label timeStamp;
    @SuppressWarnings("unused")
    private boolean isMessengerClass;

    public ContactBox(Contact contactInfo, boolean isMessengerClass) {
        this.contactInfo = contactInfo;

        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setMinSize(291, 62);
        this.prefWidth(291);
        this.prefHeight(62);
        this.setFocusTraversable(true);
        this.getStyleClass().add("contact-box");

        this.setOnMouseClicked(e -> {
            if (isMessengerClass) {
                Utils.mController.setFocusedBox(this);
            } else {
                Utils.sController.setFocusedBox(this);
            }
            this.requestFocus();

        });

        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE_ALT);
        userIcon.setFill(Paint.valueOf("#263238"));
        userIcon.setGlyphSize(40);

        username = new Label(contactInfo.getName());
        username.prefWidth(128);
        username.setMaxSize(128, 0);
        username.setStyle("-fx-font-family: 'Inter Display SemiBold'; -fx-font-size: 17px; -fx-text-fill: #212121;");

        lastMsg = new Label(contactInfo.getLastMsg());
        lastMsg.setMinSize(140, 0);
        lastMsg.setStyle(
                "-fx-font-family: 'Inter Display'; -fx-font-size: 13px; -fx-text-fill: #607d8b;-fx-background-color: transparent;");

        timeStamp = new Label(contactInfo.getTimeStamp());
        timeStamp.setAlignment(Pos.TOP_CENTER);
        timeStamp.setMinSize(70, 50);
        timeStamp.prefHeight(50);
        timeStamp.setPadding(new Insets(0, 0, 5, 0));
        timeStamp.setStyle(
                "-fx-font-family: 'Inter Display'; -fx-font-size: 12px; -fx-text-fill: #b0bec5;-fx-background-color: transparent;");

        VBox contactInfoBox = new VBox();
        contactInfoBox.setStyle("-fx-background-color: transparent; -fx-padding: 10 0 0 10;");
        contactInfoBox.setAlignment(Pos.TOP_LEFT);
        contactInfoBox.setMaxSize(217, 50);
        contactInfoBox.prefWidth(217);
        contactInfoBox.prefHeight(50);
        contactInfoBox.getChildren().addAll(username, lastMsg);

        this.getChildren().addAll(userIcon, contactInfoBox, timeStamp);
    }

    public Contact getContactInfo() {
        return contactInfo;
    }

    public void updateContact(Contact contact) {
        this.contactInfo = contact;

        username.setText(contactInfo.getName());
        if (lastMsg != null) {
            lastMsg.setText(contactInfo.getLastMsg());
            timeStamp.setText(contactInfo.getTimeStamp());

        }
    }
}
