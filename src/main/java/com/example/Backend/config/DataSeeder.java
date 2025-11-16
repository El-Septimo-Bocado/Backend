package com.example.Backend.config;

import com.example.Backend.enums.RolUsuario;
import com.example.Backend.modelos.MenuItem;
import com.example.Backend.modelos.Movie;
import com.example.Backend.modelos.Showtime;
import com.example.Backend.modelos.Usuario;
import com.example.Backend.repository.MenuItemRepository;
import com.example.Backend.repository.MovieRepository;
import com.example.Backend.repository.ShowtimeRepository;
import com.example.Backend.repository.UsuarioRepository;
import com.example.Backend.service.AuthService;
import com.example.Backend.service.SeatingService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class DataSeeder {

    private final MovieRepository movies;
    private final MenuItemRepository menu;
    private final ShowtimeRepository showtimes;
    private final SeatingService seating;

    // 🔹 NUEVO: repositorio + servicio para usuarios
    private final UsuarioRepository usuarios;
    private final AuthService auth;

    public DataSeeder(MovieRepository movies,
                      MenuItemRepository menu,
                      ShowtimeRepository showtimes,
                      SeatingService seating,
                      UsuarioRepository usuarios,
                      AuthService auth) {
        this.movies = movies;
        this.menu = menu;
        this.showtimes = showtimes;
        this.seating = seating;
        this.usuarios = usuarios;
        this.auth = auth;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        seedAdmin();     // 🔹 primero creamos admin si no existe
        seedMovies();
        seedMenu();
        seedShowtimes();
    }

    // ======================================================
    // 👑 Admin inicial
    // ======================================================
    private void seedAdmin() {
        String adminEmail = "admin@septimobocado.local";
        String adminPass  = "admin123";

        if (usuarios.existsByEmail(adminEmail)) {
            return;
        }

        var result = auth.register("Admin", adminEmail, adminPass);
        Usuario u = result.user();

        u.setRol(RolUsuario.ADMIN);
        usuarios.save(u);

        System.out.println("✅ Admin inicial creado: " + adminEmail + " / " + adminPass);
    }

    // ======================================================
    // 🎬 Películas iniciales
    // ======================================================
    private void seedMovies() {
        if (movies.count() > 0) return;

        Movie m;

        m = new Movie();
        m.setTitulo("Kill Bill Vol. 2");
        m.setPoster("https://res.cloudinary.com/dxpgzf01b/image/upload/v1760561597/kill-bill-2-poster_ocz84v.jpg");
        m.setFondo("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688535/killbill2_waynou.jpg");
        m.setCaratula("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688543/Kill-Bill-2_qwdtln.png");
        m.setDirector("Quentin Tarantino");
        m.setGeneros("Acción");
        m.setDuracion("136 min");
        m.setDescripcion("Beatrix Kiddo continúa su camino de venganza, enfrentando a los últimos miembros del Escuadrón Asesino Víbora.");
        m.setRating(new BigDecimal("4.5"));
        m.setEstado("ACTIVA");   // 🔹 estado inicial
        m.setActivo(true);       // 🔹 por si lo usas también
        movies.save(m);

        m = new Movie();
        m.setTitulo("Volver al Futuro 2");
        m.setPoster("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688550/back-future-2-poster_hctqo8.jpg");
        m.setFondo("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688534/back2_tq1rrp.jpg");
        m.setCaratula("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688543/back-2_vdmldw.png");
        m.setDirector("Robert Zemeckis");
        m.setGeneros("Aventura");
        m.setDuracion("108 min");
        m.setDescripcion("Marty McFly viaja al futuro para evitar que su hijo arruine la vida de la familia McFly, pero algo sale mal.");
        m.setRating(new BigDecimal("4.8"));
        m.setEstado("ACTIVA");
        m.setActivo(true);
        movies.save(m);

        m = new Movie();
        m.setTitulo("The Social Network");
        m.setPoster("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688551/social-network-poster_tonoia.jpg");
        m.setFondo("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688542/social_i7ojzt.jpg");
        m.setCaratula("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688549/social_oxhiyi.png");
        m.setDirector("David Fincher");
        m.setGeneros("Drama");
        m.setDuracion("120 min");
        m.setDescripcion("La historia de Mark Zuckerberg y la creación de Facebook: ambición, traición y éxito en la era digital.");
        m.setRating(new BigDecimal("4.2"));
        m.setEstado("ACTIVA");
        m.setActivo(true);
        movies.save(m);

        m = new Movie();
        m.setTitulo("Star Wars Episodio III");
        m.setPoster("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688551/star-wars-3-poster_k4dull.jpg");
        m.setFondo("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688542/star3_o9qvst.jpg");
        m.setCaratula("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688549/star-3_ttx54m.png");
        m.setDirector("George Lucas");
        m.setGeneros("Ciencia ficción");
        m.setDuracion("140 min");
        m.setDescripcion("La caída de Anakin Skywalker al lado oscuro y el nacimiento del Imperio Galáctico.");
        m.setRating(new BigDecimal("4.7"));
        m.setEstado("ACTIVA");
        m.setActivo(true);
        movies.save(m);

        m = new Movie();
        m.setTitulo("Saw V");
        m.setPoster("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688550/saw-v-poster_fmcu18.jpg");
        m.setFondo("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688542/sawv_is7ti0.jpg");
        m.setCaratula("https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688544/sawv_igoct1.png");
        m.setDirector("David Hackl");
        m.setGeneros("Terror");
        m.setDuracion("92 min");
        m.setDescripcion("El detective Hoffman continúa con el legado de Jigsaw, pero alguien más podría conocer su secreto.");
        m.setRating(new BigDecimal("3.5"));
        m.setEstado("ACTIVA");
        m.setActivo(true);
        movies.save(m);

        System.out.println("✅ Películas iniciales creadas (todas en estado ACTIVA)");
    }

    // ======================================================
    // 🍔 Menú inicial
    // ======================================================
    private void seedMenu() {
        if (menu.count() > 0) return;

        menu.saveAll(List.of(
                item("Katana Sushi Roll", "Sushi picante inspirado en Kill Bill.", 40000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688533/sushi_smwhdi.png", "plato"),
                item("Hoverboard Burger", "Hamburguesa futurista al estilo Volver al Futuro II.", 25000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688530/burguer_udyaca.png", "plato"),
                item("Lightsaber Hotdog", "Perro caliente intergaláctico. Elige tu lado.", 20000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688532/hotdog_odaztr.png", "plato"),
                item("Fatality Ramen", "Ramen intenso, con sabores que pelean entre sí hasta el último sorbo.", 40000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688533/ramen_i4bufc.png", "plato"),
                item("Taco del Murciélago Nocturno", "Taco inspirado en la oscuridad de Batman.", 20000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688534/taco_gwv52y.png", "plato"),
                item("Facebook Fries", "Papas fritas para compartir, como un buen post.", 14000, "https://res.cloudinary.com/dxpgzf01b/image/upload/v1759688530/fries_vptqlt.png", "plato")
        ));

        System.out.println("✅ Menú inicial creado");
    }

    private MenuItem item(String nombre, String descripcion, int precio, String imagen, String categoria) {
        MenuItem mi = new MenuItem();
        mi.setNombre(nombre);
        mi.setDescripcion(descripcion);
        mi.setPrecio(precio);
        mi.setImageUrl(imagen);
        mi.setCategoria(categoria);
        return mi;
    }

    // ======================================================
    // 🎟️ Funciones (Showtimes)
    // ======================================================
    private void seedShowtimes() {
        if (showtimes.count() > 0) return;

        List<Movie> all = movies.findAll();
        if (all.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (Movie m : all) {
            Showtime s1 = new Showtime();
            s1.setPelicula(m);
            s1.setFechaHora(now.withHour(18).withMinute(0));
            s1.setSala("Sala 1");
            s1.setBasePrice(18000);
            s1.setFilas(8);
            s1.setColumnas(10);
            showtimes.saveAndFlush(s1);
            seating.initForShowtime(s1.getId());

            Showtime s2 = new Showtime();
            s2.setPelicula(m);
            s2.setFechaHora(now.plusDays(1).withHour(20).withMinute(30));
            s2.setSala("Sala 2");
            s2.setBasePrice(20000);
            s2.setFilas(10);
            s2.setColumnas(12);
            showtimes.saveAndFlush(s2);
            seating.initForShowtime(s2.getId());

            Showtime s3 = new Showtime();
            s3.setPelicula(m);
            s3.setFechaHora(now.plusDays(2).withHour(22).withMinute(0));
            s3.setSala("Sala 3");
            s3.setBasePrice(22000);
            s3.setFilas(12);
            s3.setColumnas(12);
            showtimes.saveAndFlush(s3);
            seating.initForShowtime(s3.getId());
        }

        System.out.println("✅ Funciones iniciales creadas con sus mapas de asientos");
    }
}
