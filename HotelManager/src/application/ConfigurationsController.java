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
	@FXML private CheckBox checkRateMng;
	
	public void updateConfigs() {
		
		boolean setHKMng = checkHKM.isSelected();
		boolean setRateMng = checkRateMng.isSelected();

		

		try {
			
			PreparedStatement update = Database.con().prepareStatement
					("UPDATE `hoteldatabase`.`configuratons`\r\n"
							+ "SET\r\n"
							+ "`housekeeping` = ? ,\r\n"
							+ "`rateManagement` = ? \r\n"
							+ "WHERE `configID` = '1';\r\n"
							+ "");
			
			update.setBoolean(1, setHKMng);
			update.setBoolean(2, setRateMng);
			update.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void housekeepingMng() {
		
		boolean housekeeping = checkHKM.isSelected();
		boolean rateMng = checkRateMng.isSelected();
		
		
		try {
			
			PreparedStatement checked = Database.con().prepareStatement
					("SELECT housekeeping , rateManagement FROM hoteldatabase.configuratons;");
			
			ResultSet result = checked.executeQuery();
			
			while(result.next()) {
				
				housekeeping = result.getBoolean(1);
				rateMng = result.getBoolean(2);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			checkHKM.setSelected(housekeeping);
			checkRateMng.setSelected(rateMng);
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
