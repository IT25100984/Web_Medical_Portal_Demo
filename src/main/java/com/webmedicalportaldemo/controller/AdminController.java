package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.EmployeeDAO;
import com.webmedicalportaldemo.model.Employee;
import com.webmedicalportaldemo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeDAO employeeDAO;

    public AdminController(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @GetMapping("/employees")
    public String viewEmployeeRegistry(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HOSPITAL_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/employee_registry_view";
    }

    @GetMapping("/employees/add")
    public String showAddEmployeeForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HOSPITAL_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("employee", new Employee());
        return "admin/employee_registry_form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        if (employeeDAO.getEmployeeById(employee.getEmployeeId()) != null) {
            employeeDAO.updateEmployee(employee);
            return "redirect:/admin/employees?msg=updated";
        } else {
            employeeDAO.addEmployee(employee);
            return "redirect:/admin/employees?msg=added";
        }
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable("id") String employeeId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HOSPITAL_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        Employee emp = employeeDAO.getEmployeeById(employeeId);
        model.addAttribute("employee", emp);
        model.addAttribute("isEdit", true);
        return "admin/employee_registry_form";
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam("employeeId") String employeeId) {
        employeeDAO.deleteEmployee(employeeId);
        return "redirect:/admin/employees?msg=deleted";
    }
}