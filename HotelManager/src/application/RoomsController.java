package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RoomsController implements Initializable {

	//RoomView FXML Components:
	@FXML private AnchorPane roomView;
	@FXML private Button open_b;
	@FXML private Button edit_b;
	@FXML private Button delete_b;
	@FXML private ImageView photo_x;
	@FXML private Label roomNo_x;
	@FXML private Label category_x;
	@FXML private Label capacity_x; 
	@FXML private Label ac_x;
	@FXML private Label view_x;
	@FXML private Label smoking_x;
	@FXML private Label price_x;
	
	@FXML private BorderPane borderPane;
	@FXML private ToggleButton viewAllRooms_b;
	@FXML private ToggleButton viewAvailableRooms_b;
	@FXML private Button addRoom_b;
	@FXML private GridPane roomLayout;
	
	public void openCreateAndEditRoom() {
		
		Stage primaryStage = new Stage();
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/CreateEditRoom.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.showAndWait();
			
		} 
		
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
		
	}
	
	public void clickOpen() {
		
		Stage stage =  (Stage) open_b.getScene().getWindow();
		stage.close();
		
		Stage primaryStage = new Stage();
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/Calendar.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} 
		
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		
	}
	
}
