package com.example.Backend.controllers;

import java.util.List;

import com.example.Backend.modelos.MenuItem;
import com.example.Backend.modelos.Movie;
import com.example.Backend.modelos.SeatStatus;
import com.example.Backend.modelos.Showtime;
import com.example.Backend.modelos.Usuario;
import com.example.Backend.service.MenuItemService;
import com.example.Backend.service.MovieService;
import com.example.Backend.service.SeatingService;
import com.example.Backend.service.ShowtimeService;
import com.example.Backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Operaciones de administrador (requiere X-User-Id header de un admin)")
public class AdminController {
    private final MovieService movieService;
    private final MenuItemService menuService;
    private final ShowtimeService showtimeService;
    private final SeatingService seatingService;
    private final UsuarioService usuarioService;

    @Autowired
    public AdminController(MovieService movieService, MenuItemService menuService,
                           ShowtimeService showtimeService, SeatingService seatingService,
                           UsuarioService usuarioService) {
        this.movieService = movieService;
        this.menuService = menuService;
        this.showtimeService = showtimeService;
        this.seatingService = seatingService;
        this.usuarioService = usuarioService;
    }

    private boolean checkAdmin(String userId) {
        if (userId == null) return false;
        Usuario u = usuarioService.findById(userId);
        return u != null && u.isAdmin();
    }

    private <T> ResponseEntity<T> unauthorized() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/movies")
    @Operation(summary = "Listar todas las películas (admin)")
    public ResponseEntity<List<Movie>> listMovies(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (!checkAdmin(userId)) return unauthorized();
        return new ResponseEntity<>(movieService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/movies/{id}")
    @Operation(summary = "Eliminar película por id (admin)")
    public ResponseEntity<Void> deleteMovie(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                            @PathVariable String id) {
        if (!checkAdmin(userId)) return unauthorized();
        Movie m = movieService.findById(id);
        if (m == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        movieService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/menu")
    @Operation(summary = "Listar items de menú (admin)")
    public ResponseEntity<List<MenuItem>> listMenu(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (!checkAdmin(userId)) return unauthorized();
        return new ResponseEntity<>(menuService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/menu/{id}")
    @Operation(summary = "Eliminar item del menú (admin)")
    public ResponseEntity<Void> deleteMenuItem(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                               @PathVariable String id) {
        if (!checkAdmin(userId)) return unauthorized();
        MenuItem it = menuService.findById(id);
        if (it == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        menuService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/showtimes")
    @Operation(summary = "Listar todas las funciones por película (admin) (usar ?movieId=)")
    public ResponseEntity<List<Showtime>> listShowtimes(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                       @RequestParam(required = false) String movieId) {
        if (!checkAdmin(userId)) return unauthorized();
        if (movieId == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(showtimeService.findAllByMovie(movieId), HttpStatus.OK);
    }

    @DeleteMapping("/showtimes/{id}")
    @Operation(summary = "Eliminar una función (admin)")
    public ResponseEntity<Void> deleteShowtime(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                               @PathVariable String id) {
        if (!checkAdmin(userId)) return unauthorized();
        Showtime s = showtimeService.findById(id);
        if (s == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        // borrar asientos asociados primero para mantener consistencia
        seatingService.deleteByShowtime(id);
        showtimeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/seats/{showtimeId}")
    @Operation(summary = "Listar estado de asientos para una función (admin)")
    public ResponseEntity<List<SeatStatus>> listSeats(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                      @PathVariable String showtimeId) {
        if (!checkAdmin(userId)) return unauthorized();
        return new ResponseEntity<>(seatingService.getMap(showtimeId), HttpStatus.OK);
    }

    @PostMapping("/seats/{showtimeId}/force-free")
    @Operation(summary = "Forzar liberar asientos (admin)")
    public ResponseEntity<Void> forceFreeSeats(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                               @PathVariable String showtimeId,
                                               @RequestBody List<String> seatCodes) {
        if (!checkAdmin(userId)) return unauthorized();
        seatingService.forceFreeSeats(showtimeId, seatCodes);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/seats/{showtimeId}/force-sold")
    @Operation(summary = "Forzar marcar asientos como vendidos (admin)")
    public ResponseEntity<Void> forceSoldSeats(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                               @PathVariable String showtimeId,
                                               @RequestBody List<String> seatCodes) {
        if (!checkAdmin(userId)) return unauthorized();
        seatingService.forceSoldSeats(showtimeId, seatCodes);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
