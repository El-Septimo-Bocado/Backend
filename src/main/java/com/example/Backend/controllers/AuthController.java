package com.example.Backend.controllers;

import com.example.Backend.modelos.Usuario;
import com.example.Backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    // Solo lo necesario: nombre, email, password
    record RegisterDto(String nombre, String email, String password) {}
    record LoginDto(String email, String password) {}

    static class UserView {
        public Long id;
        public String nombre;
        public String email;
        public String rol;

        UserView(Usuario u) {
            this.id = u.getId();
            this.nombre = u.getNombre();
            this.email = u.getEmail();
            this.rol = String.valueOf(u.getRol());
        }
    }

    static class AuthResponse {
        public String token;
        public UserView user;
        AuthResponse(String token, Usuario u) {
            this.token = token;
            this.user = new UserView(u);
        }
    }

    @Autowired
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        try {
            var r = auth.register(dto.nombre(), dto.email(), dto.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(r.token(), r.user()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("EMAIL_ALREADY_EXISTS");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("INVALID_INPUT");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            var r = auth.login(dto.email(), dto.password());
            return ResponseEntity.ok(new AuthResponse(r.token(), r.user()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_CREDENTIALS");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (authHeader == null) ? "" : authHeader.trim();
        if (token.toLowerCase().startsWith("bearer ")) token = token.substring(7).trim();

        var u = auth.me(token);
        return (u == null)
                ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                : ResponseEntity.ok(new UserView(u));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (authHeader == null) ? "" : authHeader.trim();
        if (token.toLowerCase().startsWith("bearer ")) token = token.substring(7).trim();
        auth.logout(token);
        return ResponseEntity.noContent().build();
    }
}