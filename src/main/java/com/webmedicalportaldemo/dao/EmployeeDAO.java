package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeDAO {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> employeeRowMapper = new RowMapper<Employee>() {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee emp = new Employee();
            emp.setEmployeeId(rs.getString("employee_id"));
            emp.setFirstName(rs.getString("first_name"));
            emp.setLastName(rs.getString("last_name"));
            emp.setEmail(rs.getString("email"));
            emp.setRole(rs.getString("role"));
            emp.setDepartment(rs.getString("department"));
            emp.setRegistered(rs.getBoolean("is_registered"));

            int userId = rs.getInt("user_id");
            if (!rs.wasNull()) {
                emp.setUserId(userId);
            }

            return emp;
        }
    };

    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM employee_registry ORDER BY employee_id ASC";
        return jdbcTemplate.query(sql, employeeRowMapper);
    }

    public Employee getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employee_registry WHERE employee_id = ?";
        List<Employee> list = jdbcTemplate.query(sql, employeeRowMapper, employeeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public Employee getEmployeeByUserId(int userId) {
        String sql = "SELECT * FROM employee_registry WHERE user_id = ?";
        List<Employee> list = jdbcTemplate.query(sql, employeeRowMapper, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean createActiveEmployee(String employeeId, int userId, String department) {
        String sql = "INSERT INTO employees (employee_id, user_id, department) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employeeId, userId, department) > 0;
    }

    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO employee_registry (employee_id, first_name, last_name, email, role, department, is_registered, user_id) VALUES (?, ?, ?, ?, ?, ?, 0, NULL)";
        return jdbcTemplate.update(sql, emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getRole(), emp.getDepartment()) > 0;
    }

    public boolean linkUserToEmployee(String employeeId, int userId) {
        String sql = "UPDATE employee_registry SET user_id = ?, is_registered = 1 WHERE employee_id = ?";
        return jdbcTemplate.update(sql, userId, employeeId) > 0;
    }

    @Transactional
    public boolean updateEmployee(Employee emp) {
        // 1. Update management registry
        String sql = "UPDATE employee_registry SET first_name = ?, last_name = ?, email = ?, role = ?, department = ? WHERE employee_id = ?";
        int registryRows = jdbcTemplate.update(
                sql,
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getRole(),
                emp.getDepartment(),
                emp.getEmployeeId()
        );

        // 2. Retrieve linked user_id if not present on the object
        Integer userId = emp.getUserId();
        if (userId == null || userId == 0) {
            String findUserSql = "SELECT user_id FROM employee_registry WHERE employee_id = ?";
            try {
                userId = jdbcTemplate.queryForObject(findUserSql, Integer.class, emp.getEmployeeId());
            } catch (EmptyResultDataAccessException e) {
                userId = null;
            }
        }

        // 3. Cascade updates (email, names, role) to the active users authentication table
        if (userId != null && userId > 0) {
            String updateUserSql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, role = ? WHERE user_id = ?";
            jdbcTemplate.update(
                    updateUserSql,
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getRole(),
                    userId
            );
        }

        return registryRows > 0;
    }

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employee_registry";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return (count != null) ? count : 0;
    }

    @Transactional
    public boolean deleteEmployee(String employeeId) {
        String findUserSql = "SELECT user_id FROM employee_registry WHERE employee_id = ?";
        Integer userId = null;
        try {
            userId = jdbcTemplate.queryForObject(findUserSql, Integer.class, employeeId);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        jdbcTemplate.update("DELETE FROM employees WHERE employee_id = ?", employeeId);

        int registryDeleted = jdbcTemplate.update("DELETE FROM employee_registry WHERE employee_id = ?", employeeId);

        if (userId != null) {
            jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
        }

        return registryDeleted > 0;
    }
}