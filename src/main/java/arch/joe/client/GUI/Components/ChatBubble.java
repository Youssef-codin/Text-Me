package arch.joe.client.GUI.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class ChatBubble extends HBox {

    public ChatBubble(String msg, boolean isSender, String time) {
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setPrefWidth(USE_COMPUTED_SIZE);

        Label messageLabel = new Label(msg);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(250);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("time-label");

        StackPane bubble = new StackPane();
        bubble.getChildren().add(messageLabel);
        bubble.setPadding(new Insets(8, 12, 8, 12));

        if (isSender) {
            this.setAlignment(Pos.CENTER_RIGHT);
            bubble.getStyleClass().add("sender-bubble");
            messageLabel.setTextAlignment(TextAlignment.LEFT);
        } else {
            this.setAlignment(Pos.CENTER_LEFT);
            bubble.getStyleClass().add("receiver-bubble");
            messageLabel.setTextAlignment(TextAlignment.LEFT);
        }

        this.getChildren().add(bubble);
    }
}
