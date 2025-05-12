package com.mafi.app.data.model;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // Gerçek bir uygulamada şifreleri düz metin olarak saklamayın
    private String profilePictureUrl;
    private long createdAt;
    private boolean isActive;

    // Yapıcı metodlar
    public User() {}

    public User(int id, String username, String email, String password,
                String profilePictureUrl, long createdAt, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    // Getter ve Setter metodları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}