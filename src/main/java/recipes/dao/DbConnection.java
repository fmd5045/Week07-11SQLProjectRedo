package recipes.dao;

import java.sql.*;
import recipes.exception.DbException;

public class DbConnection {
	private static String HOST = "localhost";
	private static String PASSWORD = "recipes";
	private static int PORT = 3306;
	private static String SCHEMA = "recipes";
	private static String USER = "recipes";
	
	public static Connection getConnection() {
		String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false", 
				HOST, PORT, SCHEMA, USER, PASSWORD);
		try {
		Connection connection = DriverManager.getConnection(url);
		System.out.println("The connection succeeded!");
		return connection;
		}catch(SQLException e) {
			System.out.println("The connection failed.");
			throw new DbException(e);
		}
				
	}
}
