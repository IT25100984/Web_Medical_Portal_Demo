package com.webmedicalportaldemo.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private int userID;
    private String actionType;
    private String description;
    private LocalDateTime timestamp;

    public AuditLog() {
    }

    public AuditLog(int userID, String actionType, String description, LocalDateTime timestamp) {
        this.userID = userID;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}