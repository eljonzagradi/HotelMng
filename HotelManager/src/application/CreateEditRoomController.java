package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;

import javafx.scene.control.ChoiceBox;

public class CreateEditRoomController implements Initializable {
	@FXML private ChoiceBox<String> view_x;
	@FXML private Button cancel_b;
	@FXML private TextField capacity_x;
	@FXML private ChoiceBox<String>  smoking_x;
	@FXML private ChoiceBox<String>  currency_x;
	@FXML private TextField roomNo_x;
	@FXML private Button submit_b;
	@FXML private ChoiceBox<String>  category_x;
	@FXML private ImageView photo_x;
	@FXML private TextField price_night_x;
	@FXML private ChoiceBox<String> ac_x;
	
	public static int room_number;
		
	public void loadRoom() {
		
		
	}
	
	public void createRooom() {
		
		
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		
	}
	
	

}
