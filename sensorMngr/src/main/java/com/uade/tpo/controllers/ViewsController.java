package com.uade.tpo.controllers;

import org.springframework.stereotype.Controller;

import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;
import com.uade.tpo.views.MenuAdmin;
import com.uade.tpo.views.MenuUser;
import com.uade.tpo.views.ViewLogin;

@Controller
public class ViewsController {

    private final MongoDBCRUD mongoDBCRUD;

    public ViewsController(MongoDBCRUD mongoDBCRUD) {
        this.mongoDBCRUD = mongoDBCRUD;
    }

    public void runApplication() {

        boolean exit = false;

        while (!exit) {

            ViewLogin viewLogin = new ViewLogin(mongoDBCRUD);
            User usuario = viewLogin.mostrarMenuPrincipal();

            if (usuario == null) {
                System.out.println("Aplicación cerrada.");
                return;
            }

            String tipo = usuario.getTipoUsuario();

            if (tipo.equalsIgnoreCase("admin")) {
                MenuAdmin menuAdmin = new MenuAdmin(usuario);
                menuAdmin.mostrarMenu();
            } 
            else if (tipo.equalsIgnoreCase("user")) {
                MenuUser menuUser = new MenuUser(usuario);
                menuUser.mostrarMenu();
            } else if (tipo.equalsIgnoreCase("tecnico")) {
                MenuUser menuUser = new MenuUser(usuario);
                menuUser.mostrarMenu();
            } else {
                System.out.println("Tipo de usuario desconocido. Saliendo.");
                return;
            }
        }
    }
}
