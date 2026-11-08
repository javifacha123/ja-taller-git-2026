package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.repository.CreeperRepository;
import py.edu.uc.lp3.rest.dto.ExplosivoResumen;

import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint del protocolo de dominio: junta Creepers y TNT en una sola lista de
 * tipo Explosivo y les habla solo con los mensajes de la interfaz.
 */
@RestController
public class ExplosivoController {

    private final CreeperRepository creepers;
    private final BloqueTntRepository tnts;

    public ExplosivoController(CreeperRepository creepers, BloqueTntRepository tnts) {
        this.creepers = creepers;
        this.tnts = tnts;
    }

    @GetMapping(ApiPaths.EXPLOSIVOS)
    public List<ExplosivoResumen> listar(
            @RequestParam(name = "disponibles", defaultValue = "false") boolean soloDisponibles) {
        List<Explosivo> explosivos = new ArrayList<>();
        explosivos.addAll(creepers.findAll());
        explosivos.addAll(tnts.findAll());
        return explosivos.stream()
                .filter(e -> !soloDisponibles || e.puedeExplotar())
                .map(ExplosivoResumen::de)
                .toList();
    }
}
