package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Person;
import service.UserSession;

public class SignUpController {


    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    public void createNewAccount(ActionEvent actionEvent) {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Passwords do not match.");
            return;
        }


        try {
            DbConnectivityClass db = new DbConnectivityClass();


            Person newUser = new Person(
                    0,
                    userName,
                    "",
                    "",
                    "",
                    userName + "@example.com",
                    ""
            );
            db.insertUser(newUser);

            showAlert(Alert.AlertType.INFORMATION, "Account Created", "Your account has been successfully created!");


            UserSession userSession = UserSession.getInstance(userName, password);
            userSession.saveUserSession();

            goBack(actionEvent);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create a new account.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
