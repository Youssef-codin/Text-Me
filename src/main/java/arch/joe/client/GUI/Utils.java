package arch.joe.client.GUI;

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
}
