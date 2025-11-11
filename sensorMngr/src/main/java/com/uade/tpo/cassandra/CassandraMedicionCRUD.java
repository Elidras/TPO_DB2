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

    private static CassandraMedicionCRUD instance;

    private final CqlSession session;

    private CassandraMedicionCRUD(CqlSession session) {
        this.session = session;
    }

    public static synchronized CassandraMedicionCRUD getInstance(CqlSession session) {
        if (instance == null) {
            instance = new CassandraMedicionCRUD(session);
        }
        return instance;
    }

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
                    m.getFechaHora(),
                    m.getMedicionId(),
                    m.getTemperatura(),
                    m.getHumedad(),
                    m.getEstado()
            ));
        }
    }

    public List<Medicion> findBySensorId(UUID sensorId) {
        ResultSet rs = session.execute(
                session.prepare("SELECT * FROM mediciones_por_sensor_id WHERE sensor_id = ?")
                        .bind(sensorId));
        return mapRows(rs);
    }

    public List<Medicion> findBySensorName(String name) {
        ResultSet rs = session.execute(
                session.prepare("SELECT * FROM mediciones_por_sensor_name WHERE sensor_name = ?")
                        .bind(name));
        return mapRows(rs);
    }

    public List<Medicion> findByPais(String pais) {
        ResultSet rs = session.execute(
                session.prepare("SELECT * FROM mediciones_por_pais WHERE pais = ?")
                        .bind(pais));
        return mapRows(rs);
    }

    public List<Medicion> findByCiudad(String ciudad) {
        ResultSet rs = session.execute(
                session.prepare("SELECT * FROM mediciones_por_ciudad WHERE ciudad = ?")
                        .bind(ciudad));
        return mapRows(rs);
    }

    public List<Medicion> findByEstado(String estado) {
        ResultSet rs = session.execute(
                session.prepare("SELECT * FROM mediciones_por_estado WHERE estado = ?")
                        .bind(estado));
        return mapRows(rs);
    }

    public List<Medicion> findByPaisAndTemperatura(String pais, int temperatura) {
        ResultSet rs = session.execute(
                session.prepare(
                        "SELECT * FROM mediciones_por_pais_por_temperatura WHERE pais = ? AND temperatura = ?")
                        .bind(pais, temperatura));
        return mapRows(rs);
    }

    public List<Medicion> findByPaisAndHumedad(String pais, int humedad) {
        ResultSet rs = session.execute(
                session.prepare(
                        "SELECT * FROM mediciones_por_pais_por_humedad WHERE pais = ? AND humedad = ?")
                        .bind(pais, humedad));
        return mapRows(rs);
    }

    private List<Medicion> mapRows(ResultSet rs) {
        List<Medicion> lista = new ArrayList<>();
        for (Row row : rs) {
            Date fechaHora = row.get("fecha_hora", Date.class);
            UUID sensorId = row.getUuid("sensor_id");
            UUID medicionId = row.getUuid("medicion_id");

            String sensorName = row.getString("sensor_name");
            String pais = row.getString("pais");
            String ciudad = row.getString("ciudad");
            Integer temperatura = row.isNull("temperatura") ? null : row.getInt("temperatura");
            Integer humedad = row.isNull("humedad") ? null : row.getInt("humedad");
            String estado = row.getString("estado");

            lista.add(new Medicion(
                    sensorId, sensorName, pais, ciudad,
                    fechaHora, medicionId, temperatura, humedad, estado
            ));
        }
        return lista;
    }
    public Object rawQuery(String cql) {
        return session.execute(cql);
    }
}
