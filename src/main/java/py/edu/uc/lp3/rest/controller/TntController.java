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
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.rest.dto.TntRequest;

import java.net.URI;
import java.util.List;

/** Colocar y consultar bloques de TNT (la segunda familia que implementa Explosivo). */
@RestController
@RequestMapping(ApiPaths.TNT)
public class TntController {

    private final BloqueTntRepository repositorio;

    public TntController(BloqueTntRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping
    public ResponseEntity<BloqueTnt> colocar(@RequestBody TntRequest pedido) {
        BloqueTnt guardada = repositorio.save(pedido.crear());
        return ResponseEntity.created(URI.create(ApiPaths.TNT + "/" + guardada.getId())).body(guardada);
    }

    @GetMapping
    public List<BloqueTnt> listar() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public BloqueTnt buscar(@PathVariable("id") Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("una TNT", id));
    }
}
