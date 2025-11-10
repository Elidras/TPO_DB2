package com.uade.tpo.views;
import java.util.Optional;
import java.util.Scanner;

import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class ViewLogin {

    private final MongoDBCRUD mongoDBCRUD;

    public ViewLogin(MongoDBCRUD mongoDBCRUD) {
        this.mongoDBCRUD = mongoDBCRUD;
    }

    /**
     * Muestra el menú principal y gestiona el login.
     * Si el login es exitoso, devuelve el usuario completo.
     * Si falla, devuelve null.
     */
    public User mostrarMenuPrincipal() {
        Scanner sc = new Scanner(System.in);
        User usuarioEnSesion = null;
        boolean salir = false;

        while (!salir && usuarioEnSesion == null) {
            SpaceAdder.addSpace(9);
            System.out.println("\n==== MENÚ PRINCIPAL =====");
            System.out.println("1) Iniciar sesión");
            System.out.println("2) Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = sc.nextLine().trim();
            SpaceAdder.addSpace(3);

            switch (opcion) {
                case "1":
                    usuarioEnSesion = intentarLogin(sc);
                    break;
                case "2":
                    salir = true;
                    System.out.println("Saliendo...");
                    System.exit(0);
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }

        if (usuarioEnSesion != null) {
            System.out.println("\n✅ Bienvenido, " + usuarioEnSesion.getNombre() +
                    " (" + usuarioEnSesion.getMail() + ") — Tipo: " + usuarioEnSesion.getTipoUsuario());
        }

        return usuarioEnSesion;
    }

    private User intentarLogin(Scanner sc) {
        SpaceAdder.addSpace(9);
        System.out.println("\n===== INICIO DE SESIÓN =====");
        System.out.print("Ingrese su mail: ");
        String mail = sc.nextLine().trim();
        System.out.print("Ingrese su contraseña: ");
        String password = sc.nextLine().trim();

        Optional<User> maybeUser = mongoDBCRUD.buscarUsuarioPorMailYPassword(mail, password);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        } else {
            SpaceAdder.addSpace(3);
            System.out.println("Credenciales incorrectas. Intente nuevamente.");
            return null;
        }
    }
}
