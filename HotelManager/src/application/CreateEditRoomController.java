package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class CreateEditRoomController implements Initializable {
	
	@FXML private Label header_l;
	@FXML private ImageView photo_x;
	@FXML private TextField roomNo_x;
	@FXML private ComboBox<String>  category_x;
	@FXML private TextField capacity_x;
	@FXML private RadioButton ac_yes;
	@FXML private RadioButton ac_no;
	@FXML private ToggleGroup ac;
	@FXML private ChoiceBox<String> view_x;
	@FXML private RadioButton smoking_yes;
	@FXML private RadioButton smoking_no;
	@FXML private ToggleGroup smoking;
	@FXML private TextField price_night_x;
	@FXML private ChoiceBox<String>  currency_x;
	@FXML private Label footer_l;
	@FXML private Button submit_b;
	@FXML private Button cancel_b;
	
	ObservableList<String> categories = FXCollections.observableArrayList();
	ObservableList<String> views = FXCollections.observableArrayList("Mountain View", "Sea View", "City View" , "No View");
	ObservableList<String> currencies = FXCollections.observableArrayList("ALL", "EUR","USD","GPD");
	public static int room_number = 101;
	
	public void generalInitalValues() {
		if(room_number == -999) {
			header_l.setText("Adding");			
		} else if(room_number != -999) {
			header_l.setText("Editing");
			roomNo_x.setDisable(true);
		}
		
		loadCategories();
		view_x.setItems(views);
		currency_x.setItems(currencies);
	}
	
	public void clickSubmit() {
		
		if(!areEmpty()) {
			
			int roomNo = Integer.parseInt(roomNo_x.getText());
			String category = category_x.getValue();
			int capacity = Integer.parseInt(capacity_x.getText());
			RadioButton  selectedAc = (RadioButton) ac.getSelectedToggle();
			String acValue = selectedAc.getText();
			String view = view_x.getValue();
			RadioButton  selectedSmoking = (RadioButton) smoking.getSelectedToggle();
			String smokingValue = selectedSmoking.getText();
			int price =  Integer.parseInt(price_night_x.getText());
			String currency = currency_x.getValue();
			
			if(!RoomsController.roomList.contains(roomNo)) {
				
				PreparedStatement newRoom;
				try {
					newRoom = Database.con().prepareStatement
							("INSERT INTO `hoteldatabase`.`rooms` "
							+ "(`number`, `category`, `capacity`, `air_c`, `view`, `smoking`, `price_night`, `currency`) "
							+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?);");
					
					newRoom.setInt(1, roomNo);
					newRoom.setString(2, category);
					newRoom.setInt(3, capacity);
					newRoom.setString(4, acValue);
					newRoom.setString(5, view);
					newRoom.setString(6, smokingValue);
					newRoom.setInt(7, price);
					newRoom.setString(8, currency);
					
		    		int status = newRoom.executeUpdate();
		    		
		    		if(status != 0) {
		    			addCategoty(category_x);
	        			Stage stage =  (Stage)submit_b.getScene().getWindow();
	        			stage.close();
		    		} else {
		    			footer_l.setText("Something Went Wrong");
		    		}

				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			} else if(RoomsController.roomList.contains(room_number)) {
				
				PreparedStatement updateRoom;
				
				try {
					
					updateRoom = Database.con().prepareStatement
							("UPDATE `hoteldatabase`.`rooms`\r\n"
									+ "SET\r\n"
									+ "`category` = ?,\r\n"
									+ "`capacity` = ?,\r\n"
									+ "`air_c` = ?,\r\n"
									+ "`view` = ?,\r\n"
									+ "`smoking` = ?,\r\n"
									+ "`price_night` = ?,\r\n"
									+ "`currency` = ?\r\n"
									+ "\r\n"
									+ "WHERE `number` = ?;");
					updateRoom.setString(1, category);
					updateRoom.setInt(2, capacity);
					updateRoom.setString(3, acValue);
					updateRoom.setString(4, view);
					updateRoom.setString(5, smokingValue);
					updateRoom.setInt(6, price);
					updateRoom.setString(7, currency);
					updateRoom.setInt(8, roomNo);
					
		    		int status = updateRoom.executeUpdate();
		    		
		    		if(status != 0) {
		    			addCategoty(category_x);
	        			Stage stage =  (Stage)submit_b.getScene().getWindow();
	        			stage.close();
	        			
		    		} else {
		    			footer_l.setText("Something Went Wrong");
		    		}
		    		
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		} else {
			footer_l.setText("Complete All Fields");
		}
		
	}
	
	public void loadRoom() {
    	 
    	 if(room_number != -999) {
    		
    		 try {
    			 
    			 PreparedStatement selectRoom = Database.con().prepareStatement
    					 ("SELECT * "
			 		+ "FROM hoteldatabase.rooms\r\n"
			 		+ "WHERE number = ? ");
    			 selectRoom.setInt(1, room_number);
    			 ResultSet selectedRoom = selectRoom.executeQuery();
    			 
    			 while(selectedRoom.next()) {
    				 
    				 int roomNo = selectedRoom.getInt("number");
    				 String category = selectedRoom.getString("category");
				     int capacity = selectedRoom.getInt("capacity");
				     String acValue = selectedRoom.getString("air_c");
				     String view = selectedRoom.getString("view");
				     String smokingValue = selectedRoom.getString("smoking");
				     int price =  selectedRoom.getInt("price_night");
				     String currency = selectedRoom.getString("currency");
				
				     roomNo_x.setText(roomNo + "");
				     category_x.setValue(category);
				     capacity_x.setText(capacity + "");
				     if(acValue.equals("Yes")) {
				    	 ac_yes.setSelected(true);
				    	 } else {
				    		 ac_no.setSelected(true);
				    		 }
				     view_x.setValue(view);
				     if(smokingValue.equals("Yes")) {
				    	 smoking_yes.setSelected(true);
				    	 } else {
				    		 smoking_no.setSelected(true);
				    		 }
				     price_night_x.setText(price + "");
				     currency_x.setValue(currency);
				     }
    			 
    		 } catch (SQLException e) {
    			 e.printStackTrace();
    			 
    		 }
    		 
    	 }
    	 
     }
	
	public void clickCancel() {
		
		Stage stage =  (Stage)cancel_b.getScene().getWindow();
		stage.close();
		
	}
	
	public boolean areEmpty() {
		if(
				roomNo_x.getText().isEmpty()
				|| category_x.getValue().isEmpty()
				|| capacity_x.getText().isEmpty()
				|| ac.getSelectedToggle() == null
				|| view_x.getValue().isEmpty()
				|| smoking.getSelectedToggle() == null
				|| price_night_x.getText().isEmpty()
				|| currency_x.getValue().isEmpty()) 
		{
			return true;
		} 
		
		else {
			return false;
		}
		
	}
	
	
	public void loadCategories() {
		
		try {
			PreparedStatement loadCategories = 
					Database.con().prepareStatement
					("select * from hoteldatabase.roomcategories");
			ResultSet rs = loadCategories.executeQuery();
			
			while(rs.next()) {
				String roomCat = rs.getString("name");
				
				if(!categories.contains(roomCat)) {
					categories.add(roomCat);
					}
				}
			category_x.setItems(categories);
			
		} catch (SQLException e) {
			e.printStackTrace();	
		}

	}
	
	public void addCategoty(ComboBox<String> cb ) {
		
		String category = cb.getEditor().getText();
		
		if(!categories.contains(category)) {
			
			try {
				
				PreparedStatement newCategory = Database.con().prepareStatement
						("INSERT INTO `hoteldatabase`.`roomcategories` (`name`) VALUES ( ? );");
				newCategory.setString(1, category);				     
				newCategory.execute();
				} 
			
			catch (SQLException e) {
					e.printStackTrace();

			}
			
		}
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		generalInitalValues();
		loadRoom();
	}
	
}
