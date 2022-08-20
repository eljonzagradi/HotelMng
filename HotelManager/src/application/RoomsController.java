package application;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
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
	@FXML private ToggleGroup tg;
	@FXML private Button addRoom_b;
	@FXML private GridPane roomLayout;
	
	public static List<Integer> roomList = new ArrayList<Integer>();
	LocalDate todayDate = LocalDate.now();

	
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
	
	public void loadRooms() {

		int rowInx = 0;
		int columnInx = 0;
		ResultSet resultSet = null;
		
		try {
			PreparedStatement availableRooms = Database.con().prepareStatement
					("SELECT id_room, ro.number, category, capacity, air_c, view, smoking, price_night, currency,check_in,check_out"
			+ "FROM  hoteldatabase.rooms ro\r\n"
			+ "LEFT JOIN hoteldatabase.reservations r\r\n"
			+ "USING (number)  \r\n"
			+ "where  ro.number NOT IN \r\n"
			+ "(SELECT number FROM  hoteldatabase.rooms ro \r\n"
			+ "LEFT JOIN hoteldatabase.reservations r "
			+ "USING (number) WHERE NOT check_in > '"+todayDate+"' AND check_out > '"+todayDate+"')\r\n"
			+ "ORDER BY ro.number,check_in");
			
			PreparedStatement allRooms = Database.con().prepareStatement
					( "SELECT id_room, ro.number, category, capacity, air_c, view, smoking, price_night, currency,check_in,check_out"
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
			    
			    if(!roomList.contains(roomNo)) {

			    	if(checkin !=null && checkout !=null ) {
			    		
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
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tg.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
		    if (newVal == null)
		        oldVal.setSelected(true);
		});
		loadRooms();
		
		
		
	}
	
}
