package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bloques de TNT",
     description = "La segunda familia que implementa la interface Explosivo, con su propia tabla.")
@RestController
@RequestMapping(ApiPaths.TNT)
public class TntController {

    private final TntService servicio;

    public TntController(TntService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Colocar un bloque de TNT", description = "Potencia 16 y radio 4.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colocada, con su id"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA: faltan x o z")
    })
    @PostMapping
    public ResponseEntity<BloqueTnt> colocar(@RequestBody TntRequest pedido) {
        BloqueTnt guardada = servicio.colocar(pedido);
        return ResponseEntity.created(URI.create(ApiPaths.TNT + "/" + guardada.getId())).body(guardada);
    }

    @Operation(summary = "Listar los bloques de TNT")
    @GetMapping
    public List<BloqueTnt> listar() {
        return servicio.listar();
    }

    @Operation(summary = "Buscar una TNT por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La TNT"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    @GetMapping("/{id}")
    public BloqueTnt buscar(@PathVariable("id") Long id) {
        return servicio.buscar(id);
    }
}
