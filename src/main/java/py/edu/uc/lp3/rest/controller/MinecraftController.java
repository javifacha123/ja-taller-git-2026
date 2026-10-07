package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;

import java.util.Map;

@RestController
public class MinecraftController {

    @GetMapping("/minecraft/entidad")
    public Map<String, Object> crearEntidad(
            @RequestParam String tipo,
            @RequestParam String nombre,
            @RequestParam(required = false) Integer vida,
            @RequestParam(required = false) Integer bloques,
            @RequestParam(required = false) Integer danio,
            @RequestParam(required = false) Boolean armadura) {

        EntidadMinecraft entidad = construir(tipo, nombre, vida, bloques);

        if (danio != null) {
            if (armadura != null) {
                entidad.recibirDanio(danio, armadura);   // sobrecarga con armadura
            } else {
                entidad.recibirDanio(danio);             // sobrecarga simple
            }
        }

        return Map.of(
                "nombre", entidad.getNombre(),
                "vida", entidad.getVida(),
                "tipo", entidad.getClass().getSimpleName(),
                "movimiento", entidad.moverse(),
                "vivo", entidad.estaVivo()
        );
    }

    private EntidadMinecraft construir(String tipo, String nombre, Integer vida, Integer bloques) {
        switch (tipo.toLowerCase()) {
            case "creeper":
                return vida == null ? new Creeper(nombre) : new Creeper(nombre, vida);
            case "aldeano":
                return vida == null ? new Aldeano(nombre) : new Aldeano(nombre, vida);
            case "enderman":
                if (vida == null && bloques == null) {
                    return new Enderman(nombre);
                }
                if (bloques == null) {
                    return new Enderman(nombre, vida);
                }
                if (vida == null) {
                    throw new IllegalArgumentException("Para indicar bloques también hay que indicar la vida");
                }
                return new Enderman(nombre, vida, bloques);
            default:
                throw new IllegalArgumentException("Tipo de entidad no válido: " + tipo);
        }
    }

    // Las reglas viven en el dominio; el controller solo informa el rechazo
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarError(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
