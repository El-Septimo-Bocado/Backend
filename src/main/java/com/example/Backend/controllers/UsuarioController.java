package com.example.Backend.controllers;

import java.util.List;

import com.example.Backend.enums.RolUsuario;
import com.example.Backend.service.UsuarioService;
import com.example.Backend.modelos.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =========================
    // Helpers
    // =========================
    private RolUsuario parseRol(String raw) {
        if (raw == null) throw new IllegalArgumentException("ROL_REQUIRED");
        switch (raw.trim().toUpperCase()) {
            case "ADMIN": return RolUsuario.ADMIN;
            case "USER":  return RolUsuario.USER;
            default: throw new IllegalArgumentException("ROL_INVALID");
        }
    }

    // =========================
    // Endpoints
    // =========================
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String id) {
        Usuario usuario = usuarioService.findById(id);
        return (usuario != null) ? ResponseEntity.ok(usuario)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    @Operation(summary = "Crear usuario (no pone contraseña aquí; usar /api/auth/register)")
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario in) {
        // Ignorar campos sensibles si vienen
        in.setId(null);
        in.setPasswordHash(null);          // la contraseña se gestiona en /api/auth/register
        in.setRol(RolUsuario.USER);        // evita elevar rol por POST
        Usuario created = usuarioService.save(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario (nombre/email). No toca password/rol/fecha.")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable String id, @RequestBody Usuario in) {
        Usuario existing = usuarioService.findById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // Merge seguro
        if (in.getNombre() != null) existing.setNombre(in.getNombre());
        if (in.getEmail()  != null) existing.setEmail(in.getEmail());

        // No tocar:
        // existing.setPasswordHash(...)
        // existing.setRol(...)
        // existing.setFechaRegistro(...)

        Usuario updated = usuarioService.update(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id) {
        Usuario existing = usuarioService.findById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios por filtros", description = "Busca por nombre y/o email")
    public ResponseEntity<List<Usuario>> buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(usuarioService.buscarPorFiltros(nombre, email));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Cambiar rol de usuario (USER/ADMIN)")
    @ApiResponse(responseCode = "400", description = "Rol inválido")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<?> updateRole(@PathVariable String id, @RequestBody RoleDto body) {
        Usuario u = usuarioService.findById(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            RolUsuario role = parseRol(body == null ? null : body.rol);
            u.setRol(role);
            Usuario up = usuarioService.update(u);
            return ResponseEntity.ok(up);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    static class RoleDto { public String rol; }
}