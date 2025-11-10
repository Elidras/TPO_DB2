package com.uade.tpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.uade.tpo.mongoDB.MongoDBCRUD;
import com.uade.tpo.views.*;
import com.uade.tpo.entity.User;
import org.springframework.boot.CommandLineRunner;



@SpringBootApplication
public class SensorMngrMain implements CommandLineRunner {

    private final MongoDBCRUD mongoDBCRUD;

    public SensorMngrMain(MongoDBCRUD mongoDBCRUD) {
        this.mongoDBCRUD = mongoDBCRUD;
    }

    public static void main(String[] args) {
        SpringApplication.run(SensorMngrMain.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("SensorMngr started!");
        ViewLogin viewLogin = new ViewLogin(mongoDBCRUD);
        User usuario = viewLogin.mostrarMenuPrincipal();
        if (usuario == null) {
            System.out.println("Aplicación finalizada. No se inició sesión.");
            return; // 👈 salimos sin ejecutar los menús
        }

        if (usuario.getTipoUsuario().equals("admin")) {
            MenuAdmin menuAdmin = new MenuAdmin(usuario);
            menuAdmin.mostrarMenu();
        } else if (usuario.getTipoUsuario().equals("user")) {
            MenuUser menuUser = new MenuUser(usuario);
            menuUser.mostrarMenu();
        }

    }
}
