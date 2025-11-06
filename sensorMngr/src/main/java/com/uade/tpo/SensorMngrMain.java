package com.uade.tpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SensorMngrMain {

    public static void main(String[] args) {
        SpringApplication.run(SensorMngrMain.class, args);
        System.out.println("SensorMngr started!");
    }
}