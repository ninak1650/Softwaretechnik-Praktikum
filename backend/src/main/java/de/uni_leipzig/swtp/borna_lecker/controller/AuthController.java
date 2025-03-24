package de.uni_leipzig.swtp.borna_lecker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.services.CustomUserDetailsService;
import de.uni_leipzig.swtp.borna_lecker.util.JwtUtil;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    static private class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username,
                        loginRequest.password));

        try {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username);
            final Account account = userDetailsService.getAccount(loginRequest.username);
            final String jwt = jwtUtil.generateToken(userDetails, account);
            return jwt;
        } catch (UsernameNotFoundException e) {
            throw new IDNotFoundException("Benutzername nicht gefunden");
        }
    }

    @RequireRole({ Rolle.GRUPPENLEITUNG, Rolle.VERWALTUNG, Rolle.STANDORTLEITUNG, Rolle.KÜCHE })
    @PostMapping("/validate")
    public ResponseEntity<String> validateLogin(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password));
            // Wenn keine Exception geworfen wird, ist die Authentifizierung erfolgreich.
            return ResponseEntity.ok("Good Request");
        } catch (BadCredentialsException e) {
            // Falsches Passwort oder Benutzername
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
        } catch (Exception e) {
            // Andere Fehler (optional)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}
