package arch.joe.client.GUI;

import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

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
}
