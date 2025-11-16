package com.example.Backend.modelos;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "funcion", indexes = {
        @Index(name = "idx_funcion_pelicula", columnList = "pelicula_id")
})
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "pelicula_id")
    private Movie pelicula;

    @Column(name = "horario", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "precio_base")
    private Integer basePrice;

    @Column(name = "sala")
    private String sala;

    @Column(name = "filas")
    private Integer filas = 10;

    @Column(name = "columnas")
    private Integer columnas = 12;

    @Transient
    private String etiqueta;

    // ===== getters / setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Movie getPelicula() { return pelicula; }
    public void setPelicula(Movie pelicula) { this.pelicula = pelicula; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Integer getBasePrice() { return basePrice; }
    public void setBasePrice(Integer basePrice) { this.basePrice = basePrice; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Integer getFilas() { return filas; }
    public void setFilas(Integer filas) { this.filas = filas; }

    public Integer getColumnas() { return columnas; }
    public void setColumnas(Integer columnas) { this.columnas = columnas; }

    // Etiqueta calculada tipo "Vie 20:30 - Sala 1"
    public String getEtiqueta() {
        if (fechaHora == null) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE HH:mm");
        String base = fechaHora.format(fmt);
        return (sala == null || sala.isBlank()) ? base : (base + " - " + sala);
    }

    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; } // por compatibilidad
}
