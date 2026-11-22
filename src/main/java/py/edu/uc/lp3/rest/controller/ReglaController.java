package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.rest.dto.EsquiveEndermanRequest;
import py.edu.uc.lp3.rest.dto.OndaExpansivaRequest;
import py.edu.uc.lp3.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.service.ReglaService;

/**
 * POO-09: publica mis DOS reglas del juego para que las consulte otro servicio.
 *
 * <p>Son los endpoints que un compañero invoca cuando yo soy su A2 o su A3. No
 * tocan la base: reciben números, aplican la regla y responden 200 si la jugada
 * es legal o 400 {@code REGLA_RECHAZADA} si no.</p>
 */
@RestController
@Tag(name = "Reglas del juego",
     description = """
             Mis dos reglas, publicadas para que otro servicio las consulte. \
             Responden 200 cuando aceptan la jugada y 400 con el código \
             REGLA_RECHAZADA cuando la rechazan. No modifican la base.""")
public class ReglaController {

    private final ReglaService servicio;

    public ReglaController(ReglaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping(ApiPaths.REGLA_ONDA)
    @Operation(summary = "REGLA 1 - Onda expansiva",
               description = """
                       Dice cuánto daño corresponde a una entidad que está a \
                       'distancia' bloques de un explosivo de esa potencia y ese \
                       radio, con la fórmula \
                       ceil(potencia * (radio + 1 - d) / (radio + 1)).

                       Si se manda 'danioDeclarado', la regla valida ese número \
                       en vez de solo informarlo.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La jugada cumple la regla 1"),
            @ApiResponse(responseCode = "400",
                    description = """
                            REGLA_RECHAZADA si la entidad está fuera del radio o \
                            el daño declarado no coincide con la fórmula; \
                            ENTIDAD_INVALIDA si falta un campo o es negativo""")
    })
    public VeredictoRegla ondaExpansiva(@RequestBody OndaExpansivaRequest pedido) {
        return servicio.evaluarOndaExpansiva(pedido);
    }

    @PostMapping(ApiPaths.REGLA_ESQUIVE)
    @Operation(summary = "REGLA 2 - Esquive del Enderman",
               description = """
                       Dice si un Enderman alcanzado por la onda logra salir del \
                       radio. Necesita ceil(radio + 1 - d) bloques de \
                       teletransporte: si los tiene, los gasta, reaparece justo \
                       afuera del radio y no recibe daño.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "El Enderman puede esquivar"),
            @ApiResponse(responseCode = "400",
                    description = """
                            REGLA_RECHAZADA si está fuera del radio (no hay nada \
                            que esquivar) o si no le alcanzan los bloques; \
                            ENTIDAD_INVALIDA si falta un campo o es negativo""")
    })
    public VeredictoRegla esquiveEnderman(@RequestBody EsquiveEndermanRequest pedido) {
        return servicio.evaluarEsquiveEnderman(pedido);
    }
}
