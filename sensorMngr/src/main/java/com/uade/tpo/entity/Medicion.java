package com.uade.tpo.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;
import java.time.Instant;

@Table("mediciones_por_sensor_id")
public class Medicion {

    @PrimaryKey
    private UUID medicionId;

    private UUID sensorId;
    private String sensorName;
    private String pais;
    private String ciudad;
    private Instant fechaHora;
    private int temperatura;
    private int humedad;
    private String estado;

    // Constructors
    public Medicion() {}

    public Medicion(UUID medicionId, UUID sensorId, String sensorName, String pais, String ciudad,
                    Instant fechaHora, int temperatura, int humedad, String estado) {
        this.medicionId = medicionId;
        this.sensorId = sensorId;
        this.sensorName = sensorName;
        this.pais = pais;
        this.ciudad = ciudad;
        this.fechaHora = fechaHora;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.estado = estado;
    }

    // Getters and setters...
    public UUID getMedicionId() { return medicionId; }
    public void setMedicionId(UUID medicionId) { this.medicionId = medicionId; }
    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }
    public String getSensorName() { return sensorName; }
    public void setSensorName(String sensorName) { this.sensorName = sensorName; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public Instant getFechaHora() { return fechaHora; }
    public void setFechaHora(Instant fechaHora) { this.fechaHora = fechaHora; }
    public int getTemperatura() { return temperatura; }
    public void setTemperatura(int temperatura) { this.temperatura = temperatura; }
    public int getHumedad() { return humedad; }
    public void setHumedad(int humedad) { this.humedad = humedad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
