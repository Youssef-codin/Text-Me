package arch.joe.client.GUI;

import arch.joe.app.Contact;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class ContactBox extends HBox {

    public ContactBox(Contact contactInfo) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle(
                "-fx-background-color: #FAFAFA; -fx-padding: 0 10 10; -fx-border-color: transparent transparent #B0BEC5 transparent;");
        this.setSpacing(10);
        this.setMinSize(307, 62);
        this.prefWidth(307);
        this.prefHeight(62);

        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE_ALT);
        userIcon.setFill(Paint.valueOf("#263238"));
        userIcon.setGlyphSize(40);

        Label username = new Label(contactInfo.getName());
        username.prefWidth(128);
        username.setMaxSize(128, 0);
        username.setStyle("-fx-font-family: 'Inter Display SemiBold'; -fx-font-size: 17px; -fx-text-fill: #212121;");

        Label lastMsg = new Label(contactInfo.getLastMsg());
        lastMsg.setMaxSize(160, 0);
        lastMsg.prefWidth(160);
        lastMsg.setStyle("-fx-font-family: 'Inter Display'; -fx-font-size: 13px; -fx-text-fill: #607d8b;");

        Label timeStamp = new Label(contactInfo.getTimeStamp());
        timeStamp.setAlignment(Pos.TOP_RIGHT);
        timeStamp.setMinSize(94, 50);
        timeStamp.prefWidth(94);
        timeStamp.prefHeight(50);
        timeStamp.setStyle("-fx-font-family: 'Inter Display'; -fx-font-size: 12px; -fx-text-fill: #b0bec5;");

        VBox contactInfoBox = new VBox();
        contactInfoBox.setStyle("-fx-background-color: transparent; -fx-padding: 10 0 0 10;");
        contactInfoBox.setAlignment(Pos.TOP_LEFT);
        contactInfoBox.setMaxSize(192, 50);
        contactInfoBox.prefWidth(192);
        contactInfoBox.prefHeight(50);
        contactInfoBox.getChildren().addAll(username, lastMsg);

        this.getChildren().addAll(userIcon, contactInfoBox, timeStamp);
    }
}
