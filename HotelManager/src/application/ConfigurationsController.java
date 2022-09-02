package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class ConfigurationsController implements Initializable {
	
	@FXML private CheckBox checkHKM;
	
	public void updateConfigs() {
		
		boolean hkMng = checkHKM.isSelected();

		try {
			
			PreparedStatement update = Database.con().prepareStatement
					("UPDATE `hoteldatabase`.`configuratons`\r\n"
							+ "SET\r\n"
							+ "`housekeeping` = ? \r\n"
							+ "WHERE `configID` = '1';");
			
			update.setBoolean(1, hkMng);
			update.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void housekeepingMng() {
		
		int answer = -1;
		
		try {
			
			PreparedStatement checked = Database.con().prepareStatement
					("SELECT housekeeping FROM hoteldatabase.configuratons;");
			
			ResultSet result = checked.executeQuery();
			
			while(result.next()) {
				
				answer = result.getInt(1);			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(answer == 0) {
			
			checkHKM.setSelected(false);
		
		} else if(answer == 1) {
			
			checkHKM.setSelected(true);
		
		} else {
			System.out.println("Something went wrong");	
		}
		
	}
	
	public void clickSave() {
		
		updateConfigs();
		
		
		Stage primaryStage =  (Stage) checkHKM.getScene().getWindow();
		primaryStage.close();
		
		Stage stage = new Stage();
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/Rooms.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        stage.setScene(scene);
			stage.setScene(scene);
			stage.show();
			
		} 
		
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		housekeepingMng();
	}

}
