/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

/**
 *
 * @author JenJen
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {

            String url = "jdbc:mysql://localhost:3306/online_voting";
            String username = "root";
            String password = "";

            con = DriverManager.getConnection(
                url, username, password
            );

        } catch (SQLException e) {

            System.out.println("Database connection failed.");
            System.out.println(e.getMessage());

        }

        return con;
    }
    public static void main(String[] args) {

    Connection con = getConnection();

    if (con != null) {

        System.out.println("Database connected successfully!");

        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    } else {

        System.out.println("Database connection failed.");

    }
}
}