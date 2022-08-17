package application;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import javafx.beans.property.ObjectProperty;
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
	
	//Table FXML components:
    @FXML private TableView<Reservation> reservationsTable;
    
	@FXML private TableColumn<Reservation,String> name_c;
	@FXML private TableColumn<Reservation,String> lastname_c;
	@FXML private TableColumn<Reservation,Date> checkin_c;
	@FXML private TableColumn<Reservation,Date> checkout_c;
	@FXML private TableColumn<Reservation,Number> totalPrice_c;
	@FXML private TableColumn<Reservation,LocalDateTime> createdAt_c;
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
    @FXML private Button addReservation_b;
    @FXML private Button changeRoom_b;
    @FXML private Button deleteReservation;
    @FXML private TextField phonenum_x;
    @FXML private Label errorsDisplay;
        
    //New Reservation variables:
	ObservableList<Reservation> reservations = FXCollections.observableArrayList();
	static ObservableList<LocalDate> busyDates = FXCollections.observableArrayList();
	ObservableList<LocalDate> checkins = FXCollections.observableArrayList();
	ObservableList<LocalDate> tempList = FXCollections.observableArrayList();


	List<LocalDate> listOfDates = null;

    ToggleGroup toggleGR = new ToggleGroup();
    private LocalDate checkin;
    private LocalDate  checkout;
	private long totalPrice;
	public static int priceNight;
	public static int selectedRoom; 
    private DayNode lastSelected = null;

////////////////////////////////////////////////////////    
/*              Getters & Setters                     */  
////////////////////////////////////////////////////////
	public DayNode getLastSelected() {
		return lastSelected;
	}
	public LocalDate getCheckin() {
		return checkin;
	}

	public LocalDate getCheckout() {
		return checkout;
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
		
	public void setCheckin(LocalDate checkin) {
		
        this.checkin = checkin;
	}

	public void setCheckout(LocalDate checkout) {
		
        this.checkout = checkout;
	}
	
	public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}
	
