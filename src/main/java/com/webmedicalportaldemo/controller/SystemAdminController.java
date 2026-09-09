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
            return "redirect:/access-denied";
        }

        // Pass session user metadata to header panel
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        return "system/system_admin_dashboard";
    }

    /**
     * PBI-24: Manage User Accounts & Status
     */
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "system/user_management";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("userId") int userId,
                                   @RequestParam("active") boolean active,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        boolean success = userService.updateUserStatus(userId, active);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getUserID(),
                    "TOGGLE_USER_STATUS", "Updated user ID " + userId + " active status to " + active);
            redirectAttributes.addAttribute("msg", "success");
        } else {
            redirectAttributes.addAttribute("error", "operationFailed");
        }

        return "redirect:/system/users";
    }

    @PostMapping("/users/reset-password")
    public String resetUserPassword(@RequestParam("userId") int userId,
                                    @RequestParam("newPassword") String newPassword,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        boolean success = userService.adminResetPassword(userId, newPassword);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getUserID(),
                    "ADMIN_PASSWORD_RESET", "Reset password for user ID: " + userId);
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
            return "redirect:/access-denied";
        }

        model.addAttribute("roles", roleService.getAllSystemRoles());
        model.addAttribute("userRoleMappings", roleService.getUserRoleMappings());
        return "system/role_management";
    }

    @PostMapping("/roles/update")
    public String updateUserRole(@RequestParam("userId") int userId,
                                 @RequestParam("role") String newRole,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        boolean success = roleService.assignUserRole(userId, newRole);
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getUserID(),
                    "UPDATE_ROLE", "Assigned role " + newRole + " to user ID " + userId);
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
            return "redirect:/access-denied";
        }

        List<AuditLog> logs = auditLogService.getRecentAuditLogs();
        model.addAttribute("auditLogs", logs);
        return "system/audit_logs";
    }

    /**
     * PBI-23: Automated & Differential Database Backups
     */
    @GetMapping("/backups")
    public String manageBackups(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("backupHistory", backupService.getBackupLogs());
        model.addAttribute("backupSchedule", backupService.getCurrentScheduleConfig());
        return "system/database_backups";
    }

    @PostMapping("/backups/trigger")
    public String triggerManualBackup(@RequestParam("backupType") String backupType,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!isSystemAdmin(session)) {
            return "redirect:/access-denied";
        }

        boolean success = backupService.executeBackup(backupType); // "FULL" or "DIFFERENTIAL"
        if (success) {
            auditLogService.logAction(((User) session.getAttribute("currentUser")).getUserID(),
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