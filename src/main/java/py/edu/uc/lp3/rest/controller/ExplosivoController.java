package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.rest.dto.ExplosivoResumen;
import py.edu.uc.lp3.service.ExplosivoService;

import java.util.List;

/**
 * Endpoint del protocolo de dominio: devuelve Creepers y TNT en una sola lista
 * de tipo Explosivo. Solo traduce HTTP y delega en el servicio.
 */
@Tag(name = "Interface de dominio Explosivo",
     description = """
             Lectura polimórfica: devuelve Creepers y bloques de TNT en una sola \
             lista, hablándoles solo por los mensajes de la interface Explosivo. \
             Son dos familias distintas y el endpoint no pregunta de qué tipo es \
             cada uno.""")
@RestController
public class ExplosivoController {

    private final ExplosivoService servicio;

    public ExplosivoController(ExplosivoService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar los explosivos del mundo",
               description = "Con ?disponibles=true deja fuera los que ya detonaron.")
    @GetMapping(ApiPaths.EXPLOSIVOS)
    public List<ExplosivoResumen> listar(
            @RequestParam(name = "disponibles", defaultValue = "false") boolean soloDisponibles) {
        return servicio.listar(soloDisponibles).stream()
                .map(ExplosivoResumen::de)
                .toList();
    }
}
