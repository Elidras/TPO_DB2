package com.uade.tpo.entity;

public class User {
    private String id;
    private String nombre;
    private String mail;
    private String password;
    private Integer edad;
    private String tipoUsuario;

    public User() {}

    public User(String id, String nombre, String mail, String password, Integer edad, String tipoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.mail = mail;
        this.password = password;
        this.edad = edad;
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", mail='" + mail + '\'' +
                ", edad=" + edad +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
