package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

public class OperationImpl implements Operation {

    @Override
    public String getName(int id) {
        Connection connection = Conn.getConn();
        String sqlString = "select name from verrou where id=" + id;
        Statement stmt = null;
        ResultSet rs = null;
        String result = "";
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                result += rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean checkTime(int id) {
        Connection connection = Conn.getConn();
        String sqlString = "select time from verrou where id=" + id;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlString);
            Time time = rs.getTime(1);
            if (time == new Date()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getId(String name) {
        Connection connection = Conn.getConn();
        String sqlString = "select id from verrou where name=" + name;
        Statement stmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
