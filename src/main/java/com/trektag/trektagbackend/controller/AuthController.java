
package com.trektag.trektagbackend.controller;

import com.trektag.trektagbackend.model.User;
import com.trektag.trektagbackend.repository.UserRepository;
import com.trektag.trektagbackend.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174"
    },
    allowCredentials = "true"
)
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            UserRepository userRepository,
            SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.securityContextRepository = securityContextRepository;
    }

    public record AuthRequest(
        String name,
        String email,
        String password,
        String role
    ) {}

    public record LoginRequest(
        String email,
        String password
    ) {}

    public record AuthResponse(
        Long id,
        String name,
        String email,
        String role
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody AuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            User.Role role;

            try {
                role = User.Role.valueOf(
                    request.role().trim().toUpperCase()
                );
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body("Choose CUSTOMER or BUSINESS_OWNER.");
            }

            User user = authService.register(
                request.name(),
                request.email(),
                request.password(),
                role
            );

            authenticateUser(user, httpRequest, httpResponse);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            User user = authService.login(
                request.email(),
                request.password()
            );

            authenticateUser(user, httpRequest, httpResponse);

            return ResponseEntity.ok(toResponse(user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Not logged in.");
        }

        String email = authentication.getName();

        return userRepository.findByEmailIgnoreCase(email)
            .<ResponseEntity<?>>map(user ->
                ResponseEntity.ok(toResponse(user)))
            .orElseGet(() ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Account not found."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("Logged out successfully.");
    }

    private void authenticateUser(
            User user,
            HttpServletRequest request,
            HttpServletResponse response) {

        var authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
            );

        SecurityContext context =
            SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
            context, request, response
        );
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }
}
