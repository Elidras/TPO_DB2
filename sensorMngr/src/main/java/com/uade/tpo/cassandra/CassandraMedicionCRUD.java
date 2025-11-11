package com.uade.tpo.cassandra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.uade.tpo.entity.Medicion;

public class CassandraMedicionCRUD {

    private final CqlSession session;
    
    public CassandraMedicionCRUD(CqlSession session) {
        this.session = session;
    }

    // INSERT — escribe en las 7 tablas
    public void insertMedicion(Medicion m) {

        String[] tablas = new String[]{
                "mediciones_por_sensor_id",
                "mediciones_por_pais",
                "mediciones_por_ciudad",
                "mediciones_por_sensor_name",
                "mediciones_por_estado",
                "mediciones_por_pais_por_temperatura",
                "mediciones_por_pais_por_humedad"
        };

        for (String tabla : tablas) {
            String query = "INSERT INTO " + tabla + " (" +
                    "sensor_id, sensor_name, pais, ciudad, fecha_hora, medicion_id, " +
                    "temperatura, humedad, estado" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            session.execute(session.prepare(query).bind(
                    m.getSensorId(),
                    m.getSensorName(),
                    m.getPais(),
                    m.getCiudad(),
                    m.getFechaHora(),   // java.util.Date expected
                    m.getMedicionId(),
                    m.getTemperatura(),
                    m.getHumedad(),
                    m.getEstado()
            ));
        }
    }

    // SELECT por sensor_id
    public List<Medicion> findBySensorId(UUID sensorId) {
        String query = "SELECT * FROM mediciones_por_sensor_id WHERE sensor_id = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(sensorId));
        return mapRows(rs);
    }

    // SELECT por sensor_name
    public List<Medicion> findBySensorName(String name) {
        String query = "SELECT * FROM mediciones_por_sensor_name WHERE sensor_name = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(name));
        return mapRows(rs);
    }

    // SELECT por país
    public List<Medicion> findByPais(String pais) {
        String query = "SELECT * FROM mediciones_por_pais WHERE pais = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(pais));
        return mapRows(rs);
    }

    // SELECT por ciudad
    public List<Medicion> findByCiudad(String ciudad) {
        String query = "SELECT * FROM mediciones_por_ciudad WHERE ciudad = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(ciudad));
        return mapRows(rs);
    }

    // SELECT por estado
    public List<Medicion> findByEstado(String estado) {
        String query = "SELECT * FROM mediciones_por_estado WHERE estado = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(estado));
        return mapRows(rs);
    }

    // SELECT por país + temperatura
    public List<Medicion> findByPaisAndTemperatura(String pais, int temperatura) {
        String query = "SELECT * FROM mediciones_por_pais_por_temperatura WHERE pais = ? AND temperatura = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(pais, temperatura));
        return mapRows(rs);
    }

    // SELECT por país + humedad
    public List<Medicion> findByPaisAndHumedad(String pais, int humedad) {
        String query = "SELECT * FROM mediciones_por_pais_por_humedad WHERE pais = ? AND humedad = ?";
        ResultSet rs = session.execute(session.prepare(query).bind(pais, humedad));
        return mapRows(rs);
    }

    // MAPEADOR — usa get(column, Class) para evitar dependencias de driver method names
    private List<Medicion> mapRows(ResultSet rs) {
        List<Medicion> lista = new ArrayList<>();
        for (Row row : rs) {

            // read fecha_hora as java.util.Date in a robust way
            Date fechaHora = row.get("fecha_hora", Date.class);

            // read UUIDs and ints; handle possible nulls safely
            UUID sensorId = row.getUuid("sensor_id");
            UUID medicionId = row.getUuid("medicion_id");

            String sensorName = row.getString("sensor_name");
            String pais = row.getString("pais");
            String ciudad = row.getString("ciudad");
            Integer temperatura = row.isNull("temperatura") ? null : row.getInt("temperatura");
            Integer humedad = row.isNull("humedad") ? null : row.getInt("humedad");
            String estado = row.getString("estado");

            Medicion m = new Medicion(
                    sensorId,
                    sensorName,
                    pais,
                    ciudad,
                    fechaHora,
                    medicionId,
                    temperatura,
                    humedad,
                    estado
            );
            lista.add(m);
        }
        return lista;
    }
}
