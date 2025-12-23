package com.armadillo.coworking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @GetMapping("/")
    public String home() {
        return "Armadillo Coworking - API de Reserva de Salas";
    }

    // TODO: Implementar tus endpoints aquí
}
