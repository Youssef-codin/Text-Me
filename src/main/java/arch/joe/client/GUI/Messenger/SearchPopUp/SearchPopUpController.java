package arch.joe.client.GUI.Messenger.SearchPopUp;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.Components.ContactBox;
import arch.joe.db.UserDao;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SearchPopUpController implements Initializable {

    @FXML
    private MFXButton addButton;
    @FXML
    private MFXButton searchButton;
    @FXML
    private MFXTextField searchField;
    @FXML
    private VBox searchContactsView;

    private static ContactBox focusedBox;

    private static LinkedHashMap<String, ContactBox> userContacts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Utils.sController = this;
        userContacts = Utils.mController.getUserContacts();

        Utils.clipToRounded(addButton, 39, 39);
        Utils.clipToRounded(searchButton, 39, 39);

    }

    public void searchAction() {
        String searchTerm = searchField.getText();

        if (!searchTerm.isEmpty()) {
            searchContactsView.getChildren().clear();
            ArrayList<Contact> searchContacts = fetchContactsFromDatabase(searchTerm);
            if (searchContacts != null) {
                for (Contact contact : searchContacts) {
                    searchContactsView.getChildren().add(new ContactBox(contact, false));

                }
            }
        }
    }

    public void addAction() throws InterruptedException {
        if (focusedBox != null) {
            ContactBox tempBox = focusedBox;
            focusedBox = null;
            addButton.setDisable(true);

            double boxHeight = tempBox.getBoundsInParent().getHeight();

            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), tempBox);
            slideOut.setByX(-tempBox.getBoundsInParent().getWidth());

            int startIndex = searchContactsView.getChildren().indexOf(tempBox) + 1;
            ArrayList<Node> nodesToMove = new ArrayList<>();

            for (int i = startIndex; i < searchContactsView.getChildren().size(); i++) {
                nodesToMove.add(searchContactsView.getChildren().get(i));
            }

            slideOut.setOnFinished(e -> {
                tempBox.setVisible(false);

                ArrayList<TranslateTransition> moveUps = new ArrayList<>();

                for (Node node : nodesToMove) {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(650), node);
                    tt.setByY(-boxHeight);
                    tt.setInterpolator(Interpolator.SPLINE(0.22, 0.61, 0.36, 1.00));
                    moveUps.add(tt);
                }

                ParallelTransition parallel = new ParallelTransition();
                parallel.getChildren().addAll(moveUps);

                parallel.setOnFinished(event -> {
                    for (Node node : nodesToMove) {
                        node.setTranslateY(0);
                    }

                    searchContactsView.getChildren().remove(tempBox);
                    addButton.setDisable(false);
                });

                parallel.play();
            });

            slideOut.play();
            Utils.mController.updateContactsView(new ContactBox(tempBox.getContactInfo(), true));
        }
    }

    // TODO: get from server
    private ArrayList<Contact> fetchContactsFromDatabase(String searchTerm) {
        ArrayList<Contact> contacts = UserDao.searchUser(searchTerm);
        ArrayList<Contact> tempContacts = new ArrayList<>();

        for (Contact contact : contacts) {
            if (!userContacts.containsKey(contact.getName()) && !contact.getName().equals(Utils.c.getUsername())) {
                tempContacts.add(contact);
            }
        }

        return tempContacts;
    }

    public void setFocusedBox(ContactBox box) {
        if (focusedBox == null) {
            box.getStyleClass().set(0, "focused-contact-box");
            focusedBox = box;

        } else {
            focusedBox.getStyleClass().set(0, "contact-box");
            box.getStyleClass().set(0, "focused-contact-box");
            focusedBox = box;
        }
    }

}
