package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.rest.dto.EntidadPatch;
import py.edu.uc.lp3.rest.dto.EntidadRequest;
import py.edu.uc.lp3.service.EntidadService;

import java.net.URI;
import java.util.List;

/**
 * CRUD REST de la familia EntidadMinecraft.
 *
 * <p>El controller solo traduce HTTP: lee el pedido, llama al servicio y arma
 * la respuesta. No valida, no decide reglas y no conoce los repositorios.</p>
 */
@Tag(name = "CRUD de entidades",
     description = """
             Alta, baja, consulta y modificación de la familia EntidadMinecraft \
             (Creeper, Aldeano, Enderman). Las tres se guardan en una sola tabla \
             con la columna discriminadora tipo_entidad.""")
@RestController
@RequestMapping(ApiPaths.ENTIDADES)
public class EntidadController {

    private final EntidadService servicio;

    public EntidadController(EntidadService servicio) {
        this.servicio = servicio;
    }

    /** Crear: 201 con el recurso (y su id) y la cabecera Location. */
    @Operation(summary = "Crear una entidad",
               description = """
                       El campo 'tipo' decide el subtipo: creeper, aldeano o \
                       enderman. 'vida', 'x', 'z' (y 'cargado' o \
                       'bloquesTeletransporte') son opcionales y eligen el \
                       constructor sobrecargado que se usa.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creada, con su id y la cabecera Location"),
            @ApiResponse(responseCode = "400",
                    description = "ENTIDAD_INVALIDA: nombre vacío, vida <= 0 o bloques negativos")
    })
    @PostMapping
    public ResponseEntity<EntidadMinecraft> crear(@RequestBody EntidadRequest pedido) {
        EntidadMinecraft guardada = servicio.crear(pedido);
        URI ubicacion = URI.create(ApiPaths.ENTIDADES + "/" + guardada.getId());
        return ResponseEntity.created(ubicacion).body(guardada);
    }

    /** Leer varios, con filtros opcionales ?nombre= y ?soloVivas=true. */
    @Operation(summary = "Listar entidades",
               description = "Filtros opcionales: ?nombre= busca por parte del nombre y ?soloVivas=true deja fuera las muertas.")
    @GetMapping
    public List<EntidadMinecraft> listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "soloVivas", defaultValue = "false") boolean soloVivas) {
        return servicio.listar(nombre, soloVivas);
    }

    /** Leer uno: 200 o 404. */
    @Operation(summary = "Buscar una entidad por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La entidad"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    @GetMapping("/{id}")
    public EntidadMinecraft buscar(@PathVariable("id") Long id) {
        return servicio.buscar(id);
    }

    /** Actualizar parcial: solo cambia los campos que vienen en el JSON. */
    @Operation(summary = "Modificar una entidad",
               description = """
                       Cambia solo los campos que vienen. La vida NO se puede \
                       editar: cambia únicamente por las reglas del juego, así \
                       que mandarla responde 400.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La entidad modificada"),
            @ApiResponse(responseCode = "400",
                    description = "ENTIDAD_INVALIDA: se mandó vida, se quiso mover una entidad muerta o bloques negativos"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    @PatchMapping("/{id}")
    public EntidadMinecraft actualizar(@PathVariable("id") Long id, @RequestBody EntidadPatch cambios) {
        return servicio.actualizar(id, cambios);
    }

    /** Borrar: 204 sin cuerpo; después, el GET da 404. */
    @Operation(summary = "Borrar una entidad", description = "La entidad hace despawn y sale de la base.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Borrada, sin cuerpo"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        servicio.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
