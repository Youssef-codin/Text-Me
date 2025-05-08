package arch.joe.client.GUI.Components;

import arch.joe.app.Contact;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class ContactBox extends HBox {

    private Contact contactInfo;

    public ContactBox(Contact contactInfo) {
        this.contactInfo = contactInfo;

        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setMinSize(291, 62);
        this.prefWidth(291);
        this.prefHeight(62);
        this.setFocusTraversable(true);
        this.getStyleClass().add("contact-box");

        this.setOnMouseClicked(e -> contactAction());

        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE_ALT);
        userIcon.setFill(Paint.valueOf("#263238"));
        userIcon.setGlyphSize(40);

        Label username = new Label(contactInfo.getName());
        username.prefWidth(128);
        username.setMaxSize(128, 0);
        username.setStyle("-fx-font-family: 'Inter Display SemiBold'; -fx-font-size: 17px; -fx-text-fill: #212121;");

        Label lastMsg = new Label(contactInfo.getLastMsg());
        lastMsg.setMaxSize(170, 0);
        lastMsg.prefWidth(170);
        lastMsg.setStyle("-fx-font-family: 'Inter Display'; -fx-font-size: 13px; -fx-text-fill: #607d8b;");

        Label timeStamp = new Label(contactInfo.getTimeStamp());
        timeStamp.setAlignment(Pos.TOP_RIGHT);
        timeStamp.setMinSize(118, 50);
        timeStamp.prefWidth(118);
        timeStamp.prefHeight(50);
        timeStamp.setStyle("-fx-font-family: 'Inter Display'; -fx-font-size: 12px; -fx-text-fill: #b0bec5;");

        VBox contactInfoBox = new VBox();
        contactInfoBox.setStyle("-fx-background-color: transparent; -fx-padding: 10 0 0 10;");
        contactInfoBox.setAlignment(Pos.TOP_LEFT);
        contactInfoBox.setMaxSize(217, 50);
        contactInfoBox.prefWidth(217);
        contactInfoBox.prefHeight(50);
        contactInfoBox.getChildren().addAll(username, lastMsg);

        this.getChildren().addAll(userIcon, contactInfoBox, timeStamp);
    }

    private void contactAction() {
        this.requestFocus();
    }

    public Contact getContactInfo() {
        return contactInfo;
    }
}
