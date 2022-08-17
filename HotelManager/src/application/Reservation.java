package application;

import java.sql.Date;
import java.time.LocalDateTime;

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
	private ObjectProperty<LocalDateTime> createdat;
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
			LocalDateTime createdat
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
        this.createdat = new SimpleObjectProperty<LocalDateTime>(createdat);
	}

	public StringProperty getName() {
		return name;
	}

	public StringProperty getLastName() {
		return lastName;
	}

	public ObjectProperty<Date> getCheckin() {
		return checkin;
	}

	public ObjectProperty<Date> getCheckout() {
		return checkout;
	}

	public LongProperty getTotalPrice() {
		return totalPrice;
	}

	public ObjectProperty<LocalDateTime> getCreatedat() {
		return createdat;
	}

	public SimpleIntegerProperty getRoomNum() {
		return roomNum;
	}
	
	public int getReservationId() {
		return reservation_id.get();
	}

	public LongProperty getPhoneNum() {
		return phoneNum;
	}
}
