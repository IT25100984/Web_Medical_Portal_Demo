package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.dao.EmergencyDAO;
import com.webmedicalportaldemo.model.EmergencyRequest;
import com.webmedicalportaldemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyService {

    private final EmergencyDAO emergencyDAO;

    @Autowired
    public EmergencyService(EmergencyDAO emergencyDAO) {
        this.emergencyDAO = emergencyDAO;
    }

    public List<EmergencyRequest> getActiveEmergencies() {
        return emergencyDAO.getActiveEmergencies();
    }

    public List<User> getAvailableDoctors() {
        return emergencyDAO.getAvailableDoctors();
    }

    public boolean createEmergencyRequest(EmergencyRequest req) {
        return emergencyDAO.createEmergencyRequest(req);
    }

    public boolean assignDoctor(int requestId, int doctorId) {
        return emergencyDAO.assignDoctor(requestId, doctorId);
    }
}