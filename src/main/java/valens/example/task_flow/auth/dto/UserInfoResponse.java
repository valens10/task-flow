package valens.example.task_flow.auth.dto;

import java.util.Set;
import java.util.UUID;
import valens.example.task_flow.users.entity.Role;

public class UserInfoResponse {

    private UUID userId;
    private String email;
    private String fullName;
    private boolean enabled;
    private Set<Role> roles;

    // Constructors
    public UserInfoResponse() {
    }

    public UserInfoResponse(UUID userId, String email, String fullName, boolean enabled, Set<Role> roles) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.enabled = enabled;
        this.roles = roles;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
