package de.uni_leipzig.swtp.borna_lecker.aspects;

import java.util.Arrays;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RoleAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is missing");
        }

        Rolle role = jwtUtil.extractRole(token);

        if (Arrays.stream(requireRole.value()).noneMatch(r -> r.equals(role))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }
    }

}