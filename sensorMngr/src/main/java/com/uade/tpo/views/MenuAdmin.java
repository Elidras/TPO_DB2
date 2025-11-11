package com.uade.tpo.views;

import java.util.Scanner;

import com.uade.tpo.entity.User;

public class MenuAdmin {

    private final User usuario;
    private final Scanner scanner = new Scanner(System.in);

    public MenuAdmin(User usuario) {
        this.usuario = usuario;
    }

    public void mostrarMenu() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== MENÚ ADMINISTRADOR =====");
            System.out.println("1. Cambiar datos de mi cuenta");
            System.out.println("2. Solicitar mediciones de sensores");
            System.out.println("3. Consultar estado de deuda");
            System.out.println("4. Agregar o dar de baja cuentas de usuarios");
            System.out.println("5. Cerrar sesión");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> cambiarDatosCuenta();
                case "2" -> solicitarMediciones();
                case "3" -> consultarDeuda();
                case "4" -> gestionarUsuarios();
                case "5" -> {
                    System.out.println("Cerrando sesión...");
                    salir = true;   // ✅ return to controller
                }
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        }
    }

    private void cambiarDatosCuenta() {
        System.out.println(">> Cambiando datos de la cuenta de " + usuario.getNombre());
    }

    private void solicitarMediciones() {
        System.out.println(">> Solicitando mediciones de sensores...");
    }

    private void consultarDeuda() {
        System.out.println(">> Consultando estado de deuda...");
    }

    private void gestionarUsuarios() {
        System.out.println(">> Agregando o dando de baja usuarios...");
    }
}
