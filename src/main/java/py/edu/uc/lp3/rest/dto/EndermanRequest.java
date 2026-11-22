package py.edu.uc.lp3.rest.dto;

/**
 * Datos para crear un Enderman. Solo datos: no valida ni construye.
 * {@code bloquesTeletransporte} es opcional.
 */
public record EndermanRequest(String nombre, Integer vida, Integer bloquesTeletransporte,
                              Integer x, Integer z) implements EntidadRequest {
}
