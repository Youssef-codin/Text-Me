package arch.joe.client.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Utils {

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

    public static void addHoverAnimation(Region button) {

    }
}
