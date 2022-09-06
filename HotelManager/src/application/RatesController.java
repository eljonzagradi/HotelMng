package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;

public class RatesController implements Initializable {
	
	@FXML private TableView<Rate> rates_t;
	@FXML private TableColumn<Rate,String> category_c;
	@FXML private TableColumn<Rate,Number> price_c;
	
	@FXML private TextField category_x;
	@FXML private TextField price_x;
	@FXML private Label error_l;
	
	@FXML private Button delete_b;
	@FXML private Button add_b;
	@FXML private Button clear_b;

	ObservableList<Rate> rateList = FXCollections.observableArrayList();

	public void ratesTableSetUp() {
		
		category_c.setCellValueFactory(category -> category.getValue().getCategory());
		price_c.setCellValueFactory(price -> price.getValue().getPrice());
		category_c.setCellFactory(TextFieldTableCell.forTableColumn());
		
		price_c.setCellValueFactory(
				new Callback<CellDataFeatures<Rate, Number>, ObservableValue<Number>>() {

			@Override
			public ObservableValue<Number> call(CellDataFeatures<Rate, Number> arg0) {
				 return new SimpleIntegerProperty(arg0.getValue().getPrice().get());			}
	
		});

		price_c.setCellFactory(TextFieldTableCell.<Rate, Number>
		forTableColumn(new NumberStringConverter()));
		
		category_c.setOnEditCommit(event -> {
		    Rate user = event.getRowValue();
		    user.setCategory(new SimpleStringProperty(event.getNewValue()));
		    updateCategory(event.getNewValue(), user.getCategory().get());
		});
		
		price_c.setOnEditCommit(event -> {
		    Rate user = event.getRowValue();
		    user.setPrice(new SimpleIntegerProperty(event.getNewValue().intValue()));
		    updatePrice(event.getNewValue(), user.getCategory().get());
		});
		
		loadRates();
	}
	
	private void updatePrice(Number newValue, String pk) {
		
		try {
			int price = newValue.intValue();
			PreparedStatement update = Database.con().prepareStatement
					("UPDATE `hoteldatabase`.`categories`\r\n"
					+ "SET\r\n"
					+ "`price` = ? \r\n"
					+ "WHERE category = ? ");
			update.setInt(1, price);
			update.setString(2, pk);
			update.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void updateCategory(String newValue, String pk) {
		
		try {
			
			PreparedStatement update = Database.con().prepareStatement
					("UPDATE `hoteldatabase`.`categories`\r\n"
					+ "SET\r\n"
					+ "`category` = ? \r\n"
					+ "WHERE category = ? ");
			update.setString(1, newValue);
			update.setString(2, pk);
			update.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void loadRates() {
		
		try {
			
			PreparedStatement rates = Database.con().prepareStatement
					("SELECT * FROM hoteldatabase.categories;");
			ResultSet getRates = rates.executeQuery();
			
			while(getRates.next()) {
				String cat = getRates.getString(1);
				int price = getRates.getInt(2);
				rateList.add(new Rate(cat,price));
				
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		rates_t.setItems(null);
		rates_t.setItems(rateList);
	}
	
	public boolean rateExists(String cat) {
		
		boolean result = false;
		
		for(Rate r : rateList) {
			
			if(r.getCategory().get().equals(cat)) {
				
				result = true;
				break;
			}
			
		}
		
		return result;		
	}
	
	public void clickAdd() {
		
		if(!category_x.getText().trim().isBlank() && !price_x.getText().trim().isBlank() ) {
						
			String cat = category_x.getText();
			
			if(rateExists(cat) == false) {
				
				int price = Integer.parseInt(price_x.getText());
				
				try {
					
					PreparedStatement insert = Database.con().prepareStatement
							("INSERT INTO `hoteldatabase`.`categories`\r\n"
					+ "(`category`, `price`) VALUES ( ?, ? );");
					insert.setString(1, cat);
					insert.setInt(2, price);
					int status = insert.executeUpdate();
					
					if(status == 1) {
						rateList.add(new Rate(cat,price));
						clear();
						
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
					
				}
				
			} else {
				error_l.setText("Duplicate Entry");
				
			}
			
		} else {
			error_l.setText("Complete All Field");
			
		}
		
	}
	
	public void deleteRate() {
		
		rates_t.getSelectionModel().selectedItemProperty().addListener(e -> {
			
			if(rates_t.getSelectionModel().getSelectedItem() != null) {
				
				delete_b.setDisable(false);
				String selected = rates_t.getSelectionModel().getSelectedItem().getCategory().get();
				
				delete_b.setOnAction( del -> {
					
					try {
						
						PreparedStatement delete = Database.con().prepareStatement
								("DELETE FROM `hoteldatabase`.`categories`\r\n"
						+ "WHERE `category` = ? ;");
						delete.setString(1, selected);
						
						int status = delete.executeUpdate();
						
						if(status == 1) {
							
							rateList.clear();
							loadRates();
							delete_b.setDisable(true);
							
						}
						
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					
				});
				
			}
			
		});
		
	}
	
	public void clear() {
		rates_t.refresh();
		category_x.clear();
		price_x.clear();
		error_l.setText(null);
		rates_t.getSelectionModel().clearSelection();
		delete_b.setDisable(true);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deleteRate();
		ratesTableSetUp();	
	}

}
