package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.dao.LabReportDAO;
import com.webmedicalportaldemo.model.LabReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LabReportService {

    private final LabReportDAO labReportDAO;

    @Autowired
    public LabReportService(LabReportDAO labReportDAO) {
        this.labReportDAO = labReportDAO;
    }

    /**
     * Creates a new lab test order submitted by a doctor.
     */
    public boolean createDiagnosticRequest(int patientId, int doctorId, int appointmentId,
                                           String testType, String priority, String clinicalNotes) {
        try {
            LabReport report = new LabReport();
            report.setPatientId(patientId);     // Changed from setPatientID
            report.setDoctorId(doctorId);       // Changed from setDoctorID
            report.setAppointmentId(appointmentId); // Changed from setAppointmentID
            report.setTestType(testType);
            report.setPriority(priority != null ? priority : "ROUTINE");
            report.setClinicalNotes(clinicalNotes);
            report.setStatus("REQUESTED"); // Initial queue status for Lab Technician
            report.setRequestedAt(LocalDateTime.now());

            return labReportDAO.save(report) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all lab requests pending processing for the Lab Technician Queue.
     */
    public List<LabReport> getAllDiagnosticRequests() {
        return labReportDAO.getAllLabRequests();
    }

    /**
     * Updates the diagnostic sample status through the pipeline (REQUESTED -> IN_PROGRESS -> COMPLETED).
     */
    public boolean updateSampleStatus(int requestId, String newStatus) {
        return labReportDAO.updateStatus(requestId, newStatus);
    }

    /**
     * Submits final lab results entered by the lab technician.
     */
    public boolean submitTestResults(int requestId, String results, String comments) {
        return labReportDAO.saveResults(requestId, results, comments, "COMPLETED");
    }

    /**
     * Retrieves all lab reports associated with a specific patient for EHR history.
     */
    public List<LabReport> getReportsByPatientId(int patientId) {
        return labReportDAO.findByPatientId(patientId);
    }

    public boolean updateSampleStatus(int requestId, String sampleStatus, String overallStatus) {
        return labReportDAO.updateSampleStatus(requestId, sampleStatus, overallStatus);
    }

    public boolean submitLabResults(int requestId, String resultsSummary, String filePath) {
        return labReportDAO.submitLabResults(requestId, resultsSummary, filePath);
    }
}