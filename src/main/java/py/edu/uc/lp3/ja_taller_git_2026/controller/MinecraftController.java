package py.edu.uc.lp3.ja_taller_git_2026.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ja_taller_git_2026.domain.Aldeano;
import py.edu.uc.lp3.ja_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.ja_taller_git_2026.domain.EntidadMinecraft;

import java.util.Map;

@RestController
public class MinecraftController {

    @GetMapping("/minecraft/entidad")
    public Map<String, Object> crearEntidad(
            @RequestParam String tipo,
            @RequestParam String nombre,
            @RequestParam int vida) {

        EntidadMinecraft entidad;

        if (tipo.equalsIgnoreCase("creeper")) {
            entidad = new Creeper(nombre, vida);
        } else if (tipo.equalsIgnoreCase("aldeano")) {
            entidad = new Aldeano(nombre, vida);
        } else {
            throw new IllegalArgumentException("Tipo de entidad no válido");
        }

        return Map.of(
                "nombre", entidad.getNombre(),
                "vida", entidad.getVida(),
                "tipo", entidad.getClass().getSimpleName(),
                "movimiento", entidad.moverse(),
                "vivo", entidad.estaVivo()
        );
    }
}
