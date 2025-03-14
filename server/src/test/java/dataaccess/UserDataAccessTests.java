package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataAccessTests extends ParentDataAccessTests {

    @Test
    void createUserSuccess() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);

            var statement = "SELECT * FROM users WHERE username = ?";
            try (var stmt = connection.prepareStatement(statement)) {
                stmt.setString(1, "user1");
                var rs = stmt.executeQuery();
                assertTrue(rs.next());
                assertEquals("user1", rs.getString("username"));
                assertEquals("email@mail.com", rs.getString("email"));
            }

        } catch (SQLException e) {
            fail("Error: SQL Exception: " + e.getMessage());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void createUserFailTaken() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            UserData userDataCopy = new UserData("user1", "54321", "email2@mail.com");
            dataAccess.createUser(userData);
            dataAccess.createUser(userDataCopy);
            fail("Duplicate username added to the database with no thrown exceptions.");

        } catch (DataAccessException e) {
            assertEquals("Error: already taken", e.getMessage());

        } catch (RuntimeException e) {
            fail("Error: Runtime Exception: " + e.getMessage());
        }
    }

    @Test
    void getUserSuccess() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);
            UserData retrievedUserData = dataAccess.getUser("user1");

            assertEquals(userData.username(), retrievedUserData.username());
            assertEquals(userData.email(), retrievedUserData.email());
            assertNotEquals(userData.password(), retrievedUserData.password());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void getUserFailNotFound() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);
            dataAccess.getUser("nonexistentUser");

            fail("Nonexistent username found with no thrown exception.");

        } catch (DataAccessException e) {
            assertEquals("Error: nonexistentUser not found", e.getMessage());

        } catch (RuntimeException e) {
            fail("Error: Runtime Exception: " + e.getMessage());
        }
    }

}
