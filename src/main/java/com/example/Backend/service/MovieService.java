package com.example.Backend.service;

import java.util.List;

import com.example.Backend.modelos.Movie;
import com.example.Backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MovieService {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    // Crear o guardar película
    public Movie save(Movie m) {
        return repo.save(m);
    }

    // Buscar por ID
    public Movie findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // Listar todas las películas
    public List<Movie> findAll() {
        return repo.findAll();
    }

    // Actualizar (usa save internamente)
    public Movie update(Movie m) {
        return repo.save(m);
    }

    // Eliminar por ID
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}