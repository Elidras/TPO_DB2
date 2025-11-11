package com.uade.tpo.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uade.tpo.entity.ControlFuncionamiento;
import com.uade.tpo.entity.Sensor;

import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import com.uade.tpo.mongoDB.MongoDBCRUD;

public class SensorService {

    private final MongoDBCRUD mongoCRUD;

    public SensorService(MongoDBCRUD mongoCRUD) {
        this.mongoCRUD = mongoCRUD;
    }

    /**
     * @param sensor 
     */
    public void cambiarEstadoSensor(Sensor sensor) {
        Scanner scanner = new Scanner(System.in);

        // Guardar el estado anterior
        String estadoAnterior = sensor.getEstadoSensor();

        // Cambiar el estado del sensor
        String nuevoEstado;
        switch (estadoAnterior.toLowerCase()) {
            case "activo":
                nuevoEstado = "inactivo";
                break;
            case "inactivo":
                nuevoEstado = "activo";
                break;
            default:
                nuevoEstado = estadoAnterior; // no cambia si es "falla" u otro
        }

        sensor.setEstadoSensor(nuevoEstado);

        // Pedir la observación al usuario
        System.out.println("Ingrese la observación para este cambio de estado:");
        String observacion = scanner.nextLine();

        // Crear registro de control
        ControlFuncionamiento control = new ControlFuncionamiento(
                UUID.randomUUID(),
                sensor.getId(),
                new Date(),
                nuevoEstado,
                observacion
        );

        MongoDatabase mongo = mongoCRUD.getMongoDatabase();

        // Guardar en la colección "control"
        MongoCollection<ControlFuncionamiento> collection = mongo.getCollection(
                "control", ControlFuncionamiento.class
        );
        collection.insertOne(control);

        System.out.println("✅ Sensor " + sensor.getNombre() + " cambiado a estado: " + nuevoEstado);
        System.out.println("✅ Registro de control guardado en MongoDB: " + control);
    }
}
