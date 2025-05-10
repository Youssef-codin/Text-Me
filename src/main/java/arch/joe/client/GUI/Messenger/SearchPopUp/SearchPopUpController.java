package arch.joe.client.GUI.Messenger.SearchPopUp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import arch.joe.app.Contact;
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.Components.ContactBox;
import arch.joe.db.UserDao;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.mail.search.SearchTerm;
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
    private VBox contactsView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utils.clipToRounded(addButton, 39, 39);
        Utils.clipToRounded(searchButton, 39, 39);

    }

    public void searchAction() {
        String searchTerm = searchField.getText();

        if (!searchTerm.isEmpty()) {
            contactsView.getChildren().clear();
            ArrayList<Contact> searchContacts = fetchContactsFromDatabase(searchTerm);
            for (Contact contact : searchContacts) {
                contactsView.getChildren().add(new ContactBox(contact));

            }
        }
    }

    public void addAction() {

    }

    private ArrayList<Contact> fetchContactsFromDatabase(String searchTerm) {
        return UserDao.searchUser(searchTerm);
    }
}
