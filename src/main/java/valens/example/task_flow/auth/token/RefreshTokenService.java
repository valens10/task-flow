package valens.example.task_flow.auth.token;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken issue(UUID userId, String token, Instant expiresAt, String userAgent, String ip) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setToken(token);
        rt.setExpiresAt(expiresAt);
        rt.setUserAgent(userAgent);
        rt.setIpAddress(ip);
        return repository.save(rt);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public void revoke(String token) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }

    public void purgeExpired(UUID userId) {
        repository.deleteByUserIdAndExpiresAtBefore(userId, Instant.now());
    }
}
