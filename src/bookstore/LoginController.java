/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Zi Yu
 */
public class LoginController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button btnLogin;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private Connection connection;
    private PreparedStatement prepare;
    private ResultSet result;

    /**
     * This method authenticate an administrator using the provided username and password
     */
    public void adminLogin() {
        connection = Database.getConnection();
        String sql = "SELECT * FROM admin WHERE username = ? and password = ?";

        try {
            prepare = connection.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                // Authentication successful
                // hide login page
                btnLogin.getScene().getWindow().hide();

                // open dashboard page
                Parent root = FXMLLoader.load(getClass().getResource("Book.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            } else if (username.getText().isEmpty() || password.getText().isEmpty()) {
                // Empty fields
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Enter all blank fields!");
                alert.showAndWait();
            } else {
                // Invalid username/password
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username/password");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
