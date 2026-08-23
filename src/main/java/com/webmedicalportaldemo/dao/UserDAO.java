package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // UPDATED: Now uses GeneratedKeyHolder to fetch auto-increment IDs for file synchronization
    public int saveUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password_hash, role, is_active) "
        + " VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.setBoolean(6, user.isActive());
            return ps;
        }, keyHolder);

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            int userId = keyHolder.getKey().intValue();
            user.setUserID(userId);
            return userId;
        }
        return -1;
    }

    public User login(String email, String password) {

        String sql = " SELECT user_id, first_name, last_name, email, role, is_active "+
            " FROM users WHERE email = ? AND password_hash = ? AND is_active = TRUE ";

        List<User> users = jdbcTemplate.query(sql, userRowMapper(), email, password);

        return users.isEmpty() ? null : users.get(0);
    }

    // NEW: Deletion method required to complete individual CRUD requirements (Delete)
    public boolean deleteUserById(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ? ";
        return jdbcTemplate.update(sql, userId) > 0;
    }

    public User findByEmail(String email) {
        String sql = " SELECT user_id, first_name, last_name, email, role, is_active "+
            " FROM users WHERE email = ? " ;

        List<User> users = jdbcTemplate.query(sql, userRowMapper(), email);
        return users.isEmpty() ? null : users.get(0);
    }

    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=? ";
        return jdbcTemplate.update(sql, newPassword, userId) > 0;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserID(rs.getInt("user_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setRole(rs.getString("role"));
            user.setActive(rs.getBoolean("is_active"));
            return user;
        };
    }
}