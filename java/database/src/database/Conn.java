package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn {
    public static Connection getConn(){
        Connection connection=null;
        try {
            Class.forName("org.postgresql.Driver");
            String urlString="jdbc:postgresql://localhost:5432/postgresql";
            connection=DriverManager.getConnection(urlString,"","");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
