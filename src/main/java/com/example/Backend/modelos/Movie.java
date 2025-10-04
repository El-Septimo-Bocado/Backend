package com.example.Backend.modelos;

import java.util.List;
import java.util.UUID;


public class Movie {
    private String id;
    private String titulo;
    private String poster;
    private String fondo;
    private String director;
    private String generos;
    private String duracion;
    private double rating;
    private boolean activo;

    public Movie() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    public Movie(String titulo, String poster, String fondo, String director,
                 String generos, String duracion, double rating, boolean activo) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.poster = poster;
        this.fondo = fondo;
        this.director = director;
        this.generos = generos;
        this.duracion = duracion;
        this.rating = rating;
        this.activo = activo;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getFondo() {
        return fondo;
    }

    public void setFondo(String fondo) {
        this.fondo = fondo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGeneros() {
        return generos;
    }

    public void setGeneros(String generos) {
        this.generos = generos;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
