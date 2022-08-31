package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Room  extends Label {
	private int room_id;
	private SimpleIntegerProperty number;
	private SimpleStringProperty category;
	private SimpleStringProperty status;
	private SimpleIntegerProperty capacity;
	private SimpleStringProperty ac;
	private SimpleStringProperty view;
	private SimpleStringProperty smoking;
	private SimpleIntegerProperty price;
	private SimpleStringProperty currency;
	private Image photo;
	private boolean busy;
	
	public Room(int room_id, int number,String category,int capacity,String ac,
			    String view,String smoking, int price,String currency, Image photo) {	
		super();
		this.room_id = room_id;
		this.number = new SimpleIntegerProperty(number);
		this.category = new SimpleStringProperty(category);
		this.capacity = new SimpleIntegerProperty(capacity);
		this.ac = new SimpleStringProperty(ac);;
		this.view = new SimpleStringProperty(view);;
		this.smoking = new SimpleStringProperty(smoking);;
		this.price = new SimpleIntegerProperty(price);
		this.currency = new SimpleStringProperty(currency);;
		this.photo = photo;
		
		
        this.setAlignment(Pos.CENTER);
        this.setMaxHeight(300);
        this.setMaxWidth(300);
        this.setBackground(new Background(
				new BackgroundFill(Color.AQUA,
						new CornerRadii(0),
						new Insets(0))));
        this.setFont(new Font("Arial", 15));
		this.setStyle("-fx-border-color: white;");
		this.setText
		( "ROOM:" +number+"\n"
		+ "Category: " + category);
	}
	
	Room(int number,String category,String status) {
		this.number = new SimpleIntegerProperty(number);
		this.category = new SimpleStringProperty(category);
		this.status = new SimpleStringProperty(status);
	}

	public int getRoom_id() {
		return room_id;
	}

	public SimpleIntegerProperty getNumber() {
		return number;
	}

	public StringProperty getCategory() {
		return category;
	}

	public int getCapacity() {
		return capacity.get();
	}

	public String getAc() {
		return ac.get();
	}

	public String getView() {
		return view.get();
	}

	public String getSmoking() {
		return smoking.get();
	}

	public int getPrice() {
		return price.get();
	}

	public String getCurrency() {
		return currency.get();
	}

	public Image getPhoto() {
		return photo;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public StringProperty getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = new SimpleStringProperty(status);
	}
}
