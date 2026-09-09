package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.LabTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class LabTestDAO {

    private final JdbcTemplate jdbcTemplate;

    public LabTestDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert SQL results into a LabTest model object
    private final RowMapper<LabTest> labTestRowMapper = new RowMapper<LabTest>() {
        @Override
        public LabTest mapRow(ResultSet rs, int rowNum) throws SQLException {
            LabTest test = new LabTest();
            test.setRequestID(rs.getInt("request_id"));
            test.setPatientID(rs.getInt("patient_id"));
            test.setDoctorID(rs.getInt("doctor_id"));
            test.setTestName(rs.getString("test_name"));
            test.setCategory(rs.getString("category"));
            test.setStatus(rs.getString("status"));
            test.setSampleStatus(rs.getString("sample_status"));
            test.setResultsSummary(rs.getString("results_summary"));
            test.setFilePath(rs.getString("file_path"));
            test.setRequestedDate(rs.getTimestamp("requested_date"));
            test.setCompletedDate(rs.getTimestamp("completed_date"));

            // Optional joining labels if present in queries
            try {
                test.setPatientName(rs.getString("patient_name"));
            } catch (SQLException ignored) {}

            try {
                test.setDoctorName(rs.getString("doctor_name"));
            } catch (SQLException ignored) {}

            return test;
        }
    };

    /*
     * Fetch all lab requests for the Lab Technician dashboard queue
     */
    public List<LabTest> getAllLabRequests() {
        String sql = """
            SELECT lr.*, 
                   CONCAT(u1.first_name, ' ', u1.last_name) AS patient_name,
                   CONCAT('Dr. ', u2.first_name, ' ', u2.last_name) AS doctor_name
            FROM lab_requests lr
            JOIN patients p ON lr.patient_id = p.patient_id
            JOIN users u1 ON p.user_id = u1.user_id
            JOIN doctors d ON lr.doctor_id = d.doctor_id
            JOIN users u2 ON d.user_id = u2.user_id
            ORDER BY lr.requested_date DESC
        """;
        return jdbcTemplate.query(sql, labTestRowMapper);
    }

    /*
     * Fetch lab tests for a specific patient (for EHR / Patient view)
     */
    public List<LabTest> getLabRequestsByPatientId(int patientId) {
        String sql = """
            SELECT lr.*, 
                   CONCAT(u1.first_name, ' ', u1.last_name) AS patient_name,
                   CONCAT('Dr. ', u2.first_name, ' ', u2.last_name) AS doctor_name
            FROM lab_requests lr
            JOIN patients p ON lr.patient_id = p.patient_id
            JOIN users u1 ON p.user_id = u1.user_id
            JOIN doctors d ON lr.doctor_id = d.doctor_id
            JOIN users u2 ON d.user_id = u2.user_id
            WHERE lr.patient_id = ?
            ORDER BY lr.requested_date DESC
        """;
        return jdbcTemplate.query(sql, labTestRowMapper, patientId);
    }

    /*
     * Fetch a single lab request by ID
     */
    public LabTest getLabRequestById(int requestId) {
        String sql = "SELECT lr.*, NULL AS patient_name, NULL AS doctor_name FROM lab_requests lr WHERE lr.request_id = ?";
        List<LabTest> results = jdbcTemplate.query(sql, labTestRowMapper, requestId);
        return results.isEmpty() ? null : results.get(0);
    }

    /*
     * Create a new lab request (called when a doctor orders a test)
     */
    public boolean createLabRequest(int patientId, int doctorId, String testName, String category) {
        String sql = "INSERT INTO lab_requests (patient_id, doctor_id, test_name, category) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, patientId, doctorId, testName, category) > 0;
    }

    /*
     * Update sample pipeline tracking (e.g., NOT_COLLECTED -> SAMPLE_COLLECTED -> IN_TESTING)
     */
    public boolean updateSampleStatus(int requestId, String sampleStatus, String overallStatus) {
        String sql = "UPDATE lab_requests SET sample_status = ?, status = ? WHERE request_id = ?";
        return jdbcTemplate.update(sql, sampleStatus, overallStatus, requestId) > 0;
    }

    /*
     * Enter final lab results and attach report file URL/path
     */
    public boolean submitLabResults(int requestId, String resultsSummary, String filePath) {
        String sql = """
            UPDATE lab_requests 
            SET results_summary = ?, 
                file_path = ?, 
                status = 'COMPLETED', 
                completed_date = CURRENT_TIMESTAMP 
            WHERE request_id = ?
        """;
        return jdbcTemplate.update(sql, resultsSummary, filePath, requestId) > 0;
    }
}