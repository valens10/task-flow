package valens.example.task_flow.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import valens.example.task_flow.auth.service.JwtService;
import valens.example.task_flow.users.entity.Role;
import valens.example.task_flow.users.entity.User;
import valens.example.task_flow.users.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtService.isValid(token, false)) {
                    JWTClaimsSet claims = jwtService.parseClaims(token);
                    String email = (String) claims.getClaim("email");
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = userRepository.findByEmailAndEnabledTrue(email).orElse(null);
                        if (user != null) {
                            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                                    .map(Role::name)
                                    .map(SimpleGrantedAuthority::new)
                                    .toList();
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    user, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            } catch (ParseException | BadJOSEException ignored) {
                // Invalid token; proceed without authentication
            } catch (Exception ignored) {
                // Any other parsing/verification issues - ignore and continue
            }
        }

        filterChain.doFilter(request, response);
    }
}
