package co.plany.plany.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.plany.plany.model.Prioridad;
import co.plany.plany.repository.PrioridadRepository;

@RestController
@RequestMapping("/api/prioridades")
public class PrioridadController {

    @Autowired
    private PrioridadRepository prioridadRepository;

    @GetMapping
    public ResponseEntity<List<Prioridad>> getAllPrioridades() {
        List<Prioridad> prioridades = prioridadRepository.findAll();
        return ResponseEntity.ok(prioridades);
    }
} 