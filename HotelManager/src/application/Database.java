package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	public static Connection con() {
		
		Connection con = null;
		
		try {
			String url = "jdbc:mysql://localhost:3306/";
			String username = "root";
			String password = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;	
	}
	
	public static void isDBconnected(){
			if (con() != null) {
				System.out.println("Database Connected successfully");
			} else {
				System.out.println("Database Connection failed");
			}
	}
}
