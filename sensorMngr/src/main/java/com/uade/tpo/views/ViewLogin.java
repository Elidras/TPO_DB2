package com.uade.tpo.views;

import java.util.Optional;
import java.util.Scanner;

import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class ViewLogin {

    private final MongoDBCRUD mongoDBCRUD;
    private final Scanner sc;

    public ViewLogin(MongoDBCRUD mongoDBCRUD, Scanner sc) {
        this.mongoDBCRUD = mongoDBCRUD;
        this.sc = sc;
    }

    public User mostrarMenuPrincipal() {

        while (true) {
            SpaceAdder.addSpace(9);
            System.out.println("\n==== MENÚ PRINCIPAL =====");
            System.out.println("1) Iniciar sesión");
            System.out.println("2) Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = sc.nextLine().trim();
            SpaceAdder.addSpace(3);

            switch (opcion) {
                case "1" -> {
                    User usuario = intentarLogin();
                    if (usuario != null) {
                        System.out.println("\n✅ Bienvenido, " + usuario.getNombre() +
                                " (" + usuario.getMail() + ") — Tipo: " + usuario.getTipoUsuario());
                        return usuario;
                    }
                }

                case "2" -> {
                    System.out.println("Saliendo...");
                    return null;
                }

                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private User intentarLogin() {
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
