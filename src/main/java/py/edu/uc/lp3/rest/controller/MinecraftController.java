package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.service.EntidadService;

import java.util.Map;

/**
 * Endpoint de POO-06: construcción por parámetros de la URL, sin persistir.
 * Se conserva para no romper las pruebas anteriores.
 *
 * <p>Solo traduce HTTP: el servicio valida, elige el constructor sobrecargado y
 * aplica el daño. Los errores los atiende MinecraftExceptionHandler.</p>
 */
@RestController
public class MinecraftController {

    private final EntidadService servicio;

    public MinecraftController(EntidadService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/minecraft/entidad")
    public Map<String, Object> crearEntidad(
            @RequestParam String tipo,
            @RequestParam String nombre,
            @RequestParam(required = false) Integer vida,
            @RequestParam(required = false) Integer bloques,
            @RequestParam(required = false) Integer danio,
            @RequestParam(required = false) Boolean armadura) {

        EntidadMinecraft entidad = servicio.armarSinPersistir(tipo, nombre, vida, bloques, danio, armadura);

        return Map.of(
                "nombre", entidad.getNombre(),
                "vida", entidad.getVida(),
                "tipo", entidad.getClass().getSimpleName(),
                "movimiento", entidad.moverse(),
                "vivo", entidad.estaVivo()
        );
    }
}
