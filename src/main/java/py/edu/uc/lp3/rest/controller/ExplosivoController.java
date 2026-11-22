package py.edu.uc.lp3.rest.controller;

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
@RestController
public class ExplosivoController {

    private final ExplosivoService servicio;

    public ExplosivoController(ExplosivoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping(ApiPaths.EXPLOSIVOS)
    public List<ExplosivoResumen> listar(
            @RequestParam(name = "disponibles", defaultValue = "false") boolean soloDisponibles) {
        return servicio.listar(soloDisponibles).stream()
                .map(ExplosivoResumen::de)
                .toList();
    }
}
