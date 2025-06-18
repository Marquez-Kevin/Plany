
package co.plany.plany.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.plany.plany.model.Tipo;
import co.plany.plany.service.TipoService;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {

    private final TipoService tipoService;

    @Autowired
    public TipoController(TipoService tipoService) {
        this.tipoService = tipoService;
    }

    @GetMapping
    public ResponseEntity<List<Tipo>> getAllTipos() {
        return ResponseEntity.ok(tipoService.findAll());
    }
}