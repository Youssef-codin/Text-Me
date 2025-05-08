package arch.joe.client.GUI.Components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatBubble extends HBox {

    public ChatBubble(String msg, boolean isSender, String time) {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMinHeight(40);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setPrefWidth(USE_COMPUTED_SIZE);

        Label msgLabel = new Label(msg);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(7.0 * 35);
        msgLabel.setWrapText(true);
        msgLabel.setAlignment(Pos.TOP_LEFT);

        Text text = new Text();
        text.setFont(msgLabel.getFont());
        text.setWrappingWidth(250);
        double textHeight = text.getLayoutBounds().getHeight();
        msgLabel.setMinHeight(textHeight + 20);

        if (isSender) {
            this.setAlignment(Pos.CENTER_RIGHT);
            msgLabel.getStyleClass().add("sender-bubble");
        } else {
            this.setAlignment(Pos.CENTER_LEFT);
            msgLabel.getStyleClass().add("receiver-bubble");
        }

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("time-label");

        VBox msgBox = new VBox(3);

        msgBox.getChildren().addAll(msgLabel, timeLabel);
        this.getChildren().add(msgBox);
    }
}
