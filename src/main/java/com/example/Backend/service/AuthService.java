package com.example.Backend.service;

import com.example.Backend.enums.RolUsuario;
import com.example.Backend.modelos.Usuario;
import com.example.Backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    // Sesiones en memoria: token -> id de usuario
    private final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();

    public record AuthResult(String token, Usuario user) {}

    private final String pepper;

    @Autowired
    public AuthService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
        this.pepper = System.getenv().getOrDefault("APP_PEPPER", "dev_pepper_cambia_esto");
    }

    // ---- DEBUG (temporal): para inspección rápida
    public int debugSessionSize() { return sessions.size(); }
    public boolean debugHas(String t) { return sessions.containsKey(t); }

    public AuthResult register(String nombre, String email, String rawPassword) {
        String normEmail = (email == null) ? null : email.trim().toLowerCase();
        if (normEmail == null || normEmail.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }
        if (repo.existsByEmail(normEmail)) {
            throw new IllegalStateException("EMAIL_ALREADY_EXISTS");
        }

        var u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(normEmail);
        u.setPasswordHash(encoder.encode(pepper + rawPassword));
        u = repo.save(u);

        String token = java.util.UUID.randomUUID().toString();
        sessions.put(token, u.getId());
        System.out.println("[register] token=" + token + " sessions=" + sessions.size());
        return new AuthResult(token, u);
    }

    public AuthResult login(String email, String rawPassword) {
        String normEmail = (email == null) ? null : email.trim().toLowerCase();
        var u = repo.findByEmail(normEmail).orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));
        if (!encoder.matches(pepper + rawPassword, u.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }
        String token = java.util.UUID.randomUUID().toString();
        sessions.put(token, u.getId());
        System.out.println("[login] token=" + token + " sessions=" + sessions.size());
        return new AuthResult(token, u);
    }

    public Usuario me(String token) {
        Long uid = sessions.get(token);
        return (uid == null) ? null : repo.findById(uid).orElse(null);
    }

    public void logout(String token) {
        sessions.remove(token);
        System.out.println("[logout] removed token=" + token + " sessions=" + sessions.size());
    }
}
