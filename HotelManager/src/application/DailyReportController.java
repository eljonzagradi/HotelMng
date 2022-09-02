package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DailyReportController implements Initializable {
	@FXML private Label date_x;
	@FXML private Label totalRooms_l;
	@FXML private Label bookedRooms_l;
	@FXML private Button goback_b;
	
	public int getBookedRooms() {
		
		int answer = 0;
		Date today = Date.valueOf(LocalDate.now());
		
		try {
			
			PreparedStatement bookedRooms = Database.con().prepareStatement
					("SELECT DISTINCT COUNT(number)\r\n"
							+ "FROM hoteldatabase.reservations\r\n"
							+ "WHERE check_in <= ? AND check_out > ?;");
			bookedRooms.setDate(1, today);
			bookedRooms.setDate(2, today);
			ResultSet result = bookedRooms.executeQuery();
			
			while(result.next()) {
				
				answer = result.getInt(1);
				
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	public int getTotalRooms() {
		
		int answer = 0;
		
		try {
			
			PreparedStatement totalRooms = Database.con().prepareStatement
					("SELECT COUNT(number)\r\n"
					+ "FROM hoteldatabase.rooms;");
			ResultSet result = totalRooms.executeQuery();
			
			while(result.next()) {
				
				answer = result.getInt(1);
				
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return answer;
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
		
		date_x.setText(LocalDate.now().toString());
		totalRooms_l.setText(Integer.toString(getTotalRooms()));
		bookedRooms_l.setText(Integer.toString(getBookedRooms()));
	}

}
