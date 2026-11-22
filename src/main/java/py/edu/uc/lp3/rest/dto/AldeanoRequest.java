package py.edu.uc.lp3.rest.dto;

/** Datos para crear un Aldeano. Solo datos: no valida ni construye. */
public record AldeanoRequest(String nombre, Integer vida, Integer x, Integer z)
        implements EntidadRequest {
}
