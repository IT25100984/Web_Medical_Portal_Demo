package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.LabReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class LabReportDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LabReportDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<LabReport> labReportRowMapper = (rs, rowNum) -> {
        LabReport report = new LabReport();
        report.setRequestId(rs.getInt("request_id"));
        report.setPatientName(rs.getString("patient_name"));
        report.setDoctorName(rs.getString("doctor_name"));
        report.setTestName(rs.getString("test_name"));
        report.setCategory(rs.getString("category"));
        report.setPriority(rs.getString("priority"));
        report.setSampleStatus(rs.getString("sample_status"));
        report.setStatus(rs.getString("status"));
        report.setClinicalNotes(rs.getString("clinical_notes"));
        report.setResultsSummary(rs.getString("results_summary"));
        return report;
    };

    /**
     * Base SQL query containing columns and joins without trailing WHERE or ORDER BY clauses.
     */
    private String getBaseSelectSql() {
        return "SELECT lr.request_id, " +
                "COALESCE(CONCAT(u1.first_name, ' ', u1.last_name), CONCAT('Patient #', lr.patient_id)) AS patient_name, " +
                "COALESCE(CONCAT('Dr. ', u2.first_name, ' ', u2.last_name), CONCAT('Doctor #', lr.doctor_id)) AS doctor_name, " +
                "lr.test_name, COALESCE(lr.category, 'General Diagnostic') AS category, " +
                "COALESCE(lr.priority, 'ROUTINE') AS priority, " +
                "COALESCE(lr.sample_status, 'NOT_COLLECTED') AS sample_status, " +
                "COALESCE(lr.status, 'REQUESTED') AS status, " +
                "lr.clinical_notes, lr.results_summary, " +
                "lr.file_path, lr.requested_date, lr.completed_date " +
                "FROM lab_requests lr " +
                "LEFT JOIN patients p ON lr.patient_id = p.patient_id " +
                "LEFT JOIN users u1 ON p.user_id = u1.user_id " +
                "LEFT JOIN users u2 ON lr.doctor_id = u2.user_id";
    }

    public List<LabReport> getAllLabRequests() {
        try {
            String sql = getBaseSelectSql() + " ORDER BY lr.request_id DESC";
            return jdbcTemplate.query(sql, labReportRowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<LabReport> getLabRequestsByPatientId(int patientId) {
        try {
            String sql = getBaseSelectSql() + " WHERE lr.patient_id = ? ORDER BY lr.request_id DESC";
            return jdbcTemplate.query(sql, labReportRowMapper, patientId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<LabReport> findByPatientId(int patientId) {
        return getLabRequestsByPatientId(patientId);
    }

    public LabReport getLabRequestById(int requestId) {
        try {
            String sql = getBaseSelectSql() + " WHERE lr.request_id = ?";
            List<LabReport> results = jdbcTemplate.query(sql, labReportRowMapper, requestId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LabReport findById(int requestId) {
        return getLabRequestById(requestId);
    }

    public int save(LabReport report) {
        String sql = """
            INSERT INTO lab_requests 
            (patient_id, doctor_id, appointment_id, test_name, category, priority, clinical_notes, status, sample_status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        return jdbcTemplate.update(sql,
                report.getPatientId(),
                report.getDoctorId(),
                report.getAppointmentId() > 0 ? report.getAppointmentId() : null,
                report.getTestType() != null ? report.getTestType() : report.getTestName(),
                report.getCategory() != null ? report.getCategory() : "General Diagnostic",
                report.getPriority() != null ? report.getPriority() : "ROUTINE",
                report.getClinicalNotes(),
                report.getStatus() != null ? report.getStatus() : "REQUESTED",
                report.getSampleStatus() != null ? report.getSampleStatus() : "NOT_COLLECTED"
        );
    }

    public boolean createLabRequest(int patientId, int doctorId, String testName, String category) {
        LabReport report = new LabReport();
        report.setPatientId(patientId);
        report.setDoctorId(doctorId);
        report.setTestName(testName);
        report.setCategory(category);
        return save(report) > 0;
    }

    public boolean updateSampleStatus(int requestId, String sampleStatus, String overallStatus) {
        String sql = "UPDATE lab_requests SET sample_status = ?, status = ? WHERE request_id = ?";
        return jdbcTemplate.update(sql, sampleStatus, overallStatus, requestId) > 0;
    }

    public boolean updateStatus(int requestId, String overallStatus) {
        String sql = "UPDATE lab_requests SET status = ? WHERE request_id = ?";
        return jdbcTemplate.update(sql, overallStatus, requestId) > 0;
    }

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

    public boolean saveResults(int requestId, String resultsSummary, String comments, String status) {
        String combinedSummary = resultsSummary;
        if (comments != null && !comments.isBlank()) {
            combinedSummary += " | Notes: " + comments;
        }
        String sql = """
            UPDATE lab_requests 
            SET results_summary = ?, 
                status = ?, 
                completed_date = CURRENT_TIMESTAMP 
            WHERE request_id = ?
        """;
        return jdbcTemplate.update(sql, combinedSummary, status, requestId) > 0;
    }
}