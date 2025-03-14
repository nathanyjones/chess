package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessTests extends ParentDataAccessTests {

    @Test
    void createAuthSuccess() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);

            AuthData auth = new AuthData("example_auth_token", "user1");
            dataAccess.createAuth(auth);

            var statement = "SELECT * FROM auths WHERE authToken = ?";
            try (var stmt = connection.prepareStatement(statement)) {
                stmt.setString(1, "example_auth_token");
                var rs = stmt.executeQuery();
                assertTrue(rs.next());
                assertEquals("example_auth_token", rs.getString("authToken"));
                assertEquals("user1", rs.getString("username"));
            }

        } catch (SQLException e) {
            fail("Error: SQL Exception: " + e.getMessage());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void createAuthFailExists() {
        try {
            UserData userData1 = new UserData("user3", "12345", "email@mail.com");
            UserData userData2 = new UserData("user4", "54321", "email2@mail.com");
            dataAccess.createUser(userData1);
            dataAccess.createUser(userData2);

            AuthData auth = new AuthData("example_auth_token", "user3");
            AuthData auth2 = new AuthData("example_auth_token", "user4");
            dataAccess.createAuth(auth);
            dataAccess.createAuth(auth2);

            fail("Created two auths with same authToken without raising an exception.");

        } catch (DataAccessException e) {
            assertEquals("Error: AuthToken already exists", e.getMessage());
        }
    }

    @Test
    void getAuthSuccess() {
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);
            AuthData auth = new AuthData("example_auth_token", "user1");
            dataAccess.createAuth(auth);

            AuthData retrievedAuth = dataAccess.getAuth("example_auth_token");

            assertEquals(retrievedAuth, auth);

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void getAuthFailNotFound() {
        String nonexistentAuthToken = "nonexistent_auth_token";
        try {
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);
            AuthData auth = new AuthData("example_auth_token", "user1");
            dataAccess.createAuth(auth);

            dataAccess.getAuth(nonexistentAuthToken);
            fail("Nonexistent username found with no thrown exception.");

        } catch (DataAccessException e) {
            assertEquals("Error: AuthToken " + nonexistentAuthToken + " not found", e.getMessage());
        }
    }

    @Test
    void deleteAuth() {
        try {
            String authToken = "example_auth_token";
            UserData userData = new UserData("user1", "12345", "email@mail.com");
            dataAccess.createUser(userData);
            AuthData auth = new AuthData(authToken, "user1");
            dataAccess.createAuth(auth);
            dataAccess.deleteAuth(authToken);

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
