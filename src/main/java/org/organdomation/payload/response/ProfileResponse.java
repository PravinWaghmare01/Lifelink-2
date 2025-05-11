package org.organdomation.payload.response;

public class ProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String userType;
    private boolean profileExists;
    private Object profileData;
    private String message;
    
    // Getters and setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public boolean isProfileExists() {
        return profileExists;
    }
    
    public void setProfileExists(boolean profileExists) {
        this.profileExists = profileExists;
    }
    
    public Object getProfileData() {
        return profileData;
    }
    
    public void setProfileData(Object profileData) {
        this.profileData = profileData;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}