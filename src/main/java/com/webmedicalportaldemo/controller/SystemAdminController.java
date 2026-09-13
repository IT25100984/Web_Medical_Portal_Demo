package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.model.AuditLog;
import com.webmedicalportaldemo.service.UserService;
import com.webmedicalportaldemo.service.AuditLogService;
import com.webmedicalportaldemo.service.DatabaseBackupService;
import com.webmedicalportaldemo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/system")
public class SystemAdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuditLogService auditLogService;
    private final DatabaseBackupService backupService;

    @Autowired
    public SystemAdminController(UserService userService,
                                 RoleService roleService,
                                 AuditLogService auditLogService,
                                 DatabaseBackupService backupService) {
        this.userService = userService;
        this.roleService = roleService;
        this.auditLogService = auditLogService;
        this.backupService = backupService;
    }

    /**
     * Renders the System Administration Dashboard
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        // Pass session user metadata to header panel
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        // CORRECTED: Pointing to the admin folder
        return "admin/system_admin_dashboard";
    }

    /**
     * PBI-24: Manage User Accounts & Status
     */
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);

        // CORRECTED: Pointing to the nested views folder
        return "admin/system_admin_views/user_management";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("userID") int userID,
                                   @RequestParam("active") boolean active,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = userService.updateUserStatus(userID, active);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getuserID(),
                    "TOGGLE_USER_STATUS", "Updated user ID " + userID + " active status to " + active);
            redirectAttributes.addAttribute("msg", "success");
        } else {
            redirectAttributes.addAttribute("error", "operationFailed");
        }

        return "redirect:/system/users";
    }

    @PostMapping("/users/reset-password")
    public String resetUserPassword(@RequestParam("userID") int userID,
                                    @RequestParam("newPassword") String newPassword,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = userService.adminResetPassword(userID, newPassword);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getuserID(),
                    "ADMIN_PASSWORD_RESET", "Reset password for user ID: " + userID);
            redirectAttributes.addAttribute("msg", "success");
        } else {
            redirectAttributes.addAttribute("error", "operationFailed");
        }

        return "redirect:/system/users";
    }

    /**
     * PBI-21: Configure Role-Based Access Control (RBAC) across 6 user types
     */
    @GetMapping("/roles")
    public String manageRoles(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("roles", roleService.getAllSystemRoles());
        model.addAttribute("userRoleMappings", roleService.getUserRoleMappings());

        // CORRECTED: Pointing to the nested views folder
        return "admin/system_admin_views/role_management";
    }

    @GetMapping("/roles/edit")
    public String showEditRolePage(@RequestParam("role") String role, HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("selectedRole", role);

        // Filter users directly using your existing userService
        List<User> roleUsers = userService.getAllUsers().stream()
                .filter(u -> role.equalsIgnoreCase(u.getRole()))
                .toList();

        model.addAttribute("roleUsers", roleUsers);

        return "admin/system_admin_views/role_edit";
    }

    @PostMapping("/roles/update")
    public String updateUserRole(@RequestParam("userID") int userID,
                                 @RequestParam("role") String newRole,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = roleService.assignUserRole(userID, newRole);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getuserID(),
                    "UPDATE_ROLE", "Assigned role " + newRole + " to user ID " + userID);
            redirectAttributes.addAttribute("msg", "success");
        } else {
            redirectAttributes.addAttribute("error", "operationFailed");
        }

        return "redirect:/system/roles";
    }

    /**
     * PBI-22: View System Audit Logs & Critical Emergency Alerts
     */
    @GetMapping("/auditLogs")
    public String viewAuditLogs(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        List<AuditLog> logs = auditLogService.getRecentAuditLogs();
        model.addAttribute("auditLogs", logs);

        // CORRECTED: Pointing to the nested views folder
        return "admin/system_admin_views/audit_logs";
    }

    /**
     * PBI-23: Automated & Differential Database Backups
     */
    @GetMapping("/backups")
    public String manageBackups(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("backupHistory", backupService.getBackupLogs());
        model.addAttribute("backupSchedule", backupService.getCurrentScheduleConfig());

        // CORRECTED: Pointing to the nested views folder
        return "admin/system_admin_views/database_backups";
    }

    @PostMapping("/backups/trigger")
    public String triggerManualBackup(
            // CORRECTED: Added defaultValue to prevent 400 Bad Request if the JSP form doesn't send it
            @RequestParam(value = "backupType", defaultValue = "FULL") String backupType,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = backupService.executeBackup(backupType); // "FULL" or "DIFFERENTIAL"
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getuserID(),
                    "MANUAL_BACKUP", "Executed manual " + backupType + " database backup.");
            redirectAttributes.addAttribute("msg", "success");
        } else {
            redirectAttributes.addAttribute("error", "operationFailed");
        }

        return "redirect:/system/backups";
    }
    
    /**
     * Private Security Helper Check
     */
    private boolean isSystemAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && "SYSTEM_ADMIN".equalsIgnoreCase(user.getRole());
    }
}