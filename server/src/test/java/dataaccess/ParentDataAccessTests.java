package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;

public class ParentDataAccessTests {
    protected DataAccess dataAccess;
    protected Connection connection;

    @BeforeEach
    void setup() {
        try {
            dataAccess = new MySqlDataAccess();
            connection = DatabaseManager.getConnection();
            try (var stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM games");
                stmt.execute("DELETE FROM auths");
                stmt.execute("DELETE FROM users");
            }
        } catch (Exception e) {
            System.err.println("Error initializing data access: " + e.getMessage());
        }
    }

}
