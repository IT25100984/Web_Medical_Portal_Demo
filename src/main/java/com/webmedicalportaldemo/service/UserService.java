package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.model.User;
import java.util.List;

public class UserService {

    public List<User> getAllUsers() {
        // Retrieve all registered system users across all roles
        return List.of();
    }

    public boolean updateUserStatus(int userId, boolean active) {
        // Logic to enable/disable user account
        return true;
    }

    public boolean adminResetPassword(int userId, String newPassword) {
        // Logic to hash and update user password
        return true;
    }
}
