package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    @FXML
    private Menu csvMenu;

    @FXML
    private Label statusLabel;

@FXML
private Button addBtn;
    @FXML
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ComboBox<String> major;

    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> majorOptions = FXCollections.observableArrayList();
        for (Major m : Major.values()) {
            majorOptions.add(m.name());
        }

        major.setItems(majorOptions);
        major.getSelectionModel().selectFirst(); // Optionally select the first item by default



        addBtn.setDisable(true);

        // Listener for add button
        ChangeListener<String> formListener = (obs, oldVal, newVal) -> validateForm();

        first_name.textProperty().addListener(formListener);
        last_name.textProperty().addListener(formListener);
        department.textProperty().addListener(formListener);
       // major.textProperty().addListener(formListener);
        email.textProperty().addListener(formListener);
        imageURL.textProperty().addListener(formListener);
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);

        // Listner for edit and delete buttons
        tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean noSelection = (newSelection == null);
            editBtn.setDisable(noSelection);
            deleteBtn.setDisable(noSelection);
        });



        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void addNewRecord() {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    major.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
        statusLabel.setText("New record added successfully!");
            clearForm();


    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        major.setValue("");
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major.getValue(),
                email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        statusLabel.setText("Record updated successfully!");
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        major.setValue(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
    private void validateForm() {
        String namePattern = "^[A-Z][a-zA-Z]{1,29}$";
        String departmentPattern = "^[A-Za-z\\s]{2,15}$";
        String majorPattern = "^[A-Za-z\\s]{2,15}$";
        String emailPattern = "[A-Z,a-z]{1}+[A-Z,a-z,0-9]+@farmingdale.edu";
        String imageURLPattern = "^(http|https)://.*\\.(jpg|jpeg|png|gif)$";
        boolean valid =
                !first_name.getText().trim().isEmpty() &&
                        !last_name.getText().trim().isEmpty() &&
                        !department.getText().trim().isEmpty() &&
                        !major.getValue().trim().isEmpty() &&

                        !email.getText().trim().isEmpty() &&
                        !imageURL.getText().trim().isEmpty();

        addBtn.setDisable(!valid);
    }
    @FXML
    protected void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] dataArray = line.split(",");  // Renamed variable to avoid shadowing

                    // Assuming your CSV has the format: first_name, last_name, department, major, email, imageURL
                    if (dataArray.length == 6) {
                        Person p = new Person(dataArray[0], dataArray[1], dataArray[2], dataArray[3], dataArray[4], dataArray[5]);
                        cnUtil.insertUser(p);
                        cnUtil.retrieveId(p);
                        p.setId(cnUtil.retrieveId(p));
                        data.add(p);  // Now correctly referring to the ObservableList
                    }
                }
                statusLabel.setText("CSV file imported successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to import CSV file.");
            }
        }
    }

    @FXML
    protected void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write header line
                writer.write("First Name, Last Name, Department, Major, Email, Image URL");
                writer.newLine();

                // Write data
                for (Person person : data) {
                    writer.write(person.getFirstName() + "," + person.getLastName() + "," + person.getDepartment() + ","
                            + person.getMajor() + "," + person.getEmail() + "," + person.getImageURL());
                    writer.newLine();
                }
                statusLabel.setText("CSV file exported successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to export CSV file.");
            }
        }
    }




}