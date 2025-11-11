package com.uade.tpo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.uade.tpo.controllers.ViewsController;

@SpringBootApplication
public class SensorMngrMain implements CommandLineRunner {

    private final ViewsController viewsController;

    public SensorMngrMain(ViewsController viewsController) {
        this.viewsController = viewsController;
    }

    public static void main(String[] args) {
        SpringApplication.run(SensorMngrMain.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n\nSensorMngr started!");
        viewsController.runApplication();
    }
}
