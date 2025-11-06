package com.uade.tpo.service;

import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.uade.tpo.dto.MedicionDTO;

@Service
public class MedicionService {

    private final CqlSession session;

    public MedicionService(CqlSession session) {
        this.session = session;
    }

    public void saveMedicion(MedicionDTO dto) {
        executeInsert("mediciones_por_sensor_id", dto);
        executeInsert("mediciones_por_pais", dto);
        executeInsert("mediciones_por_ciudad", dto);
        executeInsert("mediciones_por_sensor_name", dto);
        executeInsert("mediciones_por_estado", dto);
        executeInsert("mediciones_por_pais_por_temperatura", dto);
        executeInsert("mediciones_por_pais_por_humedad", dto);
    }

    private void executeInsert(String tableName, MedicionDTO dto) {
        Insert insert = QueryBuilder.insertInto(tableName)
                .value("sensor_id", QueryBuilder.literal(dto.getSensorId()))
                .value("sensor_name", QueryBuilder.literal(dto.getSensorName()))
                .value("pais", QueryBuilder.literal(dto.getPais()))
                .value("ciudad", QueryBuilder.literal(dto.getCiudad()))
                .value("fecha_hora", QueryBuilder.literal(dto.getFechaHora()))
                .value("medicion_id", QueryBuilder.literal(dto.getMedicionId()))
                .value("temperatura", QueryBuilder.literal(dto.getTemperatura()))
                .value("humedad", QueryBuilder.literal(dto.getHumedad()))
                .value("estado", QueryBuilder.literal(dto.getEstado()));

        session.execute(insert.build());
    }
}
