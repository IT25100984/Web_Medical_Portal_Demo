package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PatientDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PatientDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves all registered patients joined with their core user details.
     */
    public List<Patient> getAllPatients() {
        String sql = """
            SELECT p.patient_id, u.user_id, u.first_name, u.last_name, u.email, u.role, u.is_active,
                   p.blood_group, p.medical_history
            FROM patients p
            JOIN users u ON p.user_id = u.user_id
            ORDER BY p.patient_id ASC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Patient patient = new Patient();
            patient.setPatientID(rs.getInt("patient_id"));
            patient.setUserID(rs.getInt("user_id"));
            patient.setFirstName(rs.getString("first_name"));
            patient.setLastName(rs.getString("last_name"));
            patient.setEmail(rs.getString("email"));
            patient.setRole(rs.getString("role"));
            patient.setActive(rs.getBoolean("is_active"));
            patient.setBloodGroup(rs.getString("blood_group"));
            patient.setMedicalHistory(rs.getString("medical_history"));
            return patient;
        });
    }

    /**
     * Registers a new patient with transactional integrity.
     */
    @Transactional
    public boolean registerPatient(Patient patient) {
        String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, patient.getEmail());
        if (count != null && count > 0) {
            return false;
        }

        String userSql = "INSERT INTO users (first_name, last_name, email, password, role, is_active) VALUES (?, ?, ?, ?, 'PATIENT', TRUE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int userRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            ps.setString(3, patient.getEmail());
            ps.setString(4, patient.getPassword());
            return ps;
        }, keyHolder);

        if (userRows == 0 || keyHolder.getKey() == null) {
            return false;
        }

        int userId = keyHolder.getKey().intValue();
        patient.setUserID(userId);

        String patientSql = "INSERT INTO patients (user_id, blood_group, medical_history) VALUES (?, ?, ?)";
        int patientRows = jdbcTemplate.update(patientSql, userId, patient.getBloodGroup(), patient.getMedicalHistory());

        return patientRows > 0;
    }

    public Integer getPatientIDByUserId(int userID) {
        String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
        List<Integer> patientIDs = jdbcTemplate.query(sql, (resultSet, rowNumber)
                -> resultSet.getInt("patient_id"), userID);
        return patientIDs.isEmpty() ? null : patientIDs.get(0);
    }

    public boolean updateMedicalHistory(int userID, String newHistory) {
        String sql = "UPDATE patients SET medical_history = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, newHistory, userID) > 0;
    }

    public boolean updateProfile(int userId, String bloodGroup, String medicalHistory) {
        String sql = "UPDATE patients SET blood_group = ?, medical_history = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, bloodGroup, medicalHistory, userId) > 0;
    }

    public Patient findByUserId(int userId) {
        String sql = """
            SELECT u.user_id, u.first_name, u.last_name, u.email, u.password, u.role, u.is_active,
                   p.patient_id, p.blood_group, p.medical_history
            FROM users u
            JOIN patients p ON u.user_id = p.user_id
            WHERE u.user_id = ?
        """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Patient patient = new Patient();
                patient.setPatientID(rs.getInt("patient_id"));
                patient.setUserID(rs.getInt("user_id"));
                patient.setFirstName(rs.getString("first_name"));
                patient.setLastName(rs.getString("last_name"));
                patient.setEmail(rs.getString("email"));
                patient.setPassword(rs.getString("password"));
                patient.setRole(rs.getString("role"));
                patient.setActive(rs.getBoolean("is_active"));
                patient.setBloodGroup(rs.getString("blood_group"));
                patient.setMedicalHistory(rs.getString("medical_history"));
                return patient;
            }, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Convenience alias for findByUserId to support standard naming conventions.
     */
    public Patient getPatientByUserId(int userId) {
        return findByUserId(userId);
    }
}