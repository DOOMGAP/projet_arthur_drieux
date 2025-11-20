package org.example.proxibanque.controller;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.Conseiller;
import org.example.proxibanque.service.ConseillerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conseillers")
public class ConseillerController {

    private final ConseillerService service;

    @PostMapping
    public Conseiller create(@RequestBody Conseiller conseiller) {
        return service.create(conseiller);
    }

    @GetMapping
    public List<Conseiller> getAll() {
        return service.findAll();
    }
}

