package com.webmedicalportaldemo.service;
import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.HealthRecordDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dto.HealthRecordRequestDTO;
import com.webmedicalportaldemo.model.HealthRecord;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EHRService {

    private final HealthRecordDAO healthRecordDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;

    public EHRService(HealthRecordDAO healthRecordDAO, PatientDAO patientDAO, DoctorDAO doctorDAO) {
        this.healthRecordDAO = healthRecordDAO;
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
    }

    public int createHealthRecord(HealthRecordRequestDTO request, int doctorUserID) {
        Integer doctorID = doctorDAO.getDoctorIdByUserId(doctorUserID);
        if (doctorID == null || doctorID <= 0) {
            return -1;
        }
        if (request.getPatientID() == null || request.getPatientID() <= 0) {
            return -1;
        }
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setPatientID(request.getPatientID());
        healthRecord.setDoctorID(doctorID);
        healthRecord.setAppointmentID(request.getAppointmentID());
        healthRecord.setRecordType(normalizeRecordType(request.getRecordType()));
        healthRecord.setDiagnosis(cleanText(request.getDiagnosis()));
        healthRecord.setSymptoms(cleanText(request.getSymptoms()));
        healthRecord.setTreatmentPlan(cleanText(request.getTreatmentPlan()));
        healthRecord.setClinicalNotes(cleanText(request.getClinicalNotes()));
        healthRecord.setAllergies(cleanText(request.getAllergies()));
        healthRecord.setMedications(cleanText(request.getMedications()));
        healthRecord.setFollowUpInstructions(cleanText(request.getFollowUpInstructions()));
        return healthRecordDAO.createHealthRecord(healthRecord);
    }

    public HealthRecord getHealthRecordById(int healthRecordID) {
        if (healthRecordID <= 0) {
            return null;
        }
        return healthRecordDAO.getHealthRecordById(healthRecordID);
    }
    public List<HealthRecord> getPatientHealthRecords(int patientID) {
        return healthRecordDAO.getHealthRecordsByPatientId(patientID);
    }
    public List<HealthRecord> getLoggedInPatientHealthRecords(int patientUserID) {
        return healthRecordDAO.getHealthRecordsByPatientUserId(patientUserID);
    }
    public boolean updateHealthRecord(int healthRecordID, HealthRecordRequestDTO request, int doctorUserID) {
        if (healthRecordID <= 0) {
            return false;
        }
        Integer doctorID = doctorDAO.getDoctorIdByUserId(doctorUserID);
        if (doctorID == null || doctorID <= 0) {
            return false;
        }
        HealthRecord existingRecord = healthRecordDAO.getHealthRecordById(healthRecordID);
        if (existingRecord == null) {
            return false;
        }
        if (existingRecord.getDoctorID() == null || !existingRecord.getDoctorID().equals(doctorID)) {
            return false;
        }
        existingRecord.setRecordType(normalizeRecordType(request.getRecordType()));
        existingRecord.setDiagnosis(cleanText(request.getDiagnosis()));
        existingRecord.setSymptoms(cleanText(request.getSymptoms()));
        existingRecord.setTreatmentPlan(cleanText(request.getTreatmentPlan()));
        existingRecord.setClinicalNotes(cleanText(request.getClinicalNotes()));
        existingRecord.setAllergies(cleanText(request.getAllergies()));
        existingRecord.setMedications(cleanText(request.getMedications()));
        existingRecord.setFollowUpInstructions(cleanText(request.getFollowUpInstructions()));
        return healthRecordDAO.updateHealthRecord(existingRecord);
    }
    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }
    private String normalizeRecordType(String recordType) {
        if (recordType == null || recordType.isBlank()) {
            return "CONSULTATION";
        }
        String normalized = recordType.trim().toUpperCase();
        return switch (normalized) {
            case "CONSULTATION", "DIAGNOSIS", "TREATMENT", "FOLLOW_UP", "EMERGENCY", "SURGERY", "LAB_RESULT", "GENERAL_NOTE" -> normalized;
            default -> "CONSULTATION";
        };
    }
}