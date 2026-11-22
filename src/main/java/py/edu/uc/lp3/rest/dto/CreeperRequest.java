package py.edu.uc.lp3.rest.dto;

/**
 * Datos para crear un Creeper. Solo datos: no valida ni construye.
 * {@code cargado} es opcional (un Creeper alcanzado por un rayo).
 */
public record CreeperRequest(String nombre, Integer vida, Integer x, Integer z, Boolean cargado)
        implements EntidadRequest {
}
