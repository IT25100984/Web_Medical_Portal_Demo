package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public boolean updateUserStatus(int userID, boolean active) {
        String sql = "UPDATE users SET active = ? WHERE user_id = ?";
        int rows = jdbcTemplate.update(sql, active, userID);
        return rows > 0;
    }

    public boolean adminResetPassword(int userID, String newPassword) {
        // Remember to hash your password in production
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        int rows = jdbcTemplate.update(sql, newPassword, userID);
        return rows > 0;
    }
}