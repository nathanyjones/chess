package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ClearDataAccessTests extends ParentDataAccessTests {

    @Test
    void clearDatabaseSuccess() {
        String authToken = "example_auth_token";
        String username = "user1";
        try {
            UserData userData = new UserData(username, "12345", "email@mail.com");
            dataAccess.createUser(userData);
            AuthData auth = new AuthData(authToken, username);
            dataAccess.createAuth(auth);
            dataAccess.clear();


            var statement = "SELECT * FROM auths WHERE authToken = ?";
            try (var stmt = connection.prepareStatement(statement)) {
                stmt.setString(1, authToken);
                var rs = stmt.executeQuery();
                assertFalse(rs.next());
            }

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());

        } catch (SQLException e) {
            fail("Error: SQL Exception: " + e.getMessage());
        }
    }

}
