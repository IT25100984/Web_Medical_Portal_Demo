package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
            emp.setEmployeeID(rs.getString("employee_id"));
            emp.setFirstName(rs.getString("first_name"));
            emp.setLastName(rs.getString("last_name"));
            emp.setEmail(rs.getString("email"));
            emp.setRole(rs.getString("role"));
            emp.setDepartment(rs.getString("department"));
            emp.setRegistered(rs.getBoolean("is_registered"));
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

    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO employee_registry (employee_id, first_name, last_name, email, role, department, is_registered, user_id) VALUES (?, ?, ?, ?, ?, ?, 0, NULL)";
        return jdbcTemplate.update(sql, emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getRole(), emp.getDepartment()) > 0;
    }

    public boolean linkUserToEmployee(String employeeId, int userId) {
        // Finds the employee by employee_id and links user_id
        String sql = "UPDATE employee_registry SET user_id = ?, is_registered = 1 WHERE employee_id = ?";
        return jdbcTemplate.update(sql, userId, employeeId) > 0;
    }

    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employee_registry SET first_name = ?, last_name = ?, email = ?, role = ?, department = ? WHERE employee_id = ?";
        return jdbcTemplate.update(sql, emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getRole(), emp.getDepartment(), emp.getEmployeeId()) > 0;
    }

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employee_registry";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return (count != null) ? count : 0;
    }

    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employee_registry WHERE employee_id = ?";
        return jdbcTemplate.update(sql, employeeId) > 0;
    }
}