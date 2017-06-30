package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class PostgreSQLJDBC {
	
	public static Connection getConnection() throws SQLException{
        try {
             Class.forName("org.postgresql.Driver");                            
             return DriverManager.getConnection("jdbc:postgresql://localhost:5432/condotech",
                     "postgres", "admin");                        
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());            
            throw new SQLException();            
        }        
    } 
}