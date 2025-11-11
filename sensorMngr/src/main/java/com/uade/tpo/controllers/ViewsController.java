package com.uade.tpo.controllers;

import java.util.Scanner;

import org.springframework.stereotype.Controller;

import com.uade.tpo.cassandra.CassandraMedicionCRUD;
import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;
import com.uade.tpo.views.MenuAdmin;
import com.uade.tpo.views.MenuTecnico;
import com.uade.tpo.views.MenuUser;
import com.uade.tpo.views.ViewLogin;

@Controller
public class ViewsController {

    private final MongoDBCRUD mongoDB;
    private final CassandraMedicionCRUD cassandraReader; // read-only service
    private final Scanner scanner;

    public ViewsController(MongoDBCRUD mongoDB, CassandraMedicionCRUD cassandraReader, Scanner scanner) {
        this.mongoDB = mongoDB;
        this.cassandraReader = cassandraReader; // injected but not touched directly by controller
        this.scanner = scanner;
    }

    /**
     * Main entry point of the program
     */
    public void runApplication() {

        while (true) {

            // ✅ Login handled entirely by ViewLogin
            ViewLogin loginView = new ViewLogin(mongoDB, scanner);
            User usuario = loginView.mostrarMenuPrincipal();

            // ✅ If user selected "Salir" -> terminate program
            if (usuario == null) {
                System.out.println("Aplicación cerrada.");
                System.exit(0);
                return;
            }

            // ✅ Enter session loop (menus exit automatically when selecting "Cerrar sesión")
            boolean sessionActive = true;

            while (sessionActive) {

                String tipo = usuario.getTipoUsuario().toLowerCase();

                switch (tipo) {
                    case "admin" -> {
                        MenuAdmin adminView = new MenuAdmin(usuario);
                        adminView.mostrarMenu();
                        sessionActive = false; // returns to login menu after closing session
                    }

                    case "tecnico", "técnico" -> {
                        MenuTecnico tecnicoView = new MenuTecnico(usuario);
                        tecnicoView.mostrarMenu();
                        sessionActive = false;
                    }

                    case "user", "usuario" -> {
                        MenuUser userView = new MenuUser(usuario);
                        userView.mostrarMenu();
                        sessionActive = false;
                    }

                    default -> {
                        System.out.println("Tipo de usuario desconocido. Finalizando sesión...");
                        sessionActive = false;
                    }
                }
            }
        }
    }
}
