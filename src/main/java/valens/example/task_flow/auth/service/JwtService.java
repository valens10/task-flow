package valens.example.task_flow.auth.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;
import valens.example.task_flow.auth.config.JwtProperties;

@Service
public class JwtService {

    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        this.props = props;
    }

    public String issueAccessToken(String subject, Map<String, Object> claims) {
        return issueToken(subject, claims, props.getAccessTtlSeconds(), props.getAccessSecret());
    }

    public String issueRefreshToken(String subject, Map<String, Object> claims) {
        return issueToken(subject, claims, props.getRefreshTtlSeconds(), props.getRefreshSecret());
    }

    public boolean isValid(String token, boolean refresh) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            MACVerifier verifier = new MACVerifier(secret(refresh));
            if (!jwt.verify(verifier))
                return false;
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.toInstant().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public JWTClaimsSet parseClaims(String token) throws ParseException, BadJOSEException {
        return SignedJWT.parse(token).getJWTClaimsSet();
    }

    private String issueToken(String subject, Map<String, Object> claims, long ttlSeconds, String secret) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(props.getIssuer())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(ttlSeconds)));
            if (claims != null)
                claims.forEach(builder::claim);
            JWTClaimsSet claimSet = builder.build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            jwt.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to issue token", e);
        }
    }

    private byte[] secret(boolean refresh) {
        return (refresh ? props.getRefreshSecret() : props.getAccessSecret())
                .getBytes(StandardCharsets.UTF_8);
    }
}
