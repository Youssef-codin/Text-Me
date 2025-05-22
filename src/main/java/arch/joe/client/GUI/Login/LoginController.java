package arch.joe.client.GUI.Login;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import animatefx.animation.SlideInLeft;
import arch.joe.client.ChatClient;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.Messenger;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
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
    private Label errorField;
    @FXML
    private ImageView drawing;
    @FXML
    private ImageView papersBackground;

    private static boolean isAnimating = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Utils.clipToRounded(loginButton, 39, 39);
        Utils.clipToRounded(registerButton, 39, 39);

        animationForDrawings(papersBackground);
        animationForDrawings(drawing);
        scaleAnimations(drawing, 1.1);

        scaleAnimations(loginButton, 1.05);
        scaleAnimations(registerButton, 1.05);

        try {
            Utils.c = new ChatClient(new URI(
                    "ws://localhost:8025/text-me/chat"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Utils.c.connect();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            userField.requestFocus();
        });
    }

    public void login(ActionEvent e) throws Exception {
        String username = userField.getText();
        String password = passwordField.getText();

        int response = Utils.c.login(username, password);

        if (username.isEmpty() && password.isEmpty()) {
            return;
        }

        if (response == -1) {
            passwordField.getStyleClass().add("wrong-field");
            errorField.setText("Incorrect password.");
            errorField.setVisible(true);

        } else if (response == -2) {
            userField.getStyleClass().add("wrong-field");
            errorField.setText("Username not found.");
            errorField.setVisible(true);

        } else {
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
            Stage messengerStage = new Stage();
            Messenger messengerApp = new Messenger();
            messengerApp.start(messengerStage);

        }
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
}
