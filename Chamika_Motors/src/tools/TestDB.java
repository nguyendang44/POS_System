package tools;

import util.DBUtil;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection c = DBUtil.getConnection()) {
            if (c != null && !c.isClosed()) {
                System.out.println("OK: Connected to " + c.getMetaData().getURL() + " as " + c.getMetaData().getUserName());
            } else {
                System.out.println("FAIL: Connection is null or closed");
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION:");
            e.printStackTrace();
            System.exit(2);
        }
    }
}
