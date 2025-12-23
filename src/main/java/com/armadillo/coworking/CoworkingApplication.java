package com.armadillo.coworking;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoworkingApplication {

    public static void main(String[] args) {
        // Cargar variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(CoworkingApplication.class, args);
        System.out.println("Sistema de Reserva de Salas - Armadillo Coworking");
        System.out.println("API REST ejecutándose en http://localhost:8080");
    }
}
