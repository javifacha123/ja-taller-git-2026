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
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.EntidadMinecraftRepository;
import py.edu.uc.lp3.rest.dto.EntidadPatch;
import py.edu.uc.lp3.rest.dto.EntidadRequest;

import java.net.URI;
import java.util.List;

/**
 * CRUD REST de la familia EntidadMinecraft. El controller traduce HTTP:
 * pide al repositorio (una interfaz) y manda mensajes al dominio. No asigna
 * campos ni decide reglas.
 */
@RestController
@RequestMapping(ApiPaths.ENTIDADES)
public class EntidadController {

    private final EntidadMinecraftRepository repositorio;

    public EntidadController(EntidadMinecraftRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** Crear: 201 con el recurso (y su id) y la cabecera Location. */
    @PostMapping
    public ResponseEntity<EntidadMinecraft> crear(@RequestBody EntidadRequest pedido) {
        EntidadMinecraft entidad = pedido.crear();          // si es ilegal, se lanza aca y no se guarda nada
        EntidadMinecraft guardada = repositorio.save(entidad);
        URI ubicacion = URI.create(ApiPaths.ENTIDADES + "/" + guardada.getId());
        return ResponseEntity.created(ubicacion).body(guardada);
    }

    /** Leer varios: lo persistido, con filtros opcionales ?nombre= y ?soloVivas=true. */
    @GetMapping
    public List<EntidadMinecraft> listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "soloVivas", defaultValue = "false") boolean soloVivas) {
        if (nombre != null && !nombre.isBlank()) {
            return repositorio.findByNombreContainingIgnoreCase(nombre);
        }
        if (soloVivas) {
            return repositorio.findByVidaGreaterThan(0);
        }
        return repositorio.findAll();
    }

    /** Leer uno: 200 o 404. */
    @GetMapping("/{id}")
    public EntidadMinecraft buscar(@PathVariable("id") Long id) {
        return buscarOFallar(id);
    }

    /**
     * Actualizar parcial (PATCH): solo cambia los campos que vienen en el JSON,
     * siempre a traves de mensajes del dominio. Si alguno es ilegal, se lanza la
     * excepcion antes de guardar y la fila queda como estaba.
     */
    @PatchMapping("/{id}")
    public EntidadMinecraft actualizar(@PathVariable("id") Long id, @RequestBody EntidadPatch cambios) {
        EntidadMinecraft entidad = buscarOFallar(id);
        if (cambios.vida() != null) {
            throw new EntidadInvalidaException("La vida no se edita: cambia solo por las reglas del juego");
        }
        if (cambios.nombre() != null) {
            entidad.renombrar(cambios.nombre());
        }
        if (cambios.x() != null || cambios.z() != null) {
            int nuevoX = cambios.x() != null ? cambios.x() : entidad.getX();
            int nuevoZ = cambios.z() != null ? cambios.z() : entidad.getZ();
            entidad.moverA(nuevoX, nuevoZ);
        }
        if (cambios.bloquesTeletransporte() != null) {
            if (!(entidad instanceof Enderman enderman)) {
                throw new EntidadInvalidaException("Solo un Enderman tiene bloques de teletransporte");
            }
            enderman.setBloquesTeletransporte(cambios.bloquesTeletransporte());
        }
        return repositorio.save(entidad);
    }

    /** Borrar: la entidad hace "despawn" (sale del mundo). 204 sin cuerpo; despues, GET da 404. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        EntidadMinecraft entidad = buscarOFallar(id);
        entidad.desaparecer();
        repositorio.delete(entidad);
        return ResponseEntity.noContent().build();
    }

    private EntidadMinecraft buscarOFallar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("una entidad", id));
    }
}
