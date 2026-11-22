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
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.rest.dto.SimulacionRequest;
import py.edu.uc.lp3.service.SimulacionService;

import java.net.URI;
import java.util.List;

/**
 * Recurso de la simulación: una "explosión". El POST la provoca (crea el
 * recurso) y los GET muestran lo que quedó guardado. Solo traduce HTTP.
 */
@Tag(name = "Simulación de explosiones",
     description = """
             Cada POST detona un explosivo y aplica las DOS reglas del juego a \
             todas las entidades vivas; el resultado queda persistido y los GET \
             lo muestran. Corre en una transacción: si algo falla, la base queda \
             igual que antes.""")
@RestController
@RequestMapping(ApiPaths.EXPLOSIONES)
public class ExplosionController {

    private final SimulacionService simulacion;

    public ExplosionController(SimulacionService simulacion) {
        this.simulacion = simulacion;
    }

    @Operation(summary = "Provocar una explosión",
               description = """
                       Detona el explosivo indicado por 'tipoExplosivo' \
                       ("creeper" o "tnt") y 'explosivoId'. Aplica la regla 1 \
                       (onda expansiva) a cada entidad alcanzada y la regla 2 \
                       (esquive) a los Endermans. Devuelve un impacto por \
                       entidad, con el daño o los bloques gastados.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "La explosión persistida, con sus impactos"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA: falta explosivoId o el tipo no es creeper ni tnt"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA: ese explosivo no existe"),
            @ApiResponse(responseCode = "409", description = "EXPLOSION_IMPOSIBLE: ya detonó una vez")
    })
    @PostMapping
    public ResponseEntity<Explosion> provocar(@RequestBody SimulacionRequest pedido) {
        Explosion explosion = simulacion.explotar(pedido.tipoExplosivo(), pedido.explosivoId());
        return ResponseEntity.created(
                URI.create(ApiPaths.EXPLOSIONES + "/" + explosion.getId())).body(explosion);
    }

    @Operation(summary = "Historial de explosiones", description = "La más reciente primero.")
    @GetMapping
    public List<Explosion> historial() {
        return simulacion.historial();
    }

    @Operation(summary = "Buscar una explosión por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La explosión con sus impactos"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    @GetMapping("/{id}")
    public Explosion buscar(@PathVariable("id") Long id) {
        return simulacion.buscar(id);
    }
}
