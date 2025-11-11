package com.uade.tpo.views;

import java.util.Scanner;
import java.util.UUID;

import com.uade.tpo.entity.Sensor;
import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;
import com.uade.tpo.service.SensorService;

public class MenuTecnico {

    private final User usuario;
    private final Scanner scanner = new Scanner(System.in);
    private final MongoDBCRUD mongoCRUD;   // ✅ agregado
    private final SensorService sensorService;


    public MenuTecnico(User usuario, MongoDBCRUD mongoCRUD) {
        this.usuario = usuario;
        this.mongoCRUD = mongoCRUD;        // ✅ inicializado
        this.sensorService = new SensorService(mongoCRUD);

    }

    public void mostrarMenu() {
        boolean salir = false;

        while (!salir) {
            SpaceAdder.addSpace(9);
            System.out.println("\n===== MENÚ USUARIO =====");
            System.out.println("1. Cambiar datos de mi cuenta");
            System.out.println("2. Solicitar mediciones de sensores");
            System.out.println("3. Consultar casilla de mensajes");
            System.out.println("4. Cambiar estado sensor");
            System.out.println("5. Cerrar sesión");
            System.out.print("Seleccione una opción: ");
            SpaceAdder.addSpace(3);

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> cambiarDatosCuenta();
                case "2" -> crearInformeMediciones();
                case "3" -> consultarCasillaMensajes();
                case "4" -> cambiarEstadoSensor();
                case "5" -> {
                    System.out.println("Cerrando sesión...");
                    salir = true;
                }
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        }
    }

    private void cambiarDatosCuenta() {
        System.out.println(">> Cambiando datos de la cuenta de " + usuario.getNombre());
        mongoCRUD.modificarAtributoUsuario(usuario); 
    }

    private void crearInformeMediciones() {
        System.out.println(">> Abriendo plataforma de creacion de mediciones...");
    }

    private void consultarCasillaMensajes(){
        System.out.println(">> Abriendo casilla de mensajes...");
    }

    private void cambiarEstadoSensor(){
        System.out.println(">> Abriendo menu de cambio...");

        System.out.print("Ingrese el ID del sensor a modificar: ");
        String sensorIdInput = scanner.nextLine();
        UUID sensorId = UUID.fromString(sensorIdInput);

        Sensor sensor = mongoCRUD.findSensorById(sensorId);

        if (sensor == null) {
            System.out.println("❌ Sensor no encontrado.");
            return;
        }

        sensorService.cambiarEstadoSensor(sensor);
    }
}
