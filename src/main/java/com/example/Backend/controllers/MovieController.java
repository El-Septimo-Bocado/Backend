package com.example.Backend.controllers;

import java.util.List;

import com.example.Backend.modelos.Movie;
import com.example.Backend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/movies")
@Tag(name = "Películas", description = "API de cartelera")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }


    // 📄 1. Listar todas las películas

    @GetMapping
    @Operation(summary = "Listar todas las películas")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<Movie>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    // 🔍 2. Obtener una película por ID

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<Movie> get(@PathVariable Long id) {
        Movie m = service.findById(id);
        return (m != null)
                ? ResponseEntity.ok(m)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    // 🧩 3. Crear película (ADMIN)

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Crear nueva película (solo ADMIN)")
    @ApiResponse(responseCode = "201", description = "Película creada")
    public ResponseEntity<Movie> create(@RequestBody Movie m) {
        Movie saved = service.save(m);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // 🛠️ 4. Actualizar película (ADMIN)

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar película existente (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película actualizada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<Movie> update(@PathVariable Long id, @RequestBody Movie m) {
        Movie exist = service.findById(id);
        if (exist == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        m.setId(id);
        Movie updated = service.update(m);
        return ResponseEntity.ok(updated);
    }


    // ❌ 5. Eliminar película (ADMIN)
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar película por ID (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Movie exist = service.findById(id);
        if (exist == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}