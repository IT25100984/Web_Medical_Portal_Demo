package com.webmedicalportaldemo.dao;
import com.webmedicalportaldemo.dto.AppointmentDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AppointmentDAOInterface {
    // Appointment retrieval
    List<AppointmentDTO> getAppointmentsByPatient(int patientID);
    List<AppointmentDTO> getAppointmentsByDoctor(int doctorID);
    List<AppointmentDTO> getAllAppointments();
    // EHR access validation
    boolean doctorHasAccessToPatient(int doctorID, int patientID);
    // Appointment booking and status management
    boolean bookAppointment(int doctorID, int patientID, String date, String time, String type, String addCharge, BigDecimal totalFee);
    boolean updateAppointmentStatus(int appointmentID, String status, String newDate, String newTime, int userID);
    boolean cancelAppointment(int appointmentID);
    // Doctor availability management
    boolean setDoctorAvailability(int doctorID, Integer dayOfWeek, String startTime, String endTime);
    boolean setDoctorAvailability(int doctorID, String availableDate, String startTime, String endTime);
    List<String> getAvailableSlots(int doctorID, String date);
}