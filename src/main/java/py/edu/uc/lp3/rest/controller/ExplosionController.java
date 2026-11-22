package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.rest.dto.SimulacionRequest;
import py.edu.uc.lp3.service.SimulacionService;

import java.net.URI;
import java.util.List;

/**
 * Recurso de la simulación: una "explosión". El POST la provoca (crea el
 * recurso) y los GET muestran lo que quedó guardado. Solo traduce HTTP.
 */
@RestController
@RequestMapping(ApiPaths.EXPLOSIONES)
public class ExplosionController {

    private final SimulacionService simulacion;

    public ExplosionController(SimulacionService simulacion) {
        this.simulacion = simulacion;
    }

    @PostMapping
    public ResponseEntity<Explosion> provocar(@RequestBody SimulacionRequest pedido) {
        Explosion explosion = simulacion.explotar(pedido.tipoExplosivo(), pedido.explosivoId());
        return ResponseEntity.created(
                URI.create(ApiPaths.EXPLOSIONES + "/" + explosion.getId())).body(explosion);
    }

    @GetMapping
    public List<Explosion> historial() {
        return simulacion.historial();
    }

    @GetMapping("/{id}")
    public Explosion buscar(@PathVariable("id") Long id) {
        return simulacion.buscar(id);
    }
}
