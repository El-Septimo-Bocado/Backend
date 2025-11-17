package com.example.Backend.security;

import com.example.Backend.modelos.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtUtil {
    // Llave secreta utilizada para firmar y validar tokens JWT
    private final SecretKey key;

    // Tiempo de vida del token en milisegundos
    private final long ttlMs;

    // Constructor que inicializa la clave y el tiempo de expiración
    public JwtUtil() {
        // Obtiene la clave JWT desde las variables de entorno; si no existe, usa un defecto
        String raw = System.getenv().getOrDefault("JWT_SECRET", "dev_secret_please_change_min_32_bytes");

        byte[] bytes;
        try {
            // Intenta decodificar la clave asumiendo que está en Base64
            bytes = Decoders.BASE64.decode(raw);
        } catch (Exception e) {
            // Si no está en Base64, usa directamente el texto en UTF-8
            bytes = raw.getBytes(StandardCharsets.UTF_8);
        }

        // Asegura que la clave tenga al menos 32 bytes (recomendado para HMAC-SHA-256)
        if (bytes.length < 32) {
            bytes = Arrays.copyOf(bytes, 32); // rellena con ceros si es necesario
        }

        // Crea una clave secreta válida a partir del arreglo de bytes
        this.key = Keys.hmacShaKeyFor(bytes);

        // Lee el tiempo de vida del token desde las variables de entorno; por defecto 24 horas
        long hours = Long.parseLong(System.getenv().getOrDefault("JWT_TTL_HOURS", "24"));
        this.ttlMs = hours * 3600_000L; // convierte horas a milisegundos
    }

    // Genera un token JWT para un usuario dado
    public String generate(Usuario u) {
        Date now = new Date(); // Fecha actual
        Date exp = new Date(now.getTime() + ttlMs); // Fecha de expiración basada en ttlMs

        return Jwts.builder()
                .setSubject(String.valueOf(u.getId())) // Asigna el ID del usuario como 'subject'
                .setIssuedAt(now) // Fecha de emisión
                .setExpiration(exp) // Fecha de expiración
                .claim("email", u.getEmail()) // Añade información adicional (email)
                .claim("rol", u.getRol().name()) // Añade el rol del usuario
                .signWith(key, SignatureAlgorithm.HS256) // Firma el token con la clave y algoritmo HMAC-SHA-256
                .compact(); // Construye el token en formato compacto (String)
    }

    // Parsea el token y obtiene los 'claims' (información cifrada)
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Establece la clave para validar la firma
                .build()
                .parseClaimsJws(token) // Parsea y valida el token
                .getBody(); // Obtiene la parte del cuerpo (claims)
    }

    // Extrae el ID del usuario a partir del token
    public Long userId(String token) {
        return Long.valueOf(parseClaims(token).getSubject()); // El subject contiene el ID como String
    }
}
