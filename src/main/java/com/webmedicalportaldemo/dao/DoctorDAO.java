package com.webmedicalportaldemo.dao;
import com.webmedicalportaldemo.model.Doctor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
@Repository
public class DoctorDAO {
    private final JdbcTemplate jdbcTemplate;
    public DoctorDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public Doctor getDoctorProfile(int userID) {
        if (userID <= 0) {
            return null;
        }
        String sql = "SELECT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM users u JOIN employees e ON u.user_id = e.user_id JOIN doctors d ON e.employee_pk = d.employee_pk WHERE u.user_id = ? AND u.role = 'DOCTOR'";
        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper(), userID);
        return doctors.isEmpty() ? null : doctors.get(0);
    }
    public List<Doctor> getAllDoctors() {
        String sql = "SELECT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM users u JOIN employees e ON u.user_id = e.user_id JOIN doctors d ON e.employee_pk = d.employee_pk WHERE u.role = 'DOCTOR' AND u.is_active = TRUE ORDER BY u.first_name ASC, u.last_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper());
    }
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.isBlank() || "All Specializations".equalsIgnoreCase(specialization.trim())) {
            return getAllDoctors();
        }
        String sql = "SELECT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM users u JOIN employees e ON u.user_id = e.user_id JOIN doctors d ON e.employee_pk = d.employee_pk WHERE u.role = 'DOCTOR' AND u.is_active = TRUE AND LOWER(d.specialization) = LOWER(?) ORDER BY u.first_name ASC, u.last_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper(), specialization.trim());
    }
    public boolean updateSpecialization(int userID, String specialization) {
        if (userID <= 0 || specialization == null || specialization.isBlank()) {
            return false;
        }
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk SET d.specialization = ? WHERE e.user_id = ?";
        return jdbcTemplate.update(sql, specialization.trim(), userID) > 0;
    }
    public boolean updateLicense(int userID, String licenseID) {
        if (userID <= 0 || licenseID == null || licenseID.isBlank()) {
            return false;
        }
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk SET d.license_id = ? WHERE e.user_id = ?";
        return jdbcTemplate.update(sql, licenseID.trim(), userID) > 0;
    }
    public boolean updateProfile(int userID, String specialization, String licenseID) {
        if (userID <= 0 || specialization == null || specialization.isBlank() || licenseID == null || licenseID.isBlank()) {
            return false;
        }
        String sql = "UPDATE doctors d JOIN employees e ON d.employee_pk = e.employee_pk SET d.specialization = ?, d.license_id = ? WHERE e.user_id = ?";
        return jdbcTemplate.update(sql, specialization.trim(), licenseID.trim(), userID) > 0;
    }
    public Integer getDoctorIDByuserID(int userID) {
        if (userID <= 0) {
            return null;
        }
        String sql = "SELECT d.doctor_id FROM doctors d JOIN employees e ON d.employee_pk = e.employee_pk WHERE e.user_id = ?";
        List<Integer> doctorIDs = jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getInt("doctor_id"), userID);
        return doctorIDs.isEmpty() ? null : doctorIDs.get(0);
    }
    public Integer getDoctorIdByUserId(int userID) {
        return getDoctorIDByuserID(userID);
    }
    public Integer getDoctorUserIDByDoctorID(int doctorID) {
        if (doctorID <= 0) {
            return null;
        }
        String sql = "SELECT e.user_id FROM doctors d JOIN employees e ON d.employee_pk = e.employee_pk WHERE d.doctor_id = ?";
        List<Integer> userIDs = jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getInt("user_id"), doctorID);
        return userIDs.isEmpty() ? null : userIDs.get(0);
    }
    public Doctor getDoctorByDoctorID(int doctorID) {
        if (doctorID <= 0) {
            return null;
        }
        String sql = "SELECT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM doctors d JOIN employees e ON d.employee_pk = e.employee_pk JOIN users u ON e.user_id = u.user_id WHERE d.doctor_id = ? AND u.role = 'DOCTOR'";
        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper(), doctorID);
        return doctors.isEmpty() ? null : doctors.get(0);
    }
    public boolean doctorExistsByUserID(int userID) {
        if (userID <= 0) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM doctors d JOIN employees e ON d.employee_pk = e.employee_pk WHERE e.user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userID);
        return count != null && count > 0;
    }
    public boolean doctorExistsByLicenseID(String licenseID) {
        if (licenseID == null || licenseID.isBlank()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM doctors WHERE license_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, licenseID.trim());
        return count != null && count > 0;
    }
    public boolean createDoctorProfile(int userID, String specialization, String licenseID) {
        if (userID <= 0 || specialization == null || specialization.isBlank() || licenseID == null || licenseID.isBlank()) {
            return false;
        }
        if (doctorExistsByUserID(userID) || doctorExistsByLicenseID(licenseID)) {
            return false;
        }
        Integer employeePK = getEmployeePKByUserID(userID);
        if (employeePK == null || employeePK <= 0) {
            return false;
        }
        String sql = "INSERT INTO doctors (employee_pk, specialization, license_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employeePK, specialization.trim(), licenseID.trim()) > 0;
    }
    public List<Doctor> getActiveDoctorsWithAvailability() {
        String sql = "SELECT DISTINCT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM users u JOIN employees e ON u.user_id = e.user_id JOIN doctors d ON e.employee_pk = d.employee_pk JOIN doctor_availability da ON d.doctor_id = da.doctor_id WHERE u.role = 'DOCTOR' AND u.is_active = TRUE ORDER BY u.first_name ASC, u.last_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper());
    }
    public List<Doctor> getDoctorsBySpecializationWithAvailability(String specialization) {
        if (specialization == null || specialization.isBlank() || "All Specializations".equalsIgnoreCase(specialization.trim())) {
            return getActiveDoctorsWithAvailability();
        }
        String sql = "SELECT DISTINCT d.doctor_id, u.user_id, u.first_name, u.last_name, u.email, u.is_active, d.specialization, d.license_id FROM users u JOIN employees e ON u.user_id = e.user_id JOIN doctors d ON e.employee_pk = d.employee_pk JOIN doctor_availability da ON d.doctor_id = da.doctor_id WHERE u.role = 'DOCTOR' AND u.is_active = TRUE AND LOWER(d.specialization) = LOWER(?) ORDER BY u.first_name ASC, u.last_name ASC";
        return jdbcTemplate.query(sql, doctorRowMapper(), specialization.trim());
    }
    private Integer getEmployeePKByUserID(int userID) {
        if (userID <= 0) {
            return null;
        }
        String sql = "SELECT employee_pk FROM employees WHERE user_id = ?";
        List<Integer> employeePKs = jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getInt("employee_pk"), userID);
        return employeePKs.isEmpty() ? null : employeePKs.get(0);
    }
    private RowMapper<Doctor> doctorRowMapper() {
        return (resultSet, rowNumber) -> {
            Doctor doctor = new Doctor();
            doctor.setDoctorID(resultSet.getInt("doctor_id"));
            doctor.setUserID(resultSet.getInt("user_id"));
            doctor.setFirstName(resultSet.getString("first_name"));
            doctor.setLastName(resultSet.getString("last_name"));
            doctor.setEmail(resultSet.getString("email"));
            doctor.setRole("DOCTOR");
            doctor.setActive(resultSet.getBoolean("is_active"));
            String specialization = resultSet.getString("specialization");
            doctor.setSpecialization(specialization == null || specialization.isBlank() ? "General" : specialization);
            doctor.setLicenseID(resultSet.getString("license_id"));
            return doctor;
        };
    }
}