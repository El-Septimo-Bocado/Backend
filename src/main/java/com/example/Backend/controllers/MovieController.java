package com.example.Backend.controllers;

import java.util.List;

import com.example.Backend.modelos.Movie;
import com.example.Backend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Películas", description = "API de cartelera")
public class MovieController {
    private final MovieService service;

    @Autowired
    public MovieController(MovieService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar películas activas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido")
    })
    public ResponseEntity<List<Movie>> list() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<Movie> get(@PathVariable String id) {
        Movie m = service.findById(id);
        return (m != null) ? new ResponseEntity<>(m, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<Movie> create(@RequestBody Movie m) {
        return new ResponseEntity<>(service.save(m), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable String id, @RequestBody Movie m) {
        Movie exist = service.findById(id);
        if (exist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        m.setId(id);
        return new ResponseEntity<>(service.update(m), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Movie exist = service.findById(id);
        if (exist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}