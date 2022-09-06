package application;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class HousekeepingController implements Initializable {
	
	@FXML private TableView<Room> table;
	@FXML private TableColumn<Room,Number> roomNo_c;
	@FXML private TableColumn<Room, String> roomType_c;
	@FXML private TableColumn<Room, String> roomStatus_c;
	@FXML private ChoiceBox<String> roomStatus_x;
	@FXML private Button apply_b;
	@FXML private Button clear_b;
	@FXML private Button goback_b;


	
	ObservableList<Room> roomList = FXCollections.observableArrayList();
	ObservableList<String> statusList = FXCollections.observableArrayList("Clean", "Dirty" , "Out of Service");


	
	public void clearSelection() {
		
		table.getSelectionModel().clearSelection();
		roomStatus_x.setValue("Select to Update");
		roomStatus_x.setDisable(true);
		clear_b.setDisable(true);
		apply_b.setDisable(true);
	}

	public void goBack() {
		
		Stage primaryStage =  (Stage) goback_b.getScene().getWindow();
		primaryStage.close();
		
		Stage stage = new Stage();
		
	
			Parent root;
			try {
				root = FXMLLoader.load(getClass().getResource("/application/Rooms.fxml"));
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		        stage.setScene(scene);
				stage.setScene(scene);
				stage.show();
			
			} catch (IOException e) {
				e.printStackTrace();
			}

		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		roomStatus_x.setValue("Select to Update");
		selectRoom();
		roomsTable();
	}
	
	public void loadRooms() {
		
		try {
			
			PreparedStatement select = Database.con().prepareStatement
					("SELECT number,type,status\r\n"
					+ "FROM hoteldatabase.rooms;");
			
			ResultSet rs = select.executeQuery();
			
			while(rs.next()) {
				
				int number = rs.getInt("number");
				String category = rs.getString("type");
				String status = rs.getString("status");
				
				Room room = new Room(number, category, status);
				roomList.add(room);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Collections.sort(roomList, Comparator.comparing(room -> room.getStatus().get()));
		table.setItems(roomList);

	}
	
	public void roomsTable() {
		roomNo_c.setCellValueFactory(number -> number.getValue().getNumber());
		roomType_c.setCellValueFactory(category -> category.getValue().getCategory());
		roomStatus_c.setCellValueFactory(status -> status.getValue().getStatus());
		
		loadRooms();

		table.setRowFactory(row -> new TableRow<Room>() {
            @Override
            public void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                    
                } else if (item.getStatus().get().equalsIgnoreCase("Clean")) {
                    this.setStyle("    -fx-background-color: -fx-table-cell-border-color, green;\r\n"
                    		    + "    -fx-background-insets: 0, 0 0 1 0;");
                    	
                } else if(item.getStatus().get().equalsIgnoreCase("Dirty")) {
                    this.setStyle("    -fx-background-color: -fx-table-cell-border-color, red;\r\n"
                		        + "    -fx-background-insets: 0, 0 0 1 0;");
                } else {
                    this.setStyle("    -fx-background-color: -fx-table-cell-border-color, yellow;\r\n"
            		            + "    -fx-background-insets: 0, 0 0 1 0;");
                }
            }
        });
		roomStatus_x.setItems(statusList);
	}
	
	public void selectRoom() {
		
		table.setOnMouseClicked( e -> {
			
			Room selectedRoom = 
					table.getSelectionModel().getSelectedItem();
			
			if(selectedRoom != null) {
				
				roomStatus_x.setValue(selectedRoom.getStatus().get());
				updateStatus(selectedRoom);
				roomStatus_x.setDisable(false);
				clear_b.setDisable(false);
				apply_b.setDisable(false);
				
			}
			
		});
		
	}
	
	public void updateStatus(Room r) {
		apply_b.setOnAction( e -> {
			
			String status = roomStatus_x.getValue();
			int number = r.getNumber().get();
			
			try {
				PreparedStatement updateStatus = Database.con().prepareStatement
						("UPDATE `hoteldatabase`.`rooms`\r\n"
								+ "SET\r\n"
								+ "`status` = ?\r\n"
								+ "WHERE `number` = ?;");
				
				updateStatus.setString(1, status);
				updateStatus.setInt(2, number);
				
				int success = updateStatus.executeUpdate();
				
				if(success == 1) {
					
					clearSelection();
					r.setStatus(status);
					Collections.sort(roomList, Comparator.comparing(room -> room.getStatus().get()));
					table.refresh();

				}
			
			} catch (SQLException e1) {
				e1.printStackTrace();
			}			
		});
		
	}

}
