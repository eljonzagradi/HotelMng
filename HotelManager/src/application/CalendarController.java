package application;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CalendarController implements Initializable {
	
	public static int priceNight;
  	public static int selectedRoom;
	public static String tempCurrency;
	
	//Table FXML components:
    @FXML private TableView<Reservation> reservationsTable;
	@FXML private TableColumn<Reservation,String> name_c;
	@FXML private TableColumn<Reservation,String> lastname_c;
	@FXML private TableColumn<Reservation,Date> checkin_c;
	@FXML private TableColumn<Reservation,Date> checkout_c;
	@FXML private TableColumn<Reservation,Number> totalPrice_c;
	
	@FXML private TableColumn<Reservation,String> currency_c;
	@FXML private TableColumn<Reservation,Timestamp> createdAt_c;
	@FXML private TableColumn<Reservation,Number> phone_c;
	
	//Calendar FXML components:
	@FXML private VBox view;
	@FXML private GridPane calendar;
	@FXML private GridPane weekDays;

	@FXML private Button prevMonth;
    @FXML private Button nextMonth;
    
    @FXML private Text calendarTitle;
    
    //Calendar variables:
    private ArrayList<DayNode> allCalendarDays = new ArrayList<>(35);
	private YearMonth currentYearMonth;
	
    //New Reservation FXML components:
	@FXML private Label room_x;
    @FXML private TextField name_x;
    @FXML private TextField lastName_x;
    @FXML private TextField checkin_x;
	@FXML private TextField checkout_x;
    @FXML private ToggleButton setCheckin_b;
    @FXML private ToggleButton setCheckout_b;
    @FXML private TextField totalPrice_x;
    @FXML private Button create_b;
    @FXML private Button changeRoom_b;
    @FXML private Button deleteReservation;
    @FXML private TextField phonenum_x;
    @FXML private ChoiceBox<String> rate_x;
    
	@FXML private Label errorsDisplay;
	@FXML private Button edit_b;
	@FXML private Button update_b;
        
    @FXML private TextField price_night_x;
	@FXML private ChoiceBox<String> currency_x;
	@FXML private Label displayCurrency_l;
	
	//New Reservation variables:
	ObservableList<String> currencies = FXCollections.observableArrayList("ALL"/* , "EUR","USD","GPD" */);
	ObservableList<Reservation> reservations = FXCollections.observableArrayList();
	ObservableList<LocalDate> busyDates = FXCollections.observableArrayList();
	ObservableList<LocalDate> checkins = FXCollections.observableArrayList();
	ObservableList<String> rateCategories = FXCollections.observableArrayList("No Selection");
	ObservableList<Rate> ratesList = FXCollections.observableArrayList();
    ObservableList<LocalDate> temp = FXCollections.observableArrayList();
    List<LocalDate> listOfDates = null;
    Reservation activeReservation = null;
	ToggleGroup toggleGR = new ToggleGroup();
	
	private LocalDate checkin;
	private LocalDate  checkout;
	private long totalPrice;
    private DayNode lastSelected = null;
    
    public void loadRates() {
    	
    	try {
			
			PreparedStatement rates = Database.con().prepareStatement
					("SELECT * FROM hoteldatabase.categories;");
			ResultSet getRates = rates.executeQuery();
			
			while(getRates.next()) {
				
				String cat = getRates.getString(1);
				int price = getRates.getInt(2);
				
				ratesList.add(new Rate(cat,price));
				rateCategories.add(cat);
				
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			}
    	rate_x.setItems(null);
    	rate_x.setItems(rateCategories);
    	
    }
    
    public void chooseRate() {
    	
    	rate_x.getSelectionModel().selectedItemProperty().addListener( e -> {
    		
    		String category = rate_x.getSelectionModel().getSelectedItem();
    		
    		if(category.equals("No Selection")){
    			
    			price_night_x.setText(Integer.toString(priceNight));
    			
    		} else {
    		
    		for(Rate r : ratesList) {
    			
    			if(r.getCategory().get().equals(category)) {
    				
    				price_night_x.setText(Integer.toString(r.getPrice().get()));
    				break;
    			}
    			
    		}
    	}
    		

    		
    	});
    	
    }
    
    public boolean areEmpty() {
		
		if (
				name_x.getText().isEmpty()
			||	lastName_x.getText().isEmpty()
			||  phonenum_x.getText().isEmpty()
			||  checkin_x.getText().isEmpty()
			||  checkout_x.getText().isEmpty()
			||  getTotalPrice() < 0
		   ) {
			
			return true;
			
		} else {
			
			return false;
			
		}	
	}
    
	public long calcPrice(LocalDate checkin, LocalDate checkout) {
		
		long price = 0;
    	long daysNo = 0;
    	
    	if(checkin != null && checkout != null && price_night_x.getText().trim() !=null) {
    		
    		daysNo = Duration.between(
    				checkin.atStartOfDay(), 
    				checkout.atStartOfDay()).toDays();
    		int pn = Integer.parseInt(price_night_x.getText());

    		price = pn * daysNo;
    	}
    	
    	return price;
    	
	}

	public void CalendarView(YearMonth yearMonth) 
    {
        currentYearMonth = yearMonth;
      
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
            	DayNode dateCell = new DayNode();
                calendar.add(dateCell,j,i);
                allCalendarDays.add(dateCell);
            }
        }
  
        populateCalendar(yearMonth);
    }
	
	public void clickAddReservations() {
		
		if(!areEmpty()) {
			
			try {
				
				PreparedStatement create =
						
						Database.con().prepareStatement
						("INSERT INTO `hoteldatabase`.`reservations` "
						+ "(`number`, `name`, `lastname`, `phone_number`, `check_in`, "
						+ "`check_out`, `total_price`, `currency`, `created_at`)"
					    + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				
				create.setInt(1, getSelectedRoom());
				create.setString(2, name_x.getText());
				create.setString(3, lastName_x.getText());
				create.setLong(4, Long.parseLong(phonenum_x.getText()));
				create.setDate(5, java.sql.Date.valueOf(getCheckin()));
				create.setDate(6, java.sql.Date.valueOf(getCheckout()));
				create.setLong(7, Long.parseLong(totalPrice_x.getText()));
				create.setString(8, displayCurrency_l.getText());
				create.setTimestamp(9,  Timestamp.valueOf(LocalDateTime.now()));
				
				create.executeUpdate();
	            clickClear();
				refresh();
			}
			
			catch (SQLException e1) {
				e1.printStackTrace();	
			}
		} 
		
		else 
		{
			errorsDisplay.setText("Complete All Fields");			
		}
		
	}
	
	public void clickChangeRoom() 
    { 
		busyDates.clear();
		
		Stage stage = (Stage) changeRoom_b.getScene().getWindow();
	    stage.close();
		
		Stage primaryStage = new Stage();
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/Rooms.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clickClear() {
		setCheckin(null);
		setCheckout(null);
		name_x.clear();
		lastName_x.clear();
		checkin_x.setText(null);
		checkout_x.setText(null);
		totalPrice_x.setText(null);
		phonenum_x.setText(null);
		setCheckin_b.setSelected(true);
		errorsDisplay.setText(null);
		currency_x.setValue(tempCurrency);
		price_night_x.setText(priceNight+"");
		edit_b.setDisable(true);
		update_b.setDisable(true);
		create_b.setDisable(false);
		deleteReservation.setDisable(true);
		setDisable(false);
		
		temp.clear();
		reservations.clear();
        busyDates.clear();
        checkins.clear();
	    allCalendarDays.clear();
	    calendar.getChildren().clear();
		loadDataFromDB();
		CalendarView(YearMonth.now());
	}
		
	public void clickDeleteReservation() {
		
		Reservation selectedItem = 
				reservationsTable.getSelectionModel().getSelectedItem();
		
		if (selectedItem != null) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle("Confirmation Dialog");
	    	alert.setHeaderText(null);
	    	alert.setContentText("Do you want to delete this reservation?");

	    	Optional<ButtonType> result = alert.showAndWait();
	    	
	    	if (result.get() == ButtonType.OK) {
	    		
	    		try {
	    			
	    			PreparedStatement delete = Database.con().prepareStatement(
	    					"DELETE FROM `hoteldatabase`.`reservations` "
	    			+ "WHERE (`id_reservation` = ? );");
	    			
	    			delete.setInt(1, selectedItem.getReservationId());
	    			delete.executeUpdate();
	    			
	    			PreparedStatement deleteLogs = Database.con().prepareStatement
	    					( "INSERT INTO `hoteldatabase`.`log_reservations` "
	    				 + "(`userName`, `action`, `reservationBefore`, `reservationAfter`, `timestamp`) "
	    				 + "VALUES ('Eljon', 'Deletion', ?, 'Not Existent', ? );" );
	    			
	    			String reservation = 
	    					 "Id: "  + selectedItem.getReservationId()
	    				   + "| Name:  " + selectedItem.getName().get()
	    				   + "| LastName: " + selectedItem.getLastName().get()
	    				   + "| Tel: " + selectedItem.getPhoneNum().get()
	    				   + "| Check-in: " + selectedItem.getCheckin().get()
	    				   + "| Check-out: " + selectedItem.getCheckout().get()
	    				   + "| TotalPrice " + selectedItem.getTotalPrice().get()
	    				   + "| Currency " + selectedItem.getCurrency().get()
	    				   + "| DateOfCreation " + selectedItem.getCreatedat().get();
	    			
	    			deleteLogs.setString(1, reservation);
	    			deleteLogs.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
	    			deleteLogs.execute();
	    			
		            setCheckin(null);
		            setCheckout(null);
		            clickClear();
	    		} catch (SQLException e1) {
	    			e1.printStackTrace();
	    			
	    		}
	    		
	    	}
	    	
		}
		
	}

	public void clickReservation() {
    
		reservationsTable.setOnMouseClicked( event -> {
			int m2 = 0;
			setCheckin(null);
			setCheckout(null);
			LocalDate todayDate = LocalDate.now();
			temp.clear();
			edit_b.setDisable(false);
			deleteReservation.setDisable(false);

			Reservation	selectedItem = reservationsTable.getSelectionModel().getSelectedItem();
			activeReservation = selectedItem;
			if(selectedItem != null) {
				
				if(selectedItem.getCheckin().get().toLocalDate().isBefore(todayDate)
						&& todayDate.isAfter(selectedItem.getCheckout().get().toLocalDate())) {
					
					deleteReservation.setDisable(true);
					edit_b.setDisable(true);				

		    		} 
		    		
				int m1 = currentYearMonth.getMonthValue();
				m2 = selectedItem.getCheckin().get().toLocalDate().getMonthValue();
				
				while(m2 != m1 ) {
					if(m1 < m2) {
						nextMonth();
						m1++;
					} else {
						previousMonth();
						m1--;
					}
					
				}
				
				create_b.setDisable(true);
				update_b.setDisable(true);
				setDisable(true);
				LocalDate start = selectedItem.getCheckin().get().toLocalDate();
				LocalDate end = selectedItem.getCheckout().get().toLocalDate();
				List<LocalDate>	l = start.datesUntil(end)
						.collect(Collectors.toList());
				
				temp.addAll(l);
				populateCalendar(currentYearMonth);
				
				int id = selectedItem.getReservationId();			
				String nameBefore = selectedItem.getName().get();
				String lastnameBefore = selectedItem.getLastName().get();
				String phoneNumBefore = Long.toString(selectedItem.getPhoneNum().get());
				LocalDate checkinBefore = selectedItem.getCheckin().get().toLocalDate();
				LocalDate checkoutBefore = selectedItem.getCheckout().get().toLocalDate();
				long totalPriceBefore = selectedItem.getTotalPrice().get();
				String currencyBefore = selectedItem.getCurrency().get();
				String timestampBefore = selectedItem.getCreatedat().get().toString();
				
				long    daysNo = Duration.between(
						checkinBefore.atStartOfDay(), 
						checkoutBefore.atStartOfDay()).toDays();
				
				long priceNightBefore = totalPriceBefore/daysNo;
				
				String reservationBefore = 
						 "Id: "  + id
					   + "| Name:  " + nameBefore 
					   + "| LastName: " + lastnameBefore
					   + "| Tel: " + phoneNumBefore
					   + "| Check-in: " + checkinBefore
					   + "| Check-out: " + checkoutBefore
					   + "| TotalPrice " + totalPriceBefore
					   + "| Currency " + currencyBefore
					   + "| DateOfCreation " + timestampBefore;
				
				name_x.setText(nameBefore);
				lastName_x.setText(lastnameBefore);
				phonenum_x.setText(phoneNumBefore);
				checkin_x.setText(checkinBefore.toString());
				checkout_x.setText(checkoutBefore.toString());
				price_night_x.setText(Long.toString(priceNightBefore));
				currency_x.setValue(currencyBefore);
				displayCurrency_l.setText(currencyBefore);
				totalPrice_x.setText(Long.toString(totalPriceBefore));
				
				selectModify(selectedItem, reservationBefore);
					
			}
			
		}
		
				);
		
		
	}
	
	public ArrayList<DayNode> getAllCalendarDays() {
        return allCalendarDays;
    }
	
	public LocalDate getCheckin() {
		return checkin;
	}
	
	public LocalDate getCheckout() {
		return checkout;
	}

	public DayNode getLastSelected() {
		return lastSelected;
	}
	
	public int getPriceNight() {
		return priceNight;	
	}
	
	public int getSelectedRoom() {
		return selectedRoom;	
	}
	
	public long getTotalPrice() {
		return totalPrice;
	}
		
	public void loadDataFromDB() {
				
		ResultSet resultSet;
		
		try {
			PreparedStatement load = Database.con().prepareStatement
					("select *\r\n"
					           + "from hoteldatabase.reservations r\r\n"
					           + "where number = ? \r\n"
					           );
			
			load.setInt(1, getSelectedRoom());
			resultSet = load.executeQuery();
			
			while (resultSet.next()) {
		    	
				int resevation_id = resultSet.getInt("id_reservation");
		    	String name = resultSet.getString("name");
		    	String lastname  = resultSet.getString("lastname");
		    	long phonenum = resultSet.getInt("phone_number");
		    	Date checkin  = resultSet.getDate("check_in");
		    	Date checkout  = resultSet.getDate("check_out");
		    	int totalPrice  = resultSet.getInt("total_price");
		    	String currency = resultSet.getString("currency");
		    	Timestamp timestamp = resultSet.getTimestamp("created_at");
		    	
		    	reservations.add(
		    			new Reservation(    
		    					resevation_id,
		    					getSelectedRoom(),
		    					name,
		    					lastname,
		    					phonenum,
		    				    checkin,
		    					checkout,
		    					totalPrice,
		    					currency,
		    					timestamp
		    					));
		    	}
  
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		reservationsTable.setItems(reservations);
		populateBusyDates();
	}
	
	public void nextMonth() 
    {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);

    }
	
	public void populateBusyDates() {
		
		try {
			
			PreparedStatement ps = Database.con().prepareStatement
					("select check_in , check_out\r\n"
					+ "from hoteldatabase.reservations\r\n"
					+ "where number = ?;");
			ps.setInt(1, getSelectedRoom());
			
			ResultSet resultSet = ps.executeQuery();
			
			while(resultSet.next()) {
				
				Date checkin  = resultSet.getDate("check_in");
		    	Date checkout  = resultSet.getDate("check_out");
		    	
		    	checkins.add(checkin.toLocalDate());
		    	
				listOfDates = (
						checkin.toLocalDate().plusDays(1))
						.datesUntil(checkout.toLocalDate())
						.collect(Collectors.toList());
				
				for(LocalDate date : listOfDates) {
					if(!busyDates.contains(date)) {
						busyDates.add(date);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		   busyDates.addAll(checkins);
		   
	}
	
	public void populateCalendar(YearMonth yearMonth) 
    {   
    	int y = yearMonth.getYear(); 
    	int m = yearMonth.getMonthValue();
    	
        LocalDate calendarDate = LocalDate.of(y,m, 1);
        
        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }
        
        for (DayNode dateCell : allCalendarDays) {

        	String txt = String.valueOf(calendarDate.getDayOfMonth());
        	dateCell.setVisible(true);
        	dateCell.setDate(calendarDate);
        	dateCell.setText(txt);
        	if(calendarDate.getMonthValue() != m) {
            	dateCell.setVisible(false);	
        	}
    		dateCell.setDisable(false);
    		selectionHandler(dateCell);
        	select(dateCell);
        	
            calendarDate = calendarDate.plusDays(1);
        }
       
        calendarTitle.setText(
        		yearMonth.getMonth().toString() 
        		+ " " +
        		String.valueOf(yearMonth.getYear()));
    }
	
	public void previousMonth() 
    {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);

    }

	public void refresh() {
		reservations.clear();
        busyDates.clear();
        checkins.clear();
		loadDataFromDB();
		populateCalendar(currentYearMonth);
		
	}
		
	public void select(DayNode selected) 
    {
		selected.setOnMouseClicked(
    			
    			e -> {
    				
					if(!update_b.isDisabled()) {
						busyDates.removeAll(temp);
					}
    				
    				if(!busyDates.contains(selected.getDate()) && selected.getText() != null 
    						&& !(update_b.isDisabled() && create_b.isDisabled())) 
    				
    				{
    					
    					if(setCheckin_b.isSelected())
    					
    					{
    						setCheckin(selected.getDate());
    						checkin_x.setText(getCheckin().toString());
    						setCheckout_b.setSelected(true);
    					}
    					
    					else if(setCheckout_b.isSelected() && getCheckin() != null)
    					
    					{
    					setCheckout(selected.getDate());
        				checkout_x.setText(getCheckout().toString());
        				
    					} else if(setCheckout_b.isSelected() && getCheckin() == null) {
    					
    					checkout_x.setText("^^^^^^");
    					
    					}
    					busyDates.clear();
    					checkins.clear();
    					populateBusyDates();
    					populateCalendar(currentYearMonth);
    					setTotalPrice(calcPrice(getCheckin(),getCheckout()));
    					totalPrice_x.setText(Long.toString(getTotalPrice()));
    					
    				}
    				
    			});
		
    }
	
	public void selectionHandler(DayNode dateCell) {
    	
    	busyDates.add(getCheckin());
    	busyDates.add(getCheckout());
    	
    	LocalDate future = null;
    	
    	if(dateCell.getDate().compareTo(LocalDate.now()) < 0) {
    		dateCell.setDisable(true);
    	}
    	
    	if(getCheckin() != null 
    			&& dateCell.getDate().compareTo(getCheckin()) <= 0) {
    		dateCell.setDisable(true);
    	}
    			
		if(getCheckout() != null 
    			&& getCheckin().compareTo(dateCell.getDate()) 
    			* dateCell.getDate().compareTo(getCheckout()) >=0) {
			
			dateCell.setStyle("-fx-background-color:yellow");
    	
    	} 
    	else 
    	{  
    		dateCell.setStyle(null);
    		
    	}
		

		
		if(busyDates.contains(dateCell.getDate())) {
    		
    		if( (getCheckin() != null 
    				
    				&& dateCell.getDate().compareTo(getCheckin()) == 0)
    				
    				|| (getCheckout() != null 
    				
    				&& dateCell.getDate().compareTo(getCheckout()) == 0 ) ) 
    		{
    			dateCell.setStyle("-fx-background-color:red");
    		} 
    		
    		else if (temp.contains(dateCell.getDate())) 
    		
    		{
    			dateCell.setStyle("-fx-background-color: green");

        		
    		} else {
    			
    			dateCell.setStyle("-fx-background-color: aqua");
    			
    		}
		}
    	
		if(checkins != null && getCheckin() != null) {
		FXCollections.sort(checkins);
  		for(LocalDate date : checkins) {
			if(date.isAfter(getCheckin())) {
				future = date;
				break;
			}}
		}
    	
   		if(future != null) 
   		{
   			busyDates.remove(future);
   		}
		
   		if(future != null && getCheckin() != null 
				&& dateCell.getDate().isAfter(future)) {
			
			dateCell.setDisable(true);
		}

    }
	
	
	
    public void selectModify(Reservation r , String reservationBefore) {

			update_b.setOnAction( e -> {
				int id = r.getReservationId();
				
				if(!areEmpty()) {
					
					String nameAfter = name_x.getText();
					String lastnameAfter = lastName_x.getText();
					Long phoneNumAfter = Long.parseLong(phonenum_x.getText());
					Date checkinAfter = java.sql.Date.valueOf(checkin_x.getText());
					Date checkoutAfter = java.sql.Date.valueOf(checkout_x.getText());
					Long totalPriceAfter = Long.parseLong(totalPrice_x.getText());
					String currencyAfter = displayCurrency_l.getText();
					Timestamp timestampAfter = Timestamp.valueOf(LocalDateTime.now());
					
					String reservationAfter = 
							 "Id: "  + id
						   + "| Name:  " + nameAfter 
						   + "| LastName: " + lastnameAfter
						   + "| Tel: " + phoneNumAfter
						   + "| Check-in: " + checkinAfter
						   + "| Check-out: " + checkoutAfter
						   + "| TotalPrice " + totalPriceAfter
					       + "| Currency " + currencyAfter;
						
				 try {
						PreparedStatement update = 
								Database.con().prepareStatement
								("UPDATE hoteldatabase.reservations\r\n"
										+ "SET \r\n"
										+ "name = ?,\r\n"
										+ "lastname = ?,\r\n"
										+ "phone_number = ?,\r\n"
										+ "check_in = ?,\r\n"
										+ "check_out = ?,\r\n"
										+ "total_price = ?,\r\n"
										+ "currency = ?,\r\n"
										+ "created_at = ?\r\n"
										+ "WHERE\r\n"
										+ "id_reservation = ?;");
						
						update.setString(1, nameAfter);
						update.setString(2, lastnameAfter);
						update.setLong(3, phoneNumAfter);
						update.setDate(4, checkinAfter);
						update.setDate(5, checkoutAfter);
						update.setLong(6, totalPriceAfter);
						update.setString(7, currencyAfter);
						update.setTimestamp(8, timestampAfter);
						update.setInt(9, id);
						update.executeUpdate();
						
						PreparedStatement updateLogs = Database.con().prepareStatement
		    				 ( "INSERT INTO `hoteldatabase`.`log_reservations` "
		    				 + "(`userName`, `action`, `reservationBefore`, `reservationAfter`, `timestamp`) "
		    				 + "VALUES ('Eljon', 'Modification', ?, ?, ? );" );

						updateLogs.setString(1, reservationBefore);
						updateLogs.setString(2, reservationAfter);
						updateLogs.setTimestamp(3, timestampAfter);
						updateLogs.execute();

					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				 
		            clickClear();
		            update_b.setDisable(true);
		            create_b.setDisable(false);
					refresh();
				}
				else {
					errorsDisplay.setText("Complete All Fields");	
				}
				
			}
			
					);
			
		}

    public void setAllCalendarDays(ArrayList<DayNode> allCalendarDays) {
        this.allCalendarDays = allCalendarDays;
    }
    
    public void setCheckin(LocalDate checkin) {
		
        this.checkin = checkin;
	}
        
	public void setCheckout(LocalDate checkout) {
		
        this.checkout = checkout;
	}
	
    public void setDisable(boolean disable) {
		
		name_x.setDisable(disable);
		lastName_x.setDisable(disable);
		phonenum_x.setDisable(disable);
	
		checkin_x.setDisable(disable);
		checkout_x.setDisable(disable);

		setCheckin_b.setDisable(disable);
		setCheckout_b.setDisable(disable);
		
		price_night_x.setDisable(disable);
		currency_x.setDisable(disable);
		totalPrice_x.setDisable(disable);
	
	}

    public void setPriceNight() {
		priceNight = Integer.parseInt(price_night_x.getText());
	}

    public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}
    
    public void someInitalValues() {
    	
    	rate_x.getSelectionModel().select(0);
		
		price_night_x.textProperty().addListener( e -> {
				setTotalPrice(calcPrice(getCheckin(),getCheckout()));
				totalPrice_x.setText(Long.toString(getTotalPrice()));
		});
		
		currency_x.setItems(currencies);
		currency_x.setValue(tempCurrency);
		displayCurrency_l.setText(tempCurrency);
		
		currency_x.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			displayCurrency_l.setText(newValue);
		});
		
		edit_b.setOnAction
		( e -> {
			Reservation	selectedItem = reservationsTable.getSelectionModel().getSelectedItem();
			
			if(selectedItem != null) {
				setDisable(false);
				update_b.setDisable(false);
				create_b.setDisable(true);
				
				if(selectedItem.getCheckin().get().toLocalDate().compareTo(LocalDate.now()) <0) {
					setCheckin(LocalDate.parse(checkin_x.getText()));
					setCheckin_b.setDisable(true);
					setCheckout_b.setSelected(true);
				}
						
	            busyDates.clear();
	            populateBusyDates();
	            populateCalendar(currentYearMonth);

			}			
		});
				
		toggleGR.selectedToggleProperty()
		.addListener((obsVal, oldVal, newVal) -> 
		{
		    if (newVal == null)
		        oldVal.setSelected(true);
		});
		
		setCheckin_b.setToggleGroup(toggleGR);
		setCheckout_b.setToggleGroup(toggleGR);
		setCheckin_b.setSelected(true);
		room_x.setText("ROOM: " + getSelectedRoom());
		price_night_x.setText(priceNight+"");
		
		setCheckin_b.setOnMouseClicked(checkin -> {

			setCheckin(null);
			setCheckout(null);
			setTotalPrice(0);
			checkin_x.setText(null);
			checkout_x.setText(null);
			totalPrice_x.setText(null);
            busyDates.clear();
            populateBusyDates();
            populateCalendar(currentYearMonth);

		});
	}

   	public void tableSetup() 
	{
		name_c.setCellValueFactory(name -> name.getValue().getName());
		lastname_c.setCellValueFactory(surname -> surname.getValue().getLastName());
		phone_c.setCellValueFactory(phonenum -> phonenum.getValue().getPhoneNum());
		checkin_c.setCellValueFactory(checkin -> checkin.getValue().getCheckin());
		checkout_c.setCellValueFactory(checkout -> checkout.getValue().getCheckout());
		totalPrice_c.setCellValueFactory(totalPrice -> totalPrice.getValue().getTotalPrice());
		currency_c.setCellValueFactory(currency -> currency.getValue().getCurrency() );
		createdAt_c.setCellValueFactory(createdat -> createdat.getValue().getCreatedat());
		
		loadDataFromDB();
	}
   	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadRates();
		chooseRate();
		clickReservation();
		tableSetup();
		someInitalValues();
		CalendarView(YearMonth.now());
		
	}
}