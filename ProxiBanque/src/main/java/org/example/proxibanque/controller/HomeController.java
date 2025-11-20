package org.example.proxibanque.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message", "Bienvenue dans ProxiBanque API",
                "endpoints", "Consultez /conseillers, /clients, /comptes"
        );
    }

    @GetMapping("/api")
    public Map<String, String> apiInfo() {
        return Map.of(
                "conseillers", "GET/POST /conseillers",
                "clients", "GET/POST /clients/conseiller/{id}",
                "comptes", "GET/POST /comptes/courant/client/{id}, /comptes/epargne/client/{id}"
        );
    }
}