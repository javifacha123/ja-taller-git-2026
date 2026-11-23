package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.service.EscenarioService;

import java.net.URI;
import java.util.List;

/**
 * POO-09: el escenario propio.
 *
 * <p>Un POST propone una jugada, el servicio la somete a las reglas de A2 y de
 * A3, y la jugada queda guardada solo si las dos la aceptan. Solo traduce
 * HTTP.</p>
 */
@RestController
@RequestMapping(ApiPaths.ESCENARIOS)
@Tag(name = "Escenario cruzado (POO-09)",
     description = """
             Propone una jugada sobre entidades de esta base y la somete a las \
             reglas de DOS compañeros. Se persiste únicamente si A2 y A3 la \
             aceptan; si alguno rechaza, la base queda igual y la respuesta \
             dice de quién fue el rechazo.""")
public class EscenarioController {

    private final EscenarioService servicio;

    public EscenarioController(EscenarioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @Operation(summary = "Jugar un escenario cruzado",
               description = """
                       El Creeper indicado detona; la onda alcanza al Aldeano y \
                       al Enderman indicados. Se calcula con mis reglas cuánto \
                       daño recibe el Aldeano y cuántos bloques necesita el \
                       Enderman, y esos dos hechos se someten a las reglas de A2 \
                       y de A3 respectivamente.

                       Las tres entidades tienen que existir ya en esta base.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Las dos APIs aceptaron: la jugada quedó guardada"),
            @ApiResponse(responseCode = "400",
                    description = "ENTIDAD_INVALIDA: falta un id o el tipo no es el que el escenario espera"),
            @ApiResponse(responseCode = "404",
                    description = "ENTIDAD_NO_ENCONTRADA: alguna de las tres entidades no existe"),
            @ApiResponse(responseCode = "409",
                    description = "COMPANERO_RECHAZO: A2 o A3 rechazaron; la base quedó igual y el JSON dice cuál"),
            @ApiResponse(responseCode = "502",
                    description = "COMPANERO_INACCESIBLE: la API de A2 o de A3 no respondió")
    })
    public ResponseEntity<Jugada> jugar(@RequestBody EscenarioRequest pedido) {
        Jugada jugada = servicio.jugar(pedido);
        return ResponseEntity
                .created(URI.create(ApiPaths.ESCENARIOS + "/" + jugada.getId()))
                .body(jugada);
    }

    @GetMapping
    @Operation(summary = "Historial de jugadas aceptadas",
               description = "Solo aparecen las jugadas que A2 y A3 aceptaron. La más reciente primero.")
    public List<Jugada> historial() {
        return servicio.historial();
    }
}
