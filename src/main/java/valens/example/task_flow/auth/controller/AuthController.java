package valens.example.task_flow.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import valens.example.task_flow.auth.dto.AuthResponse;
import valens.example.task_flow.auth.dto.LoginRequest;
import valens.example.task_flow.auth.dto.RefreshTokenRequest;
import valens.example.task_flow.auth.dto.SignupRequest;
import valens.example.task_flow.auth.dto.UserInfoResponse;
import valens.example.task_flow.auth.service.AuthService;
import valens.example.task_flow.users.entity.User;

/**
 * Authentication and authorization endpoints.
 * All endpoints support correlation IDs via X-Correlation-ID header.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed or user already exists")
    })
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT access and refresh tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the refresh token and logs out the user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info", description = "Returns information about the currently authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        UserInfoResponse response = new UserInfoResponse(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getFullName(),
                currentUser.isEnabled(),
                currentUser.getRoles());
        return ResponseEntity.ok(response);
    }
}
