package com.webmedicalportaldemo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    public List<String> getAllSystemRoles() {
        // Returns the 6 core system roles
        return List.of("PATIENT", "DOCTOR", "PHARMACIST", "LAB_TECHNICIAN", "HOSPITAL_ADMIN", "SYSTEM_ADMIN");
    }

    public Map<Integer, String> getUserRoleMappings() {
        // Logic to retrieve user ID to Role name mappings
        return Map.of();
    }

    public boolean assignUserRole(int userID, String newRole) {
        // Logic to update user RBAC permissions
        return true;
    }
}