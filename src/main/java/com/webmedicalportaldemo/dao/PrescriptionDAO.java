package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Prescription;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;

import java.time.LocalTime;

import java.util.List;

@Repository
public class PrescriptionDAO {

    private final JdbcTemplate jdbcTemplate;

    public PrescriptionDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Saves a new prescription and returns its generated ID.
     *
     * Returns -1 if the prescription could not be saved.
     */
    public int savePrescription(Prescription prescription) {

        String sql = """
                INSERT INTO prescriptions (
                    patient_id,
                    doctor_id,
                    medicine_name,
                    quantity,
                    medicine_price,
                    status,
                    order_date,
                    order_time
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(
                connection -> {
                    PreparedStatement statement =
                            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    statement.setInt(1, prescription.getPatientID());
                    /*
                     * A doctor ID of 0 means that no doctor
                     * has been associated with the order.
                     */
                    if (prescription.getDoctorID() > 0) {
                        statement.setInt(2, prescription.getDoctorID()
                        );
                    } else {
                        statement.setNull(2, java.sql.Types.INTEGER);
                    }
                    statement.setString(3, prescription.getMedicineName());
                    statement.setInt(4, prescription.getQuantity());
                    statement.setDouble(5, prescription.getMedicinePrice());
                    statement.setString(6, normalizeStatus(prescription.getStatus()));
                    statement.setDate(7, Date.valueOf(prescription.getOrderDate()));
                    statement.setTime(8, Time.valueOf(normalizeTime(prescription.getOrderTime())));
                    return statement;
                },
                keyHolder
        );

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            int generatedOrderID = keyHolder.getKey().intValue();
            prescription.setOrderID(generatedOrderID);
            return generatedOrderID;
        }
        return -1;
    }

    /**
     * Finds one prescription using its prescription ID.
     */
    public Prescription getPrescriptionById(int prescriptionID) {
        String sql = """
                SELECT
                    p.prescription_id,
                    p.patient_id,
                    p.doctor_id,
                    p.medicine_name,
                    p.quantity,
                    p.medicine_price,
                    p.status,
                    p.order_date,
                    p.order_time
                FROM prescriptions p
                WHERE p.prescription_id = ?
                """;

        List<Prescription> results = jdbcTemplate.query(sql, prescriptionRowMapper(), prescriptionID);
        return results.isEmpty() ? null : results.get(0);
    }
    /**
     * Returns all prescriptions for the pharmacist dashboard.
     */
    public List<Prescription> getAllPrescriptions() {
        String sql = """
                SELECT
                    p.prescription_id,
                    p.patient_id,
                    p.doctor_id,
                    p.medicine_name,
                    p.quantity,
                    p.medicine_price,
                    p.status,
                    p.order_date,
                    p.order_time
                FROM prescriptions p
                ORDER BY
                    p.order_date DESC,
                    p.order_time DESC
                """;
        return jdbcTemplate.query(sql, prescriptionRowMapper());
    }
    /**
     * Returns all prescriptions belonging to a patient.
     *
     * This method expects patients.patient_id.
     */
    public List<Prescription> getPrescriptionsByPatientID(int patientID) {
        String sql = """
                SELECT
                    p.prescription_id,
                    p.patient_id,
                    p.doctor_id,
                    p.medicine_name,
                    p.quantity,
                    p.medicine_price,
                    p.status,
                    p.order_date,
                    p.order_time
                FROM prescriptions p
                WHERE p.patient_id = ?
                ORDER BY
                    p.order_date DESC,
                    p.order_time DESC
                """;
        return jdbcTemplate.query(sql, prescriptionRowMapper(), patientID);
    }
    /**
     * Returns patient prescriptions using users.user_id.
     *
     * This is useful because the logged-in session stores userID,
     * while prescriptions stores patients.patient_id.
     */
    public List<Prescription> getPrescriptionsByPatientuserID(int userID) {
        String sql = """
                SELECT
                    pr.prescription_id,
                    pr.patient_id,
                    pr.doctor_id,
                    pr.medicine_name,
                    pr.quantity,
                    pr.medicine_price,
                    pr.status,
                    pr.order_date,
                    pr.order_time
                FROM prescriptions pr
                JOIN patients pa
                    ON pr.patient_id = pa.patient_id
                WHERE pa.user_id = ?
                ORDER BY
                    pr.order_date DESC,
                    pr.order_time DESC
                """;
        return jdbcTemplate.query(sql, prescriptionRowMapper(), userID);
    }
    /**
     * Returns prescriptions associated with a doctor.
     *
     * This method expects doctors.doctor_id.
     */
    public List<Prescription> getPrescriptionsByDoctorID(int doctorID) {
        String sql = """
                SELECT
                    p.prescription_id,
                    p.patient_id,
                    p.doctor_id,
                    p.medicine_name,
                    p.quantity,
                    p.medicine_price,
                    p.status,
                    p.order_date,
                    p.order_time
                FROM prescriptions p
                WHERE p.doctor_id = ?
                ORDER BY
                    p.order_date DESC,
                    p.order_time DESC
                """;
        return jdbcTemplate.query(sql, prescriptionRowMapper(), doctorID);
    }

    public List<Prescription> getPrescriptionsByPatientId(int patientID) {
        String sql = "SELECT * FROM prescriptions WHERE patient_id = ? ORDER BY prescription_id DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Prescription prescription = new Prescription();
            prescription.setPrescriptionID(rs.getInt("prescription_id"));
            prescription.setPatientID(rs.getInt("patient_id"));
            prescription.setMedicineName(rs.getString("medicine_name"));
            prescription.setQuantity(rs.getInt("quantity"));
            prescription.setMedicinePrice(rs.getDouble("medicine_price"));
            prescription.setStatus(rs.getString("status"));
            return prescription;
        }, patientID);
    }

    /**
     * Updates the status of a prescription.
     */
    public boolean updateStatus(int prescriptionID, String status) {
        String normalizedStatus = normalizeStatus(status);
        String sql = """
                UPDATE prescriptions
                SET status = ?
                WHERE prescription_id = ?
                """;
        return jdbcTemplate.update(sql, normalizedStatus, prescriptionID) > 0;
    }
    /**
     * Updates a prescription's medicine details.
     */
    public boolean updatePrescription(int prescriptionID, String medicineName, int quantity, double medicinePrice) {
        if (medicineName == null || medicineName.isBlank() ||
                quantity <= 0 || medicinePrice < 0) {
            return false;
        }
        String sql = """
                UPDATE prescriptions
                SET
                    medicine_name = ?,
                    quantity = ?,
                    medicine_price = ?
                WHERE prescription_id = ?
                """;
        return jdbcTemplate.update(sql, medicineName.trim(),
                quantity, medicinePrice, prescriptionID) > 0;
    }
    /**
     * Associates a doctor with an existing prescription.
     */
    public boolean assignDoctor(int prescriptionID, int doctorID) {
        if (doctorID <= 0) {
            return false;
        }
        String sql = """
                UPDATE prescriptions
                SET doctor_id = ?
                WHERE prescription_id = ?
                """;
        return jdbcTemplate.update(sql, doctorID, prescriptionID) > 0;
    }
    /**
     * Deletes a prescription.
     */
    public boolean deletePrescriptionById(int prescriptionID) {
        String sql = """
                DELETE FROM prescriptions
                WHERE prescription_id = ?
                """;
        return jdbcTemplate.update(sql, prescriptionID) > 0;
    }
    /**
     * Converts a prescription database row into a
     * Prescription Java object.
     */
    private RowMapper<Prescription> prescriptionRowMapper() {
        return (resultSet, rowNumber) -> {
            Prescription prescription = new Prescription();
            prescription.setOrderID(resultSet.getInt("prescription_id"));
            prescription.setPatientID(resultSet.getInt("patient_id"));
            int doctorID = resultSet.getInt("doctor_id");

            if (resultSet.wasNull()) {
                doctorID = 0;
            }
            prescription.setDoctorID(resultSet.wasNull() ? null : doctorID);
            prescription.setMedicineName(resultSet.getString("medicine_name"));
            prescription.setQuantity(resultSet.getInt("quantity"));
            prescription.setMedicinePrice(resultSet.getDouble("medicine_price"));
            prescription.setStatus(resultSet.getString("status"));
            Date orderDate = resultSet.getDate("order_date");

            if (orderDate != null) {
                prescription.setOrderDate(orderDate.toLocalDate().toString());
            }
            Time orderTime = resultSet.getTime("order_time");

            if (orderTime != null) {
                prescription.setOrderTime(orderTime.toLocalTime().toString());
            }
            return prescription;
        };
    }
    /**
     * Ensures only supported status values reach MySQL.
     */
    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "PENDING";
        }
        String normalized = status.trim().toUpperCase();

        return switch (normalized) {
            case "PENDING", "APPROVED", "COMPLETED", "CANCELLED" -> normalized;
            default -> "PENDING";
        };
    }
    /**
     * Converts values such as "14:30" into a format accepted
     * by Time.valueOf(), which requires "HH:mm:ss".
     */
    private LocalTime normalizeTime(String orderTime) {
        if (orderTime == null || orderTime.isBlank()) {
            return LocalTime.now().withNano(0);
        }
        return LocalTime.parse(orderTime.trim());
    }
}