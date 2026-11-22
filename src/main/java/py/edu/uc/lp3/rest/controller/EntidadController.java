package py.edu.uc.lp3.rest.controller;

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
@RestController
@RequestMapping(ApiPaths.ENTIDADES)
public class EntidadController {

    private final EntidadService servicio;

    public EntidadController(EntidadService servicio) {
        this.servicio = servicio;
    }

    /** Crear: 201 con el recurso (y su id) y la cabecera Location. */
    @PostMapping
    public ResponseEntity<EntidadMinecraft> crear(@RequestBody EntidadRequest pedido) {
        EntidadMinecraft guardada = servicio.crear(pedido);
        URI ubicacion = URI.create(ApiPaths.ENTIDADES + "/" + guardada.getId());
        return ResponseEntity.created(ubicacion).body(guardada);
    }

    /** Leer varios, con filtros opcionales ?nombre= y ?soloVivas=true. */
    @GetMapping
    public List<EntidadMinecraft> listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "soloVivas", defaultValue = "false") boolean soloVivas) {
        return servicio.listar(nombre, soloVivas);
    }

    /** Leer uno: 200 o 404. */
    @GetMapping("/{id}")
    public EntidadMinecraft buscar(@PathVariable("id") Long id) {
        return servicio.buscar(id);
    }

    /** Actualizar parcial: solo cambia los campos que vienen en el JSON. */
    @PatchMapping("/{id}")
    public EntidadMinecraft actualizar(@PathVariable("id") Long id, @RequestBody EntidadPatch cambios) {
        return servicio.actualizar(id, cambios);
    }

    /** Borrar: 204 sin cuerpo; después, el GET da 404. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        servicio.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
