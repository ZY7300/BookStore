/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javax.imageio.ImageIO;

/**
 *
 * @author Zi Yu
 */
public class BookController implements Initializable {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfAuthor;
    @FXML
    private TextField tfYear;
    @FXML
    private TextField tfPrice;
    @FXML
    private TableView<Books> tbBooks;
    @FXML
    private TableColumn<Books, Integer> colId;
    @FXML
    private TableColumn<Books, String> colTitle;
    @FXML
    private TableColumn<Books, String> colAuthor;
    @FXML
    private TableColumn<Books, Integer> colYear;
    @FXML
    private TableColumn<Books, Double> colPrice;
    @FXML
    private Button btnInsert;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnReset;
    @FXML
    private TextField tfSearch;
    @FXML
    private ImageView imagePreview;
    
    private byte[] selectedImage;

    /**
     * Display the details of the selected item
     * 
     * @param event 
     */
    @FXML
    private void display(MouseEvent event) {
        Books selectedItem = tbBooks.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            tfId.setText(Integer.toString(selectedItem.getId()));
            tfTitle.setText(selectedItem.getTitle());
            tfAuthor.setText(selectedItem.getAuthor());
            tfYear.setText(Integer.toString(selectedItem.getYear()));
            tfPrice.setText(Double.toString(selectedItem.getPrice()));
            
            byte[] imageBytes = selectedItem.getImage();
            if (imageBytes != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                Image image = new Image(inputStream);
                imagePreview.setImage(image);
            } else {
                imagePreview.setImage(null);
            }
        }
    }

    /**
     * Handle action for insert book
     * 
     * @param event 
     */
    @FXML
    private void handleInsert(ActionEvent event) {
        event.consume();
        insertBooks();
    }

    /**
     * Handle action for update book
     * 
     * @param event 
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        event.consume();
        updateBooks();
    }

    /**
     * Handle action for delete book
     * 
     * @param event 
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        event.consume();
        deleteBooks();
    }
    
    /**
     * Handle action for upload the cover image
     * 
     * @param event 
     */
    @FXML
    private void handleUpload(ActionEvent event) {
        uploadImage();
    }

    /**
     * Handle action for search book
     * 
     * @param event 
     */
    private void handleSearch(ActionEvent event) {
        String searchTerm = tfSearch.getText();
        if (!searchTerm.isEmpty()) {
            searchBooks(searchTerm);
        } else {
            showBooks();
        }
    }

    /**
     * Initialize the initial state of the user interface
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        showBooks();

        tfSearch.setText("");

        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchBooks(newValue);
        });
    }

    private Connection connection;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    /**
     * Retrieve books from database
     * 
     * @return The ObservableList of Books that contain retrieved book records
     */
    public ObservableList<Books> getBooksList() {
        ObservableList<Books> bookList = FXCollections.observableArrayList();
        connection = Database.getConnection();
        String sql = "SELECT * FROM books";

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
            Books books;
            while (result.next()) {
                byte[] imageBytes = result.getBytes("image");
                books = new Books(result.getInt("id"), result.getString("title"), result.getString("author"), result.getInt("year"), result.getDouble("price"), imageBytes);
                bookList.add(books);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    /**
     * Show books from database
     */
    public void showBooks() {
        ObservableList<Books> list = getBooksList();

        colId.setCellValueFactory(new PropertyValueFactory<Books, Integer>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Books, String>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<Books, String>("author"));
        colYear.setCellValueFactory(new PropertyValueFactory<Books, Integer>("year"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Books, Double>("price"));
        colPrice.setCellFactory(column -> {
            return new TableCell<Books, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        // Format the price to two decimal places
                        setText(String.format("%.2f", price));
                    }
                }
            };
        });

        tbBooks.setItems(list);
    }

    /**
     * Insert book into database
     */
    private void insertBooks() {
        connection = Database.getConnection();
        String sql = "INSERT INTO books VALUES (?,?,?,?,?,?)";

        try {

            if (tfId.getText().isEmpty() || tfTitle.getText().isEmpty() || tfAuthor.getText().isEmpty() || tfYear.getText().isEmpty() || tfPrice.getText().isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Enter all blank fields!");
                alert.showAndWait();
            } else {
                try {
                    prepare = connection.prepareStatement(sql);
                    prepare.setString(1, tfId.getText());
                    prepare.setString(2, tfTitle.getText());
                    prepare.setString(3, tfAuthor.getText());
                    prepare.setString(4, tfYear.getText());
                    prepare.setString(5, tfPrice.getText());
                    prepare.setBytes(6, selectedImage);
                    prepare.executeUpdate();

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully add the data!");
                    alert.showAndWait();

                    showBooks();
                    clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Enter all fields with correct details!");
                    alert.showAndWait();
                }
            }

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Something wrong!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Update book in database
     */
    private void updateBooks() {
        connection = Database.getConnection();

        String sql = "UPDATE books SET `title` = ?, `author` = ?, `year` = ?, `price` = ?, `image` = ? WHERE id = ?";

        try {
            if (tfId.getText().isEmpty() || tfTitle.getText().isEmpty() || tfAuthor.getText().isEmpty()
                    || tfYear.getText().isEmpty() || tfPrice.getText().isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Enter all blank fields!");
                alert.showAndWait();
            } else {
                prepare = connection.prepareStatement(sql);
                prepare.setString(1, tfTitle.getText());
                prepare.setString(2, tfAuthor.getText());
                prepare.setString(3, tfYear.getText());
                prepare.setString(4, tfPrice.getText());
                prepare.setBytes(5, selectedImage); // Add image data to the query
                prepare.setString(6, tfId.getText());

                prepare.executeUpdate();

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Congratulations");
                alert.setHeaderText(null);
                alert.setContentText("Successfully update the data!");
                alert.showAndWait();

                showBooks();
                clear();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Something wrong!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Delete book from database
     */
    private void deleteBooks() {
        connection = Database.getConnection();

        String sql = "DELETE from books WHERE `id` = '" + tfId.getText() + "'";

        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);

            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure that you want to delete it?");

            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get() == ButtonType.OK) {
                statement = connection.createStatement();
                statement.executeUpdate(sql);
            } else {
                return;
            }

            showBooks();
            clear();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Something wrong!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Search books in the TableView
     * 
     * @param searchTerm 
     */
    private void searchBooks(String searchTerm) {
        ObservableList<Books> filteredList = FXCollections.observableArrayList();
        ObservableList<Books> originalList = getBooksList();

        for (Books book : originalList) {
            if (book.getTitle().toLowerCase().contains(searchTerm.toLowerCase())
                    || book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredList.add(book);
            }
        }

        tbBooks.setItems(filteredList);
    }
    
    /**
     * Resize image to specified size
     * 
     * @param inputFile
     * @param targetWidth
     * @param targetHeight
     * @return Array of byte which represent the resized image data
     * @throws IOException 
     */
    private byte[] resizeImage(File inputFile, int targetWidth, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        resizedImage.getGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Handle action of upload image
     */
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);

            try {
                int targetWidth = 900;
                int targetHeight = 1200;
                selectedImage = resizeImage(selectedFile, targetWidth, targetHeight);
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Something wrong!");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    /**
     * Clear the input fields and image preview from the user interface
     */
    @FXML
    public void clear() {
        tfId.setText("");
        tfTitle.setText("");
        tfAuthor.setText("");
        tfYear.setText("");
        tfPrice.setText("");
        imagePreview.setImage(null);
    }
}
