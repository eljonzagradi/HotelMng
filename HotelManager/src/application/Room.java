package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Room  extends Label {
	
	private SimpleIntegerProperty number;
	private SimpleStringProperty type;
	private SimpleIntegerProperty price;
	
	public Room(int number, String type,int price) {
		
        this.number = new SimpleIntegerProperty(number);
        this.type = new SimpleStringProperty(type);
        this.price = new  SimpleIntegerProperty(price);
        
        this.setAlignment(Pos.CENTER);
        this.setMaxHeight(300);
        this.setMaxWidth(300);
		this.setBackground(new Background(new BackgroundFill(Color.AQUA, new CornerRadii(0), new Insets(0))));
		this.setStyle("-fx-border-color: white;");
		this.setText(  "ROOM:" +number+"\n"
				               +type+"\n"
				               +price+" LEK/NIGHT");
        
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            
        	@Override
            public void handle(MouseEvent e) {
            	
                if (e.getButton() == MouseButton.SECONDARY) {
                } else {
                	
        			CalendarController.selectedRoom = number;
        			CalendarController.priceNight = price;
        			
        			Stage stage =  (Stage) getScene().getWindow();
        			stage.close();
        		
        		Stage primaryStage = new Stage();
        		
        		try {
        			Parent root = FXMLLoader.load(getClass().getResource("/application/Calendar.fxml"));
        			Scene scene = new Scene(root);
        			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        			primaryStage.setScene(scene);
        			primaryStage.show();
        			
        		} catch(Exception ex) {
        			ex.printStackTrace();
        			
        		    }
        		
                }
                
        	}
        	
        });
                                       	
	}
	
	public int getNumber() {
		return number.get();
	}
	public int getPricePerNight() {
		return price.get();
	}
	public String getType() {
		return type.get();
	}
}
