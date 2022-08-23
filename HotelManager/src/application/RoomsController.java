package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RoomsController implements Initializable {

	//RoomView FXML Components:
	@FXML private AnchorPane roomView;
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
	@FXML private Button clear_b;
	@FXML private Button clearImg_b;
	
	@FXML private ToggleButton viewAllRooms_b;
	@FXML private ToggleButton viewAvailableRooms_b;
	@FXML private ToggleGroup tg;
	@FXML private Button addRoom_b;
	@FXML private Button edit_b;
	@FXML private Button open_b;
	@FXML private GridPane roomLayout;
	@FXML private Button imageSelector_b;
	
	List<Integer> roomList = new ArrayList<Integer>();
	ObservableList<String> categories = FXCollections.observableArrayList();
	ObservableList<String> views = FXCollections.observableArrayList("Mountain View", "Sea View", "City View" , "No View");
	ObservableList<String> currencies = FXCollections.observableArrayList("ALL", "EUR","USD","GPD");
	LocalDate todayDate = LocalDate.now();
	private Room activeSelection = null;
	File file = null;
	
	public Room getActiveSelection() {
		return activeSelection;
	}
	
	public void setActiveSelection(Room active) {
		this.activeSelection = active;
		
	}
	
	public void generalInitalValues() {
		loadCategories();
		view_x.setItems(views);
		currency_x.setItems(currencies);
	}
	
	public void clickEdit() {
		setDisable(false);
		open_b.setDisable(true);
		roomNo_x.setDisable(true);
		submit_b.setDisable(false);
	}
	
	public void clickAdd() {
		clickClear();
		setDisable(false);
		submit_b.setDisable(false);
		clear_b.setDisable(false);
	}
	
	public void clickOpen() {
		
		CalendarController.selectedRoom = Integer.parseInt(roomNo_x.getText());
		CalendarController.priceNight = Integer.parseInt(price_night_x.getText());
		
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
	
	
	
	public void clickSelectImage() {
		
		FileChooser chooser = new FileChooser();
	    chooser.setTitle("Select Image File");

	    chooser.getExtensionFilters().addAll(
	        new FileChooser.ExtensionFilter("Image Files",
	            "*.png", "*.jpg", "*.jpeg"));

	    file = chooser.showOpenDialog(null);
		URL url;
		
		if(file != null){
			
			try {
				url = file.toURI().toURL();
				photo_x.setImage(new Image(url.toExternalForm()));
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadRooms() {

		int rowInx = 0;
		int columnInx = 0;
		ResultSet resultSet = null;
		
		try {
			PreparedStatement availableRooms = Database.con().prepareStatement
			("SELECT number, id_room, category, capacity, air_c, view, smoking, price_night, currency, photo, check_in, check_out\r\n"
			+ "FROM  hoteldatabase.rooms ro\r\n"
			+ "LEFT JOIN hoteldatabase.reservations r\r\n"
			+ "USING (number)\r\n"
			+ "WHERE  ro.number NOT IN\r\n"
			+ "(SELECT number FROM  hoteldatabase.rooms ro\r\n"
			+ "LEFT JOIN hoteldatabase.reservations r\r\n"
			+ "USING (number) WHERE NOT check_in > ? AND check_out > ?)\r\n"
			+ "ORDER BY ro.number,check_in");
			availableRooms.setDate(1,java.sql.Date.valueOf(todayDate));
			availableRooms.setDate(2,java.sql.Date.valueOf(todayDate));
			
			
			PreparedStatement allRooms = Database.con().prepareStatement
			( "SELECT *"
			+ "FROM `hoteldatabase`.rooms ro\r\n"
		    + "LEFT JOIN `hoteldatabase`.reservations r \r\n"
			+ "ON ro.number = r.number \r\n"
			+ "ORDER BY ro.number,check_in");
			
			if( viewAllRooms_b.isSelected()) {
				resultSet = allRooms.executeQuery();
			} 
			else if( viewAvailableRooms_b.isSelected()) {
					 resultSet = availableRooms.executeQuery();
			}
			
			while(resultSet.next()) {
				
				Date checkin = resultSet.getDate("check_in");
			    Date checkout = resultSet.getDate("check_out");
			    
			    int roomID = resultSet.getInt("id_room");
				int roomNo = resultSet.getInt("number");
				String category = resultSet.getString("category");
			    int capacity = resultSet.getInt("capacity");
			    String acValue = resultSet.getString("air_c");
			    String view = resultSet.getString("view");
			    String smokingValue = resultSet.getString("smoking");
			    int price =  resultSet.getInt("price_night");
			    String currency = resultSet.getString("currency");
			    
			      
			    Room room = new Room (
			    		roomID, roomNo, category,
			    		capacity, acValue, view,
			    		smokingValue, price, currency,
			    		null);
			    selectRoom(room);
			    
			    if(!roomList.contains(roomNo)) {

			    	if(checkin !=null || checkout !=null ) {
			    		
			    		if(checkin.toLocalDate().compareTo(todayDate)
			    				* todayDate.compareTo(checkout.toLocalDate()) >= 0) {
			    			
			    			room.setBackground(new Background(
			    					new BackgroundFill(
			    							Color.RED,
					    					new CornerRadii(0),
					    					new Insets(0))));
			    			}
			    		}
				    roomLayout.add(room, columnInx, rowInx);				    
				    if(columnInx < 3) {
				    	++columnInx;
				    	} else {
				    		columnInx = 0;
				    		roomLayout.getRowConstraints()
				    		.add(new RowConstraints(80));
				    		++rowInx;
				    		}
				    }
		    	roomList.add(roomNo);
			}

			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	 }
	
	public void selectRoom(Room room) {
		room.setOnMouseClicked( e -> {
			setActiveSelection(room);
			clickRoom(room);
		});
	}
	
	public void clickRoom(Room room) {
		setDisable(true);
		loadSelectedRoom(room.getNumber());
		submit_b.setDisable(true);
		clear_b.setDisable(false);
		open_b.setDisable(false);
		edit_b.setDisable(false);
	}
	
	public void clickClearImg() {
		file = null;
		photo_x.setImage(null);
	}
	
	public void setDisable(boolean disable) {
		imageSelector_b.setDisable(disable);
		clearImg_b.setDisable(disable);
		roomNo_x.setDisable(disable);
		category_x.setDisable(disable);
		capacity_x.setDisable(disable);
		ac_yes.setDisable(disable);
		ac_no.setDisable(disable);
		view_x.setDisable(disable);
		smoking_yes.setDisable(disable);
		smoking_no.setDisable(disable);
		price_night_x.setDisable(disable);
		currency_x.setDisable(disable);
	}
	
	public  void refresh() {
		roomLayout.getChildren().clear();
		roomList.clear();
		loadRooms();		
	}
	
	public void clickClear() {
		setDisable(true);
		roomNo_x.clear();
		category_x.setValue("");
		capacity_x.clear();;
		ac_yes.setSelected(false);
		ac_no.setSelected(false);
		view_x.setValue(null);
		smoking_yes.setSelected(false);
		smoking_no.setSelected(false);
		price_night_x.clear();
		footer_l.setText(null);
		photo_x.setImage(null);
		
		submit_b.setDisable(true);
		clear_b.setDisable(true);
		open_b.setDisable(true);
		edit_b.setDisable(true);
		
		refresh();

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
			
			if(!roomList.contains(roomNo) 
					&& !roomNo_x.isDisabled()) {
				
				PreparedStatement newRoom;
				try {
					newRoom = Database.con().prepareStatement
							("INSERT INTO `hoteldatabase`.`rooms` "
							+ "(`number`, `category`, `capacity`, `air_c`, `view`, `smoking`, `price_night`, `currency`,`photo`) "
							+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?,?);");
					
					newRoom.setInt(1, roomNo);
					newRoom.setString(2, category);
					newRoom.setInt(3, capacity);
					newRoom.setString(4, acValue);
					newRoom.setString(5, view);
					newRoom.setString(6, smokingValue);
					newRoom.setInt(7, price);
					newRoom.setString(8, currency);
					
					if(file != null) {
					FileInputStream f = new FileInputStream(file);
					newRoom.setBinaryStream(9, f,f.available());
					} else { 
						newRoom.setBinaryStream(9, null);
					}
					
		    		int status = newRoom.executeUpdate();
		    		
		    		if(status != 0) {
		    			addCategoty(category_x);
		    			Alert alert = new Alert(AlertType.INFORMATION);
		    			alert.setTitle("Information Dialog");
		    			alert.setHeaderText(null);
		    			alert.setContentText("Room Created Succesfully");
		    			alert.showAndWait();
		    			refresh();
		    			clickClear();		    		} 
		    		
		    		else {
		    			footer_l.setText("Something Went Wrong");
		    		}

				} catch (SQLException | IOException e) {
					e.printStackTrace();

				}
				
			} else if(roomList.contains(roomNo) && roomNo_x.isDisabled()) {
				
				PreparedStatement updateRoom;
				
				try {
					
					updateRoom = Database.con().prepareStatement
							("    UPDATE `hoteldatabase`.`rooms`\r\n"
									+ "SET\r\n"
									+ "`category` = ?,\r\n"
									+ "`capacity` = ?,\r\n"
									+ "`air_c` = ?,\r\n"
									+ "`view` = ?,\r\n"
									+ "`smoking` = ?,\r\n"
									+ "`price_night` = ?,\r\n"
									+ "`currency` = ?\r\n"
									+ "WHERE `number` = ?;");
					updateRoom.setString(1, category);
					updateRoom.setInt(2, capacity);
					updateRoom.setString(3, acValue);
					updateRoom.setString(4, view);
					updateRoom.setString(5, smokingValue);
					updateRoom.setInt(6, price);
					updateRoom.setString(7, currency);
					updateRoom.setInt(8, roomNo);
					
					PreparedStatement updatePhoto = Database.con().prepareStatement(
							"UPDATE `hoteldatabase`.`rooms`\r\n"
							+ "SET\r\n"
							+ "`photo` = ?\r\n"
							+ "WHERE number = ?;");
					Image image = photo_x.getImage();
					
					if(file != null) {
						
						FileInputStream f = new FileInputStream(file);
						updatePhoto.setBinaryStream(1, f,f.available());
						updatePhoto.setInt(2, roomNo);
						updatePhoto.executeUpdate();
					
					} else if (file == null && image == null ) {
						updatePhoto.setBinaryStream(1, null);
						updatePhoto.setInt(2, roomNo);
						updatePhoto.executeUpdate();
					}
										
		    		int status = updateRoom.executeUpdate();
		    		
		    		if(status != 0) {
		    			addCategoty(category_x);
		    			Alert alert = new Alert(AlertType.INFORMATION);
		    			alert.setTitle("Information Dialog");
		    			alert.setHeaderText(null);
		    			alert.setContentText("Room Updated Succesfully");
		    			alert.showAndWait();
		    			clickClear();
		    			clickRoom(getActiveSelection());
		    			refresh();
		    			setActiveSelection(null);
						file = null;
	        			
		    		} else {
		    			footer_l.setText("Something Went Wrong");
		    		}
		    		
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
				
			}
			
			else if(roomList.contains(roomNo) && !roomNo_x.isDisabled()) {
				footer_l.setText("Room Exists");
				
			}
			
		} else if(areEmpty()) {
			footer_l.setText("Please Complete All Fields");
		}
	}
	
     public void loadSelectedRoom(int room_number) {
    	 photo_x.setImage(null);
    	 
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
			     Blob blob = selectedRoom.getBlob("photo");
			     
			     if(blob != null) {
			     InputStream ins = blob.getBinaryStream();
			     photo_x.setImage(new Image(ins));
			     }
			     
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
    	 refresh();
	 
}
     
     public boolean areEmpty() {
 		if(roomNo_x.getText().trim().isEmpty()
 				|| category_x.getValue().trim().isEmpty()
 				|| capacity_x.getText().trim().isEmpty()
 				|| view_x.getValue() == null
 				|| price_night_x.getText().trim().isEmpty()
 				|| currency_x.getValue() == null
 				|| ac.getSelectedToggle() == null
 				|| smoking.getSelectedToggle() == null)
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
		setDisable(true);
		tg.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
		    if (newVal == null)
		        oldVal.setSelected(true);
		});
		viewAllRooms_b.setSelected(true);
		loadRooms();

	}
	
	}
