package com.uade.tpo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CassandraInitMediciones {

    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder().build()) {

            // Sensors: {name, type, city, country, estado}
            String[][] sensors = {
                    {"Sensor-A1", "temperatura", "Buenos Aires", "Argentina", "activo"},
                    {"Sensor-A2", "humedad", "Córdoba", "Argentina", "activo"},
                    {"Sensor-A3", "temperatura", "Mendoza", "Argentina", "inactivo"},
                    {"Sensor-A4", "humedad", "Mar del Plata", "Argentina", "activo"},
                    {"Sensor-A5", "temperatura", "Tucumán", "Argentina", "falla"},
                    {"Sensor-A6", "humedad", "Salta", "Argentina", "activo"},
                    {"Sensor-A7", "temperatura", "Corrientes", "Argentina", "activo"},
                    {"Sensor-A8", "humedad", "Catamarca", "Argentina", "inactivo"},
                    {"Sensor-A9", "temperatura", "La Rioja", "Argentina", "activo"},
                    {"Sensor-A10", "humedad", "Viedma", "Argentina", "activo"},
                    {"Sensor-A11", "temperatura", "Río Gallegos", "Argentina", "falla"},
                    {"Sensor-A12", "humedad", "Ushuaia", "Argentina", "activo"},
                    {"Sensor-A13", "temperatura", "La Plata", "Argentina", "inactivo"},
                    {"Sensor-A14", "humedad", "Neuquén", "Argentina", "activo"},
                    {"Sensor-A15", "temperatura", "Posadas", "Argentina", "activo"},
                    {"Sensor-A16", "humedad", "Concepción del Uruguay", "Argentina", "falla"},
                    {"Sensor-A17", "temperatura", "Rosario", "Argentina", "activo"},
                    {"Sensor-A18", "humedad", "Concordia", "Argentina", "activo"},
                    {"Sensor-A19", "temperatura", "San Isidro", "Argentina", "activo"},
                    {"Sensor-A20", "humedad", "Lanús", "Argentina", "inactivo"}
            };

            int measurementsPerSensor = 60;

            for (String[] sensor : sensors) {
                String nombre = sensor[0];
                String tipo = sensor[1];
                String ciudad = sensor[2];
                String pais = sensor[3];
                String estado = sensor[4];

                for (int i = 0; i < measurementsPerSensor; i++) {
                    UUID sensorId = UUID.randomUUID();
                    UUID medicionId = UUID.randomUUID();

                    // Generate timestamp: now minus i hours for variety
                    Instant fechaHora = Instant.now().minus(i * 1, ChronoUnit.HOURS);

                    int temperatura = tipo.equals("temperatura") ? 20 + (i % 10) : 0;
                    int humedad = tipo.equals("humedad") ? 40 + (i % 20) : 0;

                    String cql = "INSERT INTO mediciones.medicion " +
                            "(sensor_id, sensor_name, pais, ciudad, fecha_hora, medicion_id, temperatura, humedad, estado) " +
                            "VALUES (" +
                            sensorId + ", '" +
                            nombre + "', '" +
                            pais + "', '" +
                            ciudad + "', '" +
                            fechaHora + "', " +
                            medicionId + ", " +
                            temperatura + ", " +
                            humedad + ", '" +
                            estado + "'" +
                            ");";

                    session.execute(cql);
                }

                System.out.println("✅ Inserted " + measurementsPerSensor + " measurements for " + nombre);
            }

            System.out.println("✅ Total 1200 measurements inserted successfully.");
        }
    }
}