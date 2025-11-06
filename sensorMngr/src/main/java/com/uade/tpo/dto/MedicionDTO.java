package com.uade.tpo.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class MedicionDTO {
    private UUID sensorId;
    private String sensorName;
    private String pais;
    private String ciudad;
    private Instant fechaHora;
    private UUID medicionId;
    private int temperatura;
    private int humedad;
    private String estado;
}
