package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class DoctorDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DoctorDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Get a single doctor profile using the User ID
     */
    public Doctor getDoctorProfile(int userID) {
        String sql = " SELECT u.user_id, u.first_name, u.last_name, u.email, d.specialization, d.license_id "+
                " FROM users u JOIN employees e ON u.user_id = e.user_id " +
                " LEFT JOIN doctors d ON e.employee_pk = d.employee_pk WHERE u.user_id = ? ;";

        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper(), userID);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    /**
     * Return all doctors
     */
    public List<Doctor> getAllDoctors() {

        String sql = " SELECT u.user_id, u.first_name, u.last_name, u.email, d.specialization, d.license_id "+
                " FROM users u JOIN employees e ON u.user_id = e.user_id " +
                " JOIN doctors d ON e.employee_pk = d.employee_pk ORDER BY u.first_name ;";

        return jdbcTemplate.query(sql, doctorRowMapper());
    }

    /**
     * Find doctors by specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        String sql = " SELECT u.user_id, u.first_name, u.last_name, u.email, d.specialization, d.license_id "+
                " FROM users u JOIN employees e ON u.user_id = e.user_id "+
                " JOIN doctors d ON e.employee_pk = d.employee_pk " +
                " WHERE d.specialization = ? ORDER BY u.first_name ";

        return jdbcTemplate.query(sql, doctorRowMapper(), specialization);
    }

    /**
     * Update specialization
     */
    public boolean updateSpecialization(int userID, String specialization) {
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk "+
                " SET d.specialization = ? WHERE e.user_id = ? ";

        return jdbcTemplate.update(sql, specialization, userID) > 0;
    }

    /**
     * Update medical license
     */
    public boolean updateLicense(int userID, int licenseId) {
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk "+
                " SET d.license_id = ? WHERE e.user_id = ? ";

        return jdbcTemplate.update(sql, licenseId, userID) > 0;
    }

    /**
     * Update both specialization and license
     */
    public boolean updateProfile(int userID, String specialization, int licenseId) {
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk "+
                " SET d.specialization = ?, d.license_id = ? WHERE e.user_id = ? " ;

        return jdbcTemplate.update(sql, specialization, licenseId, userID) > 0;
    }
    public Integer getDoctorIDByuserID(int userID) {
        String sql = " SELECT d.doctor_id FROM doctors d JOIN employees e " +
                " ON d.employee_pk = e.employee_pk WHERE e.user_id = ? ";

        List<Integer> doctorIDs = jdbcTemplate.query(sql,
                (resultSet, rowNumber) ->
                        resultSet.getInt("doctor_id"), userID);
        return doctorIDs.isEmpty() ? null : doctorIDs.get(0);
    }

    public boolean createDoctorProfile(int userID, String specialization, Integer licenseId) {
        String sql = "INSERT INTO doctors (employee_pk, specialization, license_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, userID, specialization, licenseId) > 0;
    }

    /**
     * Maps SQL results to Doctor objects
     */
    private RowMapper<Doctor> doctorRowMapper() {
        return (rs, rowNum) -> {
            Doctor doctor = new Doctor();
            doctor.setUserID(rs.getInt("user_id"));
            doctor.setFirstName(rs.getString("first_name"));
            doctor.setLastName(rs.getString("last_name"));
            doctor.setEmail(rs.getString("email"));
            doctor.setRole("DOCTOR");
            doctor.setSpecialization(rs.getString("specialization") != null ? rs.getString("specialization") : "General");

            // Safely map NULL database values to 0 or null
            Integer licenseId = rs.getObject("license_id", Integer.class);
            doctor.setLicenseID(licenseId != null ? licenseId : 0);

            return doctor;
        };
    }
}