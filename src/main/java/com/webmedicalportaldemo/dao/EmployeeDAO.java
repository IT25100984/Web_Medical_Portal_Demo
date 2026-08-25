package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EmployeeDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Check if employee exists in registry
     */
    public boolean validateEmployeeID(String employeeID) {
        String sql = "SELECT COUNT(*) FROM employee_registry WHERE employee_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, employeeID);
        return count != null && count > 0;
    }

    /**
     * Check if employee already registered
     */
    public boolean isRegistered(String employeeID) {
        String sql = "SELECT is_registered FROM employee_registry WHERE employee_id = ?";

        Boolean registered = jdbcTemplate.queryForObject(sql, Boolean.class, employeeID);
        return registered != null && registered;
    }

    /**
     * Retrieve employee info from registry
     */
    public Employee findEmployeeByID(String employeeID) {
        String sql = " SELECT employee_id, first_name, last_name, email, role, department "+
                " FROM employee_registry WHERE employee_id = ? ";

        List<Employee> employees = jdbcTemplate.query(sql, employeeRegistryMapper(), employeeID);
        return employees.isEmpty() ? null : employees.get(0);
    }

    /**
     * Mark employee as registered
     */
    public boolean markRegistered(String employeeID) {
        String sql = " UPDATE employee_registry SET is_registered = TRUE WHERE employee_id = ? ";

        return jdbcTemplate.update(sql, employeeID) > 0;
    }

    /**
     * Insert into employees table
     */
    public boolean registerEmployee(Employee employee) {
        String sql = " INSERT INTO employees (employee_id, user_id, department) VALUES (?, ?, ?) ";

        return jdbcTemplate.update(sql, employee.getEmployeeID(), employee.getUserID(), employee.getDepartment()) > 0;
    }

    /**
     * Get employee by user id
     */
    public Employee getEmployeeByUserId(int userId) {
        String sql = "SELECT e.employee_id, e.department, u.user_id, u.first_name,"
                    + " u.last_name, u.email, u.role " +
                " FROM employees e JOIN users u ON e.user_id = u.user_id WHERE u.user_id = ? ";

        List<Employee> employees = jdbcTemplate.query(sql, employeeMapper(), userId);
        return employees.isEmpty() ? null : employees.get(0);
    }

    public int getEmployeePkByUserId(int userId) {
        String sql = "SELECT employee_pk FROM employees WHERE user_id = ?";
        Integer pk = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return pk != null ? pk : 0;
    }

    private RowMapper<Employee> employeeMapper() {
        return (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setUserID(rs.getInt("user_id"));
            employee.setEmployeeID(rs.getString("employee_id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employee.setEmail(rs.getString("email"));
            employee.setRole(rs.getString("role"));
            employee.setDepartment(rs.getString("department"));
            return employee;
        };
    }

    private RowMapper<Employee> employeeRegistryMapper() {
        return (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setEmployeeID(rs.getString("employee_id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employee.setEmail(rs.getString("email"));
            employee.setRole(rs.getString("role"));
            employee.setDepartment(rs.getString("department"));
            return employee;
        };
    }
}