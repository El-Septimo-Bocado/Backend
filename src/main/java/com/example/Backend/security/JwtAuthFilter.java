package com.example.Backend.security;

import com.example.Backend.modelos.Usuario;
import com.example.Backend.repository.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // Objeto para manejar la lógica del token JWT
    private final JwtUtil jwt;

    // Repositorio para acceder a la base de datos de usuarios
    private final UsuarioRepository repo;

    // Constructor para inyectar dependencias
    public JwtAuthFilter(JwtUtil jwt, UsuarioRepository repo) {
        this.jwt = jwt;
        this.repo = repo;
    }

    // Método principal del filtro que se ejecuta por cada petición
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Obtener el encabezado 'Authorization' de la petición
        String h = req.getHeader("Authorization");

        // Verificar si el encabezado existe y empieza con 'Bearer '
        if (h != null && h.toLowerCase(Locale.ROOT).startsWith("bearer ")) {

            // Extraer el token removiendo la palabra 'Bearer '
            String token = h.substring(7).trim();
            try {
                // Obtener el ID del usuario que está dentro del token
                Long uid = jwt.userId(token);

                // Buscar el usuario por ID en la base de datos
                Optional<Usuario> uOpt = repo.findById(uid);

                // Si el usuario existe
                if (uOpt.isPresent()) {
                    var u = uOpt.get();

                    // Crear objeto de autenticación con el usuario y su rol
                    var auth = new UsernamePasswordAuthenticationToken(
                            u, // objeto Usuario
                            null, // sin credenciales (ya autenticado)
                            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name())) // asigna el rol al usuario
                    );

                    // Agregar detalles de la petición al objeto de autenticación
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // Si hay errores en el token (inválido, expirado, manipulado), no se autentica
            }
        }

        // Continuar con el resto del flujo de filtros de la petición HTTP
        chain.doFilter(req, res);
    }
}
