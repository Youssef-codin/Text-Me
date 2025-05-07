package arch.joe.client.GUI;

import arch.joe.app.Contact;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

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
}
