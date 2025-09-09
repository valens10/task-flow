package valens.example.task_flow.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private final String issuer;
    private final String accessSecret;
    private final String refreshSecret;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    @ConstructorBinding
    public JwtProperties(
            String issuer,
            String accessSecret,
            String refreshSecret,
            long accessTtlSeconds,
            long refreshTtlSeconds) {
        this.issuer = issuer != null ? issuer : "task-flow";
        this.accessSecret = accessSecret != null ? accessSecret : "dev-access-secret-change-me";
        this.refreshSecret = refreshSecret != null ? refreshSecret : "dev-refresh-secret-change-me";
        this.accessTtlSeconds = accessTtlSeconds > 0 ? accessTtlSeconds : 7200; // 2 hours
        this.refreshTtlSeconds = refreshTtlSeconds > 0 ? refreshTtlSeconds : 2592000;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public String getRefreshSecret() {
        return refreshSecret;
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }
}
