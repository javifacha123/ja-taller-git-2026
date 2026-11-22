package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.BloqueTnt;
import py.edu.uc.lp3.rest.dto.TntRequest;
import py.edu.uc.lp3.service.TntService;

import java.net.URI;
import java.util.List;

/**
 * Colocar y consultar bloques de TNT (la segunda familia que implementa
 * Explosivo). Solo traduce HTTP y delega en el servicio.
 */
@RestController
@RequestMapping(ApiPaths.TNT)
public class TntController {

    private final TntService servicio;

    public TntController(TntService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<BloqueTnt> colocar(@RequestBody TntRequest pedido) {
        BloqueTnt guardada = servicio.colocar(pedido);
        return ResponseEntity.created(URI.create(ApiPaths.TNT + "/" + guardada.getId())).body(guardada);
    }

    @GetMapping
    public List<BloqueTnt> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public BloqueTnt buscar(@PathVariable("id") Long id) {
        return servicio.buscar(id);
    }
}
