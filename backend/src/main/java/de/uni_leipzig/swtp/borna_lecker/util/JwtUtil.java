package de.uni_leipzig.swtp.borna_lecker.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Rolle extractRole(String token) {
        return extractClaim(token, claims -> Rolle.valueOf((String) claims.get("role")));
    }

    public String extractStandort(String token) {
        return extractClaim(token, claims -> (String) claims.get("standort"));
    }

    public int extractGruppe(String token) {
        return extractClaim(token, claims -> (int) claims.get("gruppe"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, Account account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", account.getRolle().toString());
        claims.put("standort", account.getStandort());
        if (account.getRolle() != Rolle.VERWALTUNG && account.getRolle() != Rolle.STANDORTLEITUNG) {
            claims.put("gruppe", account.getGruppe().getGruppenNummer());
        }
        claims.put("requirePasswordChange", account.isRequirePasswordChange());

        return createToken(claims, userDetails.getUsername());
    }

    public String generateTestToken(String username, Rolle role, String standort, int gruppe) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.toString());
        claims.put("standort", standort);
        claims.put("gruppe", gruppe);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(secretKey).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
