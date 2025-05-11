package arch.joe.client.GUI.Messenger.SearchPopUp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.MessengerController;
import arch.joe.client.GUI.Messenger.Components.ContactBox;
import arch.joe.db.UserDao;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

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

    private MessengerController controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utils.clipToRounded(addButton, 39, 39);
        Utils.clipToRounded(searchButton, 39, 39);

    }

    public void searchAction() {
        String searchTerm = searchField.getText();

        if (!searchTerm.isEmpty()) {
            searchContactsView.getChildren().clear();
            ArrayList<Contact> searchContacts = fetchContactsFromDatabase(searchTerm);
            for (Contact contact : searchContacts) {
                searchContactsView.getChildren().add(new ContactBox(contact));

            }
        }
    }

    public void addAction() {
        System.out.println(focusedBox);
        if (focusedBox != null) {
            controller.updateContactsView(focusedBox);

        }
    }

    private ArrayList<Contact> fetchContactsFromDatabase(String searchTerm) {
        return UserDao.searchUser(searchTerm);
    }

    public void setFocusedBox(ContactBox box) {
        focusedBox = box;
        System.out.println("Focused ContactBox: " + focusedBox.getContactInfo().getName());

    }

    public void setMessengerController(MessengerController controller) {
        this.controller = controller;
    }
}
