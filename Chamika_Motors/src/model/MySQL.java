package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import util.DBUtil;

/**
 * Lightweight helper to execute simple SQL statements.
 *
 * NOTE: This utility returns a ResultSet for SELECT queries and
 * leaves it to the caller to close the ResultSet/Statement/Connection
 * using DBUtil.closeQuietly(...) when finished. For UPDATE/INSERT/DELETE
 * it executes the update and returns null.
 */
public class MySQL {

    public static ResultSet execute(String query) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();

            if (query.trim().toUpperCase().startsWith("SELECT")) {
                // Return the ResultSet (caller must close resources)
                return statement.executeQuery(query);
            } else {
                statement.executeUpdate(query);
                // close resources for non-select
                DBUtil.closeQuietly(statement, connection);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Attempt to close resources on error
            DBUtil.closeQuietly(statement, connection);
            return null;
        }
    }
}
