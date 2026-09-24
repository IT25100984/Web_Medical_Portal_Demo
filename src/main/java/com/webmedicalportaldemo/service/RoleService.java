package com.webmedicalportaldemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getAllSystemRoles() {
        // Returns the 6 core system roles
        return List.of("PATIENT", "DOCTOR", "PHARMACIST", "LAB_TECHNICIAN", "HOSPITAL_ADMIN", "SYSTEM_ADMIN");
    }

    public Map<String, Integer> getUserRoleMappings() {
        // Returns a count of active users assigned to each role for role_management.jsp
        String sql = "SELECT role, COUNT(*) AS total FROM users GROUP BY role";
        return jdbcTemplate.query(sql, rs -> {
            Map<String, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getString("role"), rs.getInt("total"));
            }
            return map;
        });
    }

    public boolean assignUserRole(int userID, String newRole) {
        // Executes the actual SQL UPDATE on the database
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newRole, userID);
        return rowsAffected > 0;
    }
}