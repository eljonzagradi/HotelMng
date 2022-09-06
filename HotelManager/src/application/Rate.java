package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Rate {

	
	private SimpleStringProperty category;
	private SimpleIntegerProperty price;
	
	Rate(String category, int price) {
		this.category = new SimpleStringProperty(category);
		this.price = new SimpleIntegerProperty(price);
		
	}
	
	public StringProperty getCategory() {
		return category;
	}

	public IntegerProperty getPrice() {
		return price;
	}

	public void setCategory(SimpleStringProperty category) {
		this.category = category;
	}

	public void setPrice(SimpleIntegerProperty price) {
		this.price = price;
	}

}
