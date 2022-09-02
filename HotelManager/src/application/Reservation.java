package application;

import java.sql.Date;
import java.sql.Timestamp;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Reservation {
	
	private SimpleStringProperty  name,lastName;
	private ObjectProperty<Date>  checkin;
	private ObjectProperty<Date> checkout;
	private LongProperty totalPrice;
	private SimpleStringProperty currency;
	private ObjectProperty<Timestamp> createdat;
	private SimpleIntegerProperty roomNum;
	private LongProperty phoneNum;
	private SimpleIntegerProperty reservation_id;
	
	public Reservation(
			int reservation_id,			
			int roomNum,
			String name,
			String lastName,
			long phonenum,
			Date checkin,
			Date checkout,
			long totalPrice,
			String currency,
			Timestamp createdat
			) 
	{
		super();
		this.reservation_id = new SimpleIntegerProperty(reservation_id);
		this.name = new SimpleStringProperty(name);
		this.lastName = new SimpleStringProperty(lastName);
		this.phoneNum = new SimpleLongProperty(phonenum);
        this.checkin = new SimpleObjectProperty<Date>(checkin);
        this.checkout = new SimpleObjectProperty<Date>(checkout);
        this.totalPrice = new SimpleLongProperty(totalPrice);
		this.currency = new SimpleStringProperty(currency);
        this.createdat = new SimpleObjectProperty<Timestamp>(createdat);
	}

	public ObjectProperty<Date> getCheckin() {
		return checkin;
	}

	public ObjectProperty<Date> getCheckout() {
		return checkout;
	}

	public ObjectProperty<Timestamp> getCreatedat() {
		return createdat;
	}

	public StringProperty getCurrency() {
		return currency;
	}

	public StringProperty getLastName() {
		return lastName;
	}

	public StringProperty getName() {
		return name;
	}

	public LongProperty getPhoneNum() {
		return phoneNum;
	}
	
	public int getReservationId() {
		return reservation_id.get();
	}

	public SimpleIntegerProperty getRoomNum() {
		return roomNum;
	}

	public LongProperty getTotalPrice() {
		return totalPrice;
	}
}
