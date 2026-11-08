package py.edu.uc.lp3.rest.dto;

import java.time.OffsetDateTime;

/**
 * JSON de error comun a todos los handlers.
 * "error" es el codigo de la excepcion: con el, el cliente distingue a las hermanas
 * aunque dos compartieran el codigo HTTP.
 */
public record ErrorRespuesta(String error, String excepcion, int estado, String mensaje, String ruta, String momento) {

    public static ErrorRespuesta de(String codigo, Exception ex, int estado, String mensaje, String ruta) {
        return new ErrorRespuesta(codigo, ex.getClass().getSimpleName(), estado, mensaje, ruta,
                OffsetDateTime.now().toString());
    }
}
