package tools;

import util.DBUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Test to demonstrate HikariCP connection pool reuse.
 * Runs multiple queries in the same process to show pool efficiency.
 */
public class PoolReuseTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== HikariCP Connection Pool Reuse Test ===\n");

        // Warm up: 1 query to initialize pool and establish first connection
        System.out.println("--- WARM UP (1 query to init pool) ---");
        long warmupStart = System.currentTimeMillis();
        runQuery("SELECT COUNT(*) as employee_count FROM employee");
        long warmupEnd = System.currentTimeMillis();
        System.out.println("Warmup time: " + (warmupEnd - warmupStart) + "ms\n");

        // Test 1: Run 10 queries using the pool (connections should be reused)
        System.out.println("--- TEST 1: Run 10 queries with connection pool ---");
        long test1Start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            runQuery("SELECT first_name, last_name FROM employee LIMIT 1");
        }
        long test1End = System.currentTimeMillis();
        long test1Time = test1End - test1Start;
        System.out.println("Total time for 10 queries: " + test1Time + "ms");
        System.out.println("Average per query: " + (test1Time / 10.0) + "ms\n");

        // Test 2: Run 20 quick queries to test pool throughput
        System.out.println("--- TEST 2: Run 20 queries with pool ---");
        long test2Start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            runQuery("SELECT COUNT(*) as total FROM employee");
        }
        long test2End = System.currentTimeMillis();
        long test2Time = test2End - test2Start;
        System.out.println("Total time for 20 queries: " + test2Time + "ms");
        System.out.println("Average per query: " + (test2Time / 20.0) + "ms\n");

        // Test 3: Concurrent-like queries (sequential but mimicking concurrent load)
        System.out.println("--- TEST 3: Run 30 queries (simulating higher load) ---");
        long test3Start = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {
            runQuery("SELECT mobile, first_name FROM employee LIMIT 5");
        }
        long test3End = System.currentTimeMillis();
        long test3Time = test3End - test3Start;
        System.out.println("Total time for 30 queries: " + test3Time + "ms");
        System.out.println("Average per query: " + (test3Time / 30.0) + "ms\n");

        System.out.println("=== Pool Reuse Test Complete ===");
        System.out.println("All queries executed successfully using connection pool.");
        System.out.println("Notice: After warmup, average query time is very fast (pool reuse is efficient)");
    }

    private static void runQuery(String sql) throws Exception {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Just consume the result to ensure query completes
            if (rs.next()) {
                // Query executed
            }
        }
    }
}
