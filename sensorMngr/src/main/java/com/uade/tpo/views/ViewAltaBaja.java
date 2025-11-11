package com.uade.tpo.views;

import java.util.Scanner;

import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class ViewAltaBaja {

    private final MongoDBCRUD mongoCRUD;
    private final Scanner scanner;

    public ViewAltaBaja(MongoDBCRUD mongoCRUD) {
        this.mongoCRUD = mongoCRUD;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú de alta/baja.
     * Devuelve "VOLVER" para regresar al menú anterior.
     */
    public String mostrarMenu() {

        String opcion;

        do {
            SpaceAdder.addSpace(9);
            System.out.println("\n===== ALTA / BAJA DE USUARIOS =====");
            System.out.println("1. Dar de ALTA un usuario");
            System.out.println("2. Dar de BAJA un usuario por mail");
            System.out.println("0. Volver al menú anterior");
            System.out.print("Seleccione una opción: ");
            SpaceAdder.addSpace(3);

            opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    altaUsuario();
                    break;

                case "2":
                    bajaUsuario();
                    break;

                case "0":
                    return "VOLVER";

                default:
                    System.out.println("❌ Opción inválida. Intente nuevamente.");
            }

        } while (true);
    }

    private void altaUsuario() {
        System.out.println("\n--- DAR DE ALTA USUARIO ---");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Mail: ");
        String mail = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Edad: ");
        int edad = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer

        System.out.print("Tipo de usuario (admin/user/tecnico): ");
        String tipoUsuario = scanner.nextLine();

        User nuevo = new User(null, nombre, mail, password, edad, tipoUsuario);
        mongoCRUD.darDeAltaUsuario(nuevo);

        System.out.println("✅ El usuario fue creado correctamente.");
    }

    private void bajaUsuario() {
        System.out.println("\n--- DAR DE BAJA USUARIO ---");

        System.out.print("Mail del usuario a eliminar: ");
        String mail = scanner.nextLine();

        System.out.print("¿Confirmar eliminación? (s/n): ");
        String confirmar = scanner.nextLine().toLowerCase();

        if (confirmar.equals("s")) {
            mongoCRUD.darDeBajaUsuarioPorMail(mail);
            System.out.println("✅ Usuario eliminado.");
        } else {
            System.out.println("❌ Operación cancelada.");
        }
    }
}
