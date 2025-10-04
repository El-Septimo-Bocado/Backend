package com.example.Backend.service;

import java.util.List;

import com.example.Backend.modelos.Movie;
import com.example.Backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MovieService {
    private final MovieRepository repo;

    @Autowired
    public MovieService(MovieRepository repo) {
        this.repo = repo;
        initSample();
    }

    private void initSample() {

        save(new Movie("Kill Bill: Vol. 2", "/iconos/Kill-Bill-2.png", null, "Quentin Tarantino",
                "Acción, Thriller", "2h 16m", 5.0, true));
        save(new Movie("Volver al futuro II", "/iconos/back-2.png", null, "Robert Zemeckis",
                "Aventura, Sci-Fi", "1h 48m", 5.0, true));
        save(new Movie("Star Wars: Episodio III", "/iconos/star-3.png", null, "George Lucas",
                "Sci-Fi, Acción", "2h 20m", 4.5, true));
        save(new Movie("The Social Network", "/iconos/social.png", null, "David Fincher",
                "Drama, Biografía", "2h 0m", 5.0, true));
        save(new Movie("Saw V", "/iconos/sawv.png", null, "David Hackl",
                "Terror, Thriller", "1h 32m", 3.0, true));
    }

    public Movie save(Movie m) {
        return repo.save(m);
    }

    public Movie findById(String id) {
        return repo.findById(id);
    }

    public List<Movie> findAll() {
        return repo.findAll();
    }

    public Movie update(Movie m) {
        return repo.update(m);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }
}