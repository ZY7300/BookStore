/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Zi Yu
 */
public class Database {
    
    /**
     * This method creates a connection to a MySQL database using the JDBC driver.
     * 
     * @return Connection object for the established database connection
     */
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/library", "root", "");
            return connection;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
