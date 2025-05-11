package org.organdomation.payload.request;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    
    // Default constructor
    public ChangePasswordRequest() {
    }
    
    // Getters and setters
    public String getOldPassword() {
        return oldPassword;
    }
    
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}