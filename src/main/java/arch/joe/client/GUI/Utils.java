package arch.joe.client.GUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import arch.joe.client.ChatClient;
import arch.joe.client.GUI.Messenger.MessengerController;
import arch.joe.client.GUI.Messenger.SearchPopUp.SearchPopUpController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Utils {

    private Utils() {

    }

    public static final Path TEXT_ME_PATH = Paths.get(System.getProperty("user.home"), "Documents", "Text-me");

    public static MessengerController mController;
    public static SearchPopUpController sController;
    public static ChatClient c;

    public static void clipToRounded(Region region, double arcWidth, double arcHeight) {
        Rectangle clip = new Rectangle();
        region.setClip(clip);

        region.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
            clip.setArcWidth(arcWidth);
            clip.setArcHeight(arcHeight);
        });

    }

    public static void animateScrollToBottom(ScrollPane scrollPane, int duration) {
        double targetVvalue = 1.0;
        double startVvalue = scrollPane.getVvalue();

        if (Math.abs(targetVvalue - startVvalue) > 0.01) {
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(scrollPane.vvalueProperty(),
                    targetVvalue,
                    Interpolator.EASE_OUT);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(duration), keyValue);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        } else {
            scrollPane.setVvalue(1.0);
        }
    }

    public static String timeFormat(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return dateTime.format(formatter);

    }

    public static void createFolder() throws IOException {
        if (!Files.exists(TEXT_ME_PATH)) {
            Files.createDirectories(TEXT_ME_PATH);

        }
    }
}
