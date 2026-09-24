package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.dao.UserDAO;
import com.webmedicalportaldemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean updateUserStatus(int userId, boolean isActive) {
        return userDAO.updateUserStatus(userId, isActive);
    }

    public boolean adminResetPassword(int userID, String newPassword) {
        return userDAO.changePassword(userID, newPassword);
    }
}