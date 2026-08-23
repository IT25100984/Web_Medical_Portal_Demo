package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Patient;
import com.webmedicalportaldemo.util.DBConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class PatientDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PatientDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean registerPatient(Patient patient) {
        String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ? ";

        String userSql = "INSERT INTO users " +
                        "(first_name, last_name, email, password_hash, role, is_active) " +
                        "VALUES (?, ?, ?, ?, 'PATIENT', TRUE)";

        String patientSql =
                "INSERT INTO patients (user_id, blood_group, medical_history) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            // Check if email already exists
            Integer count = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, patient.getEmail());
            if (count != null && count > 0) {
                return false;
            }
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);
            int userId;
            // Insert into users table
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, patient.getFirstName());
                userStmt.setString(2, patient.getLastName());
                userStmt.setString(3, patient.getEmail());
                // Replace with BCrypt later
                userStmt.setString(4, patient.getPassword());

                int userRows = userStmt.executeUpdate();
                if (userRows == 0) {
                    conn.rollback();
                    return false;
                }
                try (ResultSet rs = userStmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    userId = rs.getInt(1);
                }
            }

            // Store generated user ID inside the object
            patient.setUserID(userId);
            // Insert into patients table
            try (PreparedStatement patientStmt = conn.prepareStatement(patientSql)) {
                patientStmt.setInt(1, userId);
                patientStmt.setString(2, patient.getBloodGroup());
                patientStmt.setString(3, patient.getMedicalHistory());
                int patientRows = patientStmt.executeUpdate();
                if (patientRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getPatientIdByUserId(int userID) {
        String sql = "SELECT patient_id " + "FROM patients " + "WHERE user_id = ?";
        List<Integer> patientIDs = jdbcTemplate.query(sql, (resultSet, rowNumber)
                -> resultSet.getInt("patient_id"), userID);
        return patientIDs.isEmpty() ? null : patientIDs.get(0);
    }

    public boolean updateMedicalHistory(int userID, String newHistory) {
        String sql = "UPDATE patients SET medical_history = ? WHERE user_id = ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHistory);
            stmt.setInt(2, userID);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int userId, String bloodGroup, String medicalHistory) {
        String sql = "UPDATE patients SET blood_group = ?, medical_history = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, bloodGroup, medicalHistory, userId) > 0;
    }
}