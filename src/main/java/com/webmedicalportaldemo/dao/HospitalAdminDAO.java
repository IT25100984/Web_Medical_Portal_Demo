package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class HospitalAdminDAO {

    /**
     * Persists a new administrator account to the global users table.
     * Hardcodes the 'ADMIN' role string to maintain strict system boundaries.
     */
    /**
    public boolean registerAdmin(HospitalAdmin hospitalAdmin) {
        // SQL targets only base user columns and enforces the 'ADMIN' role explicitly
        String sql = "INSERT INTO users (first_name, last_name, email, password, role) " +
                "VALUES (?, ?, ?, ?, 'ADMIN')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = Objects.requireNonNull(conn).prepareStatement(sql)) {

            stmt.setString(1, hospitalAdmin.getFirstName());
            stmt.setString(2, hospitalAdmin.getLastName());
            stmt.setString(3, hospitalAdmin.getEmail());
            stmt.setString(4, hospitalAdmin.getPassword());

            return stmt.executeUpdate() > 0; // Returns true if insertion succeeded
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    */
    /**
     * Administrative Utility: Updates core credentials if required.
     */
    public boolean updateAdminProfile(int adminId, String firstName, String lastName, String email) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE user_id = ? AND role = 'HOSPITAL_ADMIN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = Objects.requireNonNull(conn).prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setInt(4, adminId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}