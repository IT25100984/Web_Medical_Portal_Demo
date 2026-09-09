package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.HealthRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Repository
public class HealthRecordDAO {

    private final JdbcTemplate jdbcTemplate;

    public HealthRecordDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createHealthRecord(HealthRecord healthRecord) {
        String sql = "INSERT INTO health_records (" +
                "patient_id, doctor_id, appointment_id, record_type, diagnosis, " +
                "symptoms, treatment_plan, clinical_notes, allergies, medications, " +
                "follow_up_instructions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, healthRecord.getPatientID());

            if (healthRecord.getDoctorID() != null) {
                statement.setInt(2, healthRecord.getDoctorID());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            if (healthRecord.getAppointmentID() != null) {
                statement.setInt(3, healthRecord.getAppointmentID());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            statement.setString(4, normalizeRecordType(healthRecord.getRecordType()));
            statement.setString(5, healthRecord.getDiagnosis());
            statement.setString(6, healthRecord.getSymptoms());
            statement.setString(7, healthRecord.getTreatmentPlan());
            statement.setString(8, healthRecord.getClinicalNotes());
            statement.setString(9, healthRecord.getAllergies());
            statement.setString(10, healthRecord.getMedications());
            statement.setString(11, healthRecord.getFollowUpInstructions());
            return statement;
        }, keyHolder);

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            int generatedRecordID = keyHolder.getKey().intValue();
            healthRecord.setHealthRecordID(generatedRecordID);
            return generatedRecordID;
        }
        return -1;
    }

    public HealthRecord getHealthRecordById(int healthRecordID) {
        String sql = "SELECT health_record_id, patient_id, doctor_id, appointment_id, record_type, " +
                "diagnosis, symptoms, treatment_plan, clinical_notes, allergies, medications, " +
                "follow_up_instructions, created_at, updated_at " +
                "FROM health_records WHERE health_record_id = ?";

        return jdbcTemplate.query(sql, healthRecordRowMapper(), healthRecordID)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<HealthRecord> getHealthRecordsByPatientID(int patientID) {
        String sql = "SELECT health_record_id, patient_id, doctor_id, appointment_id, record_type, " +
                "diagnosis, symptoms, treatment_plan, clinical_notes, allergies, medications, " +
                "follow_up_instructions, created_at, updated_at " +
                "FROM health_records WHERE patient_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, healthRecordRowMapper(), patientID);
    }

    public List<HealthRecord> getHealthRecordsByPatientUserId(int userID) {
        String sql = "SELECT hr.health_record_id, hr.patient_id, hr.doctor_id, hr.appointment_id, " +
                "hr.record_type, hr.diagnosis, hr.symptoms, hr.treatment_plan, hr.clinical_notes, " +
                "hr.allergies, hr.medications, hr.follow_up_instructions, hr.created_at, hr.updated_at " +
                "FROM health_records hr JOIN patients p ON hr.patient_id = p.patient_id " +
                "WHERE p.user_id = ? ORDER BY hr.created_at DESC";
        return jdbcTemplate.query(sql, healthRecordRowMapper(), userID);
    }

    public boolean updateHealthRecord(HealthRecord healthRecord) {
        String sql = "UPDATE health_records SET record_type = ?, diagnosis = ?, symptoms = ?, " +
                "treatment_plan = ?, clinical_notes = ?, allergies = ?, medications = ?, " +
                "follow_up_instructions = ? WHERE health_record_id = ?";

        int rowsAffected = jdbcTemplate.update(
                sql,
                normalizeRecordType(healthRecord.getRecordType()),
                healthRecord.getDiagnosis(),
                healthRecord.getSymptoms(),
                healthRecord.getTreatmentPlan(),
                healthRecord.getClinicalNotes(),
                healthRecord.getAllergies(),
                healthRecord.getMedications(),
                healthRecord.getFollowUpInstructions(),
                healthRecord.getHealthRecordID()
        );
        return rowsAffected > 0;
    }

    public void saveRecord(HealthRecord record) {
        if (record.getHealthRecordID() > 0) {
            updateHealthRecord(record);
        } else {
            createHealthRecord(record);
        }
    }

    private RowMapper<HealthRecord> healthRecordRowMapper() {
        return (resultSet, rowNumber) -> {
            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setHealthRecordID(resultSet.getInt("health_record_id"));
            healthRecord.setPatientID(resultSet.getInt("patient_id"));

            int doctorID = resultSet.getInt("doctor_id");
            healthRecord.setDoctorID(resultSet.wasNull() ? null : doctorID);

            int appointmentID = resultSet.getInt("appointment_id");
            healthRecord.setAppointmentID(resultSet.wasNull() ? null : appointmentID);

            healthRecord.setRecordType(resultSet.getString("record_type"));
            healthRecord.setDiagnosis(resultSet.getString("diagnosis"));
            healthRecord.setSymptoms(resultSet.getString("symptoms"));
            healthRecord.setTreatmentPlan(resultSet.getString("treatment_plan"));
            healthRecord.setClinicalNotes(resultSet.getString("clinical_notes"));
            healthRecord.setAllergies(resultSet.getString("allergies"));
            healthRecord.setMedications(resultSet.getString("medications"));
            healthRecord.setFollowUpInstructions(resultSet.getString("follow_up_instructions"));

            Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
            Timestamp updatedTimestamp = resultSet.getTimestamp("updated_at");

            if (createdTimestamp != null) {
                healthRecord.setCreatedAt(createdTimestamp.toLocalDateTime());
            }
            if (updatedTimestamp != null) {
                healthRecord.setUpdatedAt(updatedTimestamp.toLocalDateTime());
            }

            return healthRecord;
        };
    }

    private String normalizeRecordType(String recordType) {
        if (recordType == null || recordType.isBlank()) {
            return "CONSULTATION";
        }
        String normalizedRecordType = recordType.trim().toUpperCase();
        return switch (normalizedRecordType) {
            case "CONSULTATION", "DIAGNOSIS", "TREATMENT", "FOLLOW_UP", "EMERGENCY", "SURGERY", "LAB_RESULT", "GENERAL_NOTE" -> normalizedRecordType;
            default -> "CONSULTATION";
        };
    }
}