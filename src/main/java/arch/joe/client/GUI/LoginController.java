package arch.joe.client.GUI;

import java.net.URL;
import java.util.ResourceBundle;

import animatefx.animation.SlideInLeft;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private MFXButton loginButton;
    @FXML
    private MFXButton registerButton;
    @FXML
    private MFXTextField userField;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private ImageView drawing;
    @FXML
    private ImageView papersBackground;

    private static boolean isAnimating = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clipToPill(loginButton);
        clipToPill(registerButton);

        animationForDrawings(papersBackground);
        animationForDrawings(drawing);
        scaleAnimations(drawing, 1.1);

        scaleAnimations(loginButton, 1.05);
        scaleAnimations(registerButton, 1.05);

    }

    private void animationForDrawings(Node node) {

        SlideInLeft drawingAnim = new SlideInLeft(node);
        drawingAnim.setSpeed(0.6);
        drawingAnim.play();
        drawingAnim.setOnFinished(event1 -> {
            isAnimating = false;
        });
    }

    private void scaleAnimations(Node node, double scale) {
        scaleInNode(node, scale);
        scaleOutNode(node);

    }

    private void scaleInNode(Node node, double scale) {
        node.setOnMouseEntered(event -> {
            if (!isAnimating) {

                ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), node);
                scaleIn.setToX(scale);
                scaleIn.setToY(scale);
                scaleIn.setInterpolator(Interpolator.EASE_IN);
                scaleIn.play();
            }
        });
    }

    private void scaleOutNode(Node node) {
        node.setOnMouseExited(event -> {
            if (!isAnimating) {

                ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), node);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.setInterpolator(Interpolator.EASE_OUT);
                scaleOut.play();

            }
        });
    }

    public void login(ActionEvent e) {
        System.out.println("login");
    }

    private void clipToPill(Region button) {
        Rectangle clip = new Rectangle();
        button.setClip(clip);

        button.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            clip.setWidth(width);
            clip.setHeight(height);
            clip.setArcWidth(height);
            clip.setArcHeight(height);
        });
    }
}
