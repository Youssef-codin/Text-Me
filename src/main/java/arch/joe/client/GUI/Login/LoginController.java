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
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private MFXButton loginButton, registerButton, signUpButton, backToLogin;
    @FXML
    private MFXTextField userField, emailField, rUserField;
    @FXML
    private MFXPasswordField passwordField, rPasswordField;
    @FXML
    private Label errorField, rErrorField;
    @FXML
    private ImageView drawing, papersBackground;
    @FXML
    private Pane loginPane, drawingPane, registerPane;

    private static boolean isAnimating = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Utils.clipToRounded(loginButton, 39, 39);
        Utils.clipToRounded(registerButton, 39, 39);
        Utils.clipToRounded(backToLogin, 39, 39);
        Utils.clipToRounded(signUpButton, 39, 39);

        introAnimDrawings(papersBackground);
        introAnimDrawings(drawing);
        drawingScaleIn();
        drawingScaleOut();

        scaleAnimations(loginButton, 1.05);
        scaleAnimations(registerButton, 1.05);
        scaleAnimations(signUpButton, 1.05);
        scaleAnimations(backToLogin, 1.05);

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
            setTravesable(true);
        });

    }

    public void login(ActionEvent e) throws Exception {
        String username = userField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() && password.isEmpty()) {
            return;
        }

        int response = Utils.c.login(username, password);

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

    public void register() {
        double a = 0.86;
        double b = 0;
        double c = 0.07;
        double d = 1;
        int speed = 1000;

        TranslateTransition tt = new TranslateTransition(Duration.millis(speed), drawingPane);
        tt.setByX(428);
        tt.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt.play();

        TranslateTransition tt2 = new TranslateTransition(Duration.millis(speed), loginPane);
        tt2.setByX(428);
        tt2.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt2.play();

        TranslateTransition tt3 = new TranslateTransition(Duration.millis(speed), registerPane);
        tt3.setByX(428);
        tt3.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt3.play();

        signUpButton.setDefaultButton(true);
        loginButton.setDefaultButton(false);

        setTravesable(false);

    }

    private void introAnimDrawings(Node node) {

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

    private void drawingScaleIn() {

        drawing.setOnMouseEntered(event -> {
            if (!isAnimating) {

                ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), drawing);
                scaleIn.setToX(1.1);
                scaleIn.setToY(1.1);
                scaleIn.setInterpolator(Interpolator.EASE_IN);
                scaleIn.play();

                ScaleTransition scaleIn1 = new ScaleTransition(Duration.seconds(0.3), papersBackground);
                scaleIn1.setToX(1.1);
                scaleIn1.setToY(1.1);
                scaleIn1.setInterpolator(Interpolator.EASE_IN);
                scaleIn1.play();
            }

        });

    }

    private void drawingScaleOut() {
        drawing.setOnMouseExited(event -> {
            if (!isAnimating) {

                ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), drawing);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.setInterpolator(Interpolator.EASE_OUT);
                scaleOut.play();

                ScaleTransition scaleOut1 = new ScaleTransition(Duration.seconds(0.3), papersBackground);
                scaleOut1.setToX(1.0);
                scaleOut1.setToY(1.0);
                scaleOut1.setInterpolator(Interpolator.EASE_OUT);
                scaleOut1.play();

            }
        });

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

    // Register page

    public void backToLogin() {

        double a = 0.86;
        double b = 0;
        double c = 0.07;
        double d = 1;
        int speed = 1000;

        TranslateTransition tt = new TranslateTransition(Duration.millis(speed), drawingPane);
        tt.setByX(-428);
        tt.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt.play();

        TranslateTransition tt2 = new TranslateTransition(Duration.millis(speed), loginPane);
        tt2.setByX(-428);
        tt2.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt2.play();

        TranslateTransition tt3 = new TranslateTransition(Duration.millis(speed), registerPane);
        tt3.setByX(-428);
        tt3.setInterpolator(Interpolator.SPLINE(a, b, c, d));
        tt3.play();

        loginButton.setDefaultButton(true);
        signUpButton.setDefaultButton(false);
        setTravesable(true);

    }

    public void setTravesable(boolean isLoginPage) {

        if (isLoginPage) {
            userField.setDisable(false);
            passwordField.setDisable(false);

            emailField.setDisable(true);
            rUserField.setDisable(true);
            rPasswordField.setDisable(true);
        } else {
            userField.setDisable(true);
            passwordField.setDisable(true);

            emailField.setDisable(false);
            rUserField.setDisable(false);
            rPasswordField.setDisable(false);
        }
    }

    public void signUp() throws Exception {
        Platform.runLater(() -> {

            String email = emailField.getText();
            String username = rUserField.getText();
            String password = rPasswordField.getText();
            rErrorField.setStyle("-fx-text-fill: red;");

            emailField.getStyleClass().removeAll("wrong-field", "correct-field");
            rUserField.getStyleClass().removeAll("wrong-field", "correct-field");
            rPasswordField.getStyleClass().removeAll("wrong-field", "correct-field");

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                emailField.getStyleClass().removeAll("wrong-field", "correct-field");
                rUserField.getStyleClass().removeAll("wrong-field", "correct-field");
                rPasswordField.getStyleClass().removeAll("wrong-field", "correct-field");
                return;
            }

            boolean validEmail = isValidEmailAddress(email);
            boolean emailExists;

            try {
                emailExists = Utils.c.emailThere(email);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            if (!validEmail) {
                rErrorField.setText("Invalid Email");
                emailField.getStyleClass().remove("correct-field");
                if (!emailField.getStyleClass().contains("wrong-field")) {
                    emailField.getStyleClass().add("wrong-field");
                }
                rErrorField.setVisible(true);
                return;
            } else {
                emailField.getStyleClass().remove("wrong-field");
                if (!emailField.getStyleClass().contains("correct-field")) {
                    emailField.getStyleClass().add("correct-field");
                }
            }

            if (emailExists) {
                rErrorField.setText("Email already exists");
                emailField.getStyleClass().remove("correct-field");
                if (!emailField.getStyleClass().contains("wrong-field")) {
                    emailField.getStyleClass().add("wrong-field");
                }
                rErrorField.setVisible(true);
                return;
            } else {
                emailField.getStyleClass().remove("wrong-field");
                if (!emailField.getStyleClass().contains("correct-field")) {
                    emailField.getStyleClass().add("correct-field");
                }
            }

            try {
                if (Utils.c.userThere(username)) {
                    rErrorField.setText("Username already exists");
                    rUserField.getStyleClass().remove("correct-field");
                    if (!rUserField.getStyleClass().contains("wrong-field")) {
                        rUserField.getStyleClass().add("wrong-field");
                    }
                    rErrorField.setVisible(true);
                    return;
                } else {
                    rUserField.getStyleClass().remove("wrong-field");
                    if (!rUserField.getStyleClass().contains("correct-field")) {
                        rUserField.getStyleClass().add("correct-field");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            if (!validUsername(username)) {
                rUserField.getStyleClass().remove("correct-field");
                if (!rUserField.getStyleClass().contains("wrong-field")) {
                    rUserField.getStyleClass().add("wrong-field");
                }
                return;
            } else {
                rUserField.getStyleClass().remove("wrong-field");
                if (!rUserField.getStyleClass().contains("correct-field")) {
                    rUserField.getStyleClass().add("correct-field");
                }
            }

            if (!validPass(password)) {
                rPasswordField.getStyleClass().remove("correct-field");
                if (!rPasswordField.getStyleClass().contains("wrong-field")) {
                    rPasswordField.getStyleClass().add("wrong-field");
                }
                return;
            } else {
                rPasswordField.getStyleClass().remove("wrong-field");
                if (!rPasswordField.getStyleClass().contains("correct-field")) {
                    rPasswordField.getStyleClass().add("correct-field");
                }
            }

            boolean registered = false;

            try {
                registered = Utils.c.registerRequest(username, email, password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!registered) {
                rErrorField.setText("Something went wrong, please try again.");
                rErrorField.setVisible(true);
                return;
            }

            rErrorField.setText("Successfully registered! Hope you enjoy my app :-)");
            rErrorField.setStyle("-fx-text-fill: #4CAF50;");
            rErrorField.setVisible(true);

            emailField.getStyleClass().remove("wrong-field");
            if (!emailField.getStyleClass().contains("correct-field")) {
                emailField.getStyleClass().add("correct-field");
            }
            rUserField.getStyleClass().remove("wrong-field");
            if (!rUserField.getStyleClass().contains("correct-field")) {
                rUserField.getStyleClass().add("correct-field");
            }
            rPasswordField.getStyleClass().remove("wrong-field");
            if (!rPasswordField.getStyleClass().contains("correct-field")) {
                rPasswordField.getStyleClass().add("correct-field");
            }
        });
    }

    public boolean validUsername(String username) {
        if (username.length() < 3 || username.length() > 12) {
            rErrorField.setText("Username must be more than 3 characters and less than 12 characters");
            rUserField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            rErrorField.setText("Username can only contain letters numbers and underscores");
            rUserField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }
        if (username.startsWith("_") || username.endsWith("_")) {
            rErrorField.setText("Username cant start or end with an underscore");
            rUserField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }
        if (username.contains("__")) {
            rErrorField.setText("Username can't have consecutive underscores");
            rUserField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }
        if (username.contains(" ")) {
            rErrorField.setText("Username can't contain whitespaces");
            rUserField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        return true;
    }

    private boolean validPass(String password) {

        if (password.length() < 8) {
            rErrorField.setText("Password must be at least 8 characters long");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            rErrorField.setText("Password must have at least one upper case letter");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            rErrorField.setText("Password must have at least one lower case letter");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        if (!password.matches(".*[@#$%^&+=].*")) {
            rErrorField.setText("Password must have at least one special character");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            rErrorField.setText("Password must have at least one digit");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        if (password.matches(".*\\s.*")) {
            rErrorField.setText("Password field does not allow whitespace");
            rPasswordField.getStyleClass().add("wrong-field");
            rErrorField.setVisible(true);
            return false;
        }

        return true;
    }

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
