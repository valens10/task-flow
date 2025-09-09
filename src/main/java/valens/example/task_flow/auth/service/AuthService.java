package valens.example.task_flow.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valens.example.task_flow.auth.config.JwtProperties;
import valens.example.task_flow.auth.dto.AuthResponse;
import valens.example.task_flow.auth.dto.LoginRequest;
import valens.example.task_flow.auth.dto.SignupRequest;
import valens.example.task_flow.auth.dto.UserInfoResponse;
import valens.example.task_flow.auth.token.RefreshToken;
import valens.example.task_flow.auth.token.RefreshTokenService;
import valens.example.task_flow.users.entity.User;
import valens.example.task_flow.users.entity.Role;
import valens.example.task_flow.users.repository.UserRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
    }

    public AuthResponse signup(SignupRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(java.util.Set.of(Role.ROLE_USER)); // Default role

        User savedUser = userRepository.save(user);

        // Generate tokens
        return generateAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Get user details
        User user = userRepository.findByEmailAndEnabledTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.isValid(refreshToken, true)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Check if token exists in database and is not revoked
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Get user
        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Revoke old refresh token
        refreshTokenService.revoke(refreshToken);

        // Generate new tokens
        return generateAuthResponse(user);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public UserInfoResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isEnabled(),
                user.getRoles());
    }

    private AuthResponse generateAuthResponse(User user) {
        // Create claims for JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("fullName", user.getFullName());
        claims.put("roles", user.getRoles().stream().map(Role::name).toArray(String[]::new));

        // Generate tokens with JWT service
        String accessToken = jwtService.issueAccessToken(user.getId().toString(), claims);
        String refreshToken = jwtService.issueRefreshToken(user.getId().toString(), claims);

        // Store refresh token
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshTtlSeconds());
        refreshTokenService.issue(user.getId(), refreshToken, expiresAt, null, null);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                accessToken,
                refreshToken,
                jwtProperties.getAccessTtlSeconds(),
                user.getRoles());
    }
}
