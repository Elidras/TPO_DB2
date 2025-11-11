package com.uade.tpo.entity;

import java.util.List;
import java.util.UUID;

public class Grupo {

    private UUID id;
    private String nombre;
    private List<String> usuarios; // Lista de IDs o nombres de los usuarios miembros

    // Constructor completo
    public Grupo(UUID id, String nombre, List<String> usuarios) {
        this.id = id;
        this.nombre = nombre;
        this.usuarios = usuarios;
    }

    // Constructor vacío
    public Grupo() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", usuarios=" + usuarios +
                '}';
    }
}