/////////////////////////////////////////////////////////
	
	public void clickAddReservations() {
		
		if(!areEmpty()) {
			try {
				PreparedStatement ps =
						Database.con().prepareStatement
						("INSERT INTO `hoteldatabase`.`reservations` "
						+ "(`name`, `surname`,`phone_number`, `check_in`, `check_out`, `total_price`, `created_at`, `number`) "
						+ "VALUES ('"
						+ name_x.getText()
						+ "', '"
					    + lastName_x.getText()
						+ "', '"
						+ Long.parseLong(phonenum_x.getText())
						+ "', '"
						+ java.sql.Date.valueOf(getCheckin()) 
						+ "', '"
						+ java.sql.Date.valueOf(getCheckout())
						+ "', '"
						+ Long.parseLong(totalPrice_x.getText())
						+ "', '"
						+ LocalDateTime.now()
						+ "', '"
						+ getSelectedRoom()
						+ "');\r\n"
						
								);
				
				ps.executeUpdate();
                clickClear();
			    refresh();
			}
			
			catch (SQLException e1) {
				
				e1.printStackTrace();
				
			}
			
		} 
		
		else { 
			errorsDisplay.setText("Please Complete all the required fields"); 
		}
		
	}
	
	public void clickClear() {
		name_x.clear();
		lastName_x.clear();
		checkin_x.setText(null);
		checkout_x.setText(null);
		totalPrice_x.setText(null);
		phonenum_x.setText(null);
		setCheckin_b.setSelected(true);
		errorsDisplay.setText(null);
		setCheckin(null);
		setCheckout(null);
	    refresh();
	}
	
	public void refresh() {
		reservations.clear();
        busyDates.clear();
        checkins.clear();
		loadDataFromDB();
		populateCalendar(currentYearMonth);
	}
	
	public void cilckEditReservation() {
		
//		Reservation selectedItem = 
//				reservationsTable.getSelectionModel().getSelectedItem();
//		
//		if (selectedItem != null) {
//			
//			setCheckin(null);
//			setCheckout(null);
//			LocalDate start = selectedItem.getCheckin().get().toLocalDate(); 
//			LocalDate end = selectedItem.getCheckout().get().toLocalDate();
//			name_x.setText(selectedItem.getName().get());
//			lastName_x.setText(selectedItem.getLastName().get());
//			checkin_x.setText(start.toString());
//			checkout_x.setText(end.toString());
//			totalPrice_x.setText(Long.toString(selectedItem.getTotalPrice().get()));
//			
//			List<LocalDate> temp = (
//					start)
//					.datesUntil(end)
//					.collect(Collectors.toList());
//			busyDates.removeAll(temp);
//			populateCalendar(currentYearMonth);
//
//	    	}	
		}
		
	public void someInitalValues() {
		
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
		
		setCheckin_b.setOnMouseClicked(checkin -> {
			setCheckin(null);
			setCheckout(null);
			checkin_x.setText(null);
			checkout_x.setText(null);
			refresh();
		});
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
	    			
	    			PreparedStatement statement = Database.con().prepareStatement(
	    					"DELETE FROM `hoteldatabase`.`reservations` "
	    			+ "WHERE (`id_reservation` = ? );");
	    			
	    			statement.setInt(1, selectedItem.getReservationId());
		            statement.executeUpdate();
		            setCheckin(null);
		            setCheckout(null);
		            refresh();
		            
	    		} catch (SQLException e1) {
	    			e1.printStackTrace();
	    			
	    		}
	    		
	    	}
	    	
		}
		
	}
	
	public long calcPrice(LocalDate checkin, LocalDate checkout) {
		
		long price = 0;
    	long daysNo = 0;
    	
    	if(checkin != null && checkout != null) {
    		
    		daysNo = Duration.between(
    				checkin.atStartOfDay(), 
    				checkout.atStartOfDay()).toDays();
    		
    				price = getPriceNight() * daysNo;
    	}
    	
    	return price;
    	
	}
	
	public boolean areEmpty() {
		
		if (
				name_x.getText().isBlank()
			||	lastName_x.getText().isBlank()
			||  phonenum_x.getText().isEmpty()
			||  getCheckin() == null
			||  getCheckout() == null
			||  getTotalPrice() <= 0
		   ) {
			
			return true;
			
		} else {
			
			return false;
			
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

	public void tableSetup() 
	{
		name_c.setCellValueFactory(name -> name.getValue().getName());
		lastname_c.setCellValueFactory(surname -> surname.getValue().getLastName());
		phone_c.setCellValueFactory(phonenum -> phonenum.getValue().getPhoneNum());
		checkin_c.setCellValueFactory(checkin -> checkin.getValue().getCheckin());
		checkout_c.setCellValueFactory(checkout -> checkout.getValue().getCheckout());
		totalPrice_c.setCellValueFactory(totalPrice -> totalPrice.getValue().getTotalPrice());
		createdAt_c.setCellValueFactory(createdat -> createdat.getValue().getCreatedat());
		
		loadDataFromDB();
	}
		
	public void loadDataFromDB() {
				
		Statement statement;
		ResultSet resultSet;
		
		try {
			statement = Database.con().createStatement();
			
			resultSet = statement.executeQuery("select *\r\n"
					           + "from hoteldatabase.reservations r\r\n"
					           + "where number = '"+getSelectedRoom()+"'\r\n"
					           );
			while (resultSet.next()) {
		    	
				int resevation_id = resultSet.getInt("id_reservation");
		    	String name = resultSet.getString("name");
		    	String lastname  = resultSet.getString("surname");
		    	long phonenum = resultSet.getInt("phone_number");
		    	Date checkin  = resultSet.getDate("check_in");
		    	Date checkout  = resultSet.getDate("check_out");
		    	int totalPrice  = resultSet.getInt("total_price");
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
		    					timestamp.toLocalDateTime()
		    					));
		    	
				reservationsTable.setItems(reservations);

		    }
			if(reservations != null) {
			for(Reservation r : reservations) {
				
				ObjectProperty<Date> startDate = r.getCheckin();
				ObjectProperty<Date> endDate = r.getCheckout();
				
				checkins.add(startDate.get().toLocalDate());
				

				listOfDates = (
						 startDate.get().toLocalDate().plusDays(1))
						.datesUntil(endDate.get().toLocalDate())
						.collect(Collectors.toList());
				

				for(LocalDate date : listOfDates) {
					
					if(!busyDates.contains(date)) {
						busyDates.add(date);
						
					}
					
				}
				   busyDates.addAll(checkins);

			}}
  
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		
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
        	dateCell.setDate(calendarDate);
        	
        	dateCell.setText(txt);
    		dateCell.setDisable(false);
    		
    		addTempbusyDates();
    		disablePastDates(dateCell);
    		setBusyDates(dateCell);
        	select(dateCell);
        	
            calendarDate = calendarDate.plusDays(1);
        }
       
        calendarTitle.setText(
        		yearMonth.getMonth().toString() 
        		+ " " +
        		String.valueOf(yearMonth.getYear()));
    }
    
    public void disablePastDates(DayNode dateCell) {
    	
    	LocalDate future = null;
    	
    	if(checkins != null && getCheckin() != null) {
		FXCollections.sort(checkins);
  		for(LocalDate date : checkins) {
			if(date.isAfter(getCheckin())) {
				future = date;
				break;
			}}
		}
    	
    	if(dateCell.getDate().compareTo(LocalDate.now()) < 0) {
    		dateCell.setDisable(true);
    	}
    	
    	if(getCheckin() != null 
    			&& dateCell.getDate().compareTo(getCheckin()) <= 0) {
    		dateCell.setDisable(true);
    	}

   		if(future != null) 
   		{
   			busyDates.remove(future);
   		}
    	
		if(future != null && getCheckin() != null 
				&& dateCell.getDate().isAfter(future)) {
			
			dateCell.setDisable(true);
		}
		
		if(getCheckout() != null 
    			&& getCheckin().compareTo(dateCell.getDate()) 
    			* dateCell.getDate().compareTo(getCheckout()) >= 0) {
    		    
    		dateCell.setStyle("-fx-background-color:yellow");
    	
    	} 
    	
    	else 
   	
    	{  
    		dateCell.setStyle(null);
    	}
    	
    }
    
    public void setBusyDates(DayNode dateCell) {
    	if(busyDates.contains(dateCell.getDate())) {
    		
    		if( (getCheckin() != null 
    				
    				&& dateCell.getDate().compareTo(getCheckin()) == 0)
    				
    				|| (getCheckout() != null 
    				
    				&& dateCell.getDate().compareTo(getCheckout()) == 0 ) ) 
    		{
    			dateCell.setStyle("-fx-background-color:red");
    		} 
    		
    		else 
    		
    		{
    			dateCell.setStyle("-fx-background-color:aqua");
    		}
    		
    	}
    	
    }
    
    public void addTempbusyDates() {
    	busyDates.add(getCheckin());
    	busyDates.add(getCheckout());
    }
    
	public void select(DayNode selected) 
    {
		selected.setOnMouseClicked(
    			
    			e -> { 
    				
    				if(!busyDates.contains(selected.getDate())) 
    				
    				{
    					
    					if(setCheckin_b.isSelected())
    					
    					{
    						setCheckin(selected.getDate());
    						checkin_x.setText(getCheckin().toString());
    						populateCalendar(currentYearMonth);
    						setCheckout_b.setSelected(true);
    				} 
    			
    				else if(setCheckout_b.isSelected() && getCheckin() != null)
    			   	
    				{
    					setCheckout(selected.getDate());
        				checkout_x.setText(getCheckout().toString());
        				refresh();        				
    				} else if(setCheckout_b.isSelected() && getCheckin() == null) {
    					
    					checkout_x.setText("^Check-in first");
        			}
    					
    					setTotalPrice(calcPrice(getCheckin(),getCheckout()));
    					totalPrice_x.setText(Long.toString(getTotalPrice()));
    					
    				}
    				
    			});
		
    }
	
    public void previousMonth() 
    {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);

    }

    public void nextMonth() 
    {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);

    }

    public ArrayList<DayNode> getAllCalendarDays() 
    {
        return allCalendarDays;
    }
    
    public void setAllCalendarDays(ArrayList<DayNode> allCalendarDays) {
        this.allCalendarDays = allCalendarDays;
    }

   	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableSetup();
		someInitalValues();
		CalendarView(YearMonth.now());
	}
}