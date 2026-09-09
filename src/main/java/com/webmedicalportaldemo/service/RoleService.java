package com.webmedicalportaldemo.service;

import java.util.List;
import java.util.Map;

public class RoleService {

    public List<String> getAllSystemRoles() {
        // Returns the 6 core system roles
        return List.of("PATIENT", "DOCTOR", "NURSE", "PHARMACIST", "HOSPITAL_ADMIN", "SYSTEM_ADMIN");
    }

    public Map<Integer, String> getUserRoleMappings() {
        // Logic to retrieve user ID to Role name mappings
        return Map.of();
    }

    public boolean assignUserRole(int userId, String newRole) {
        // Logic to update user RBAC permissions
        return true;
    }
}