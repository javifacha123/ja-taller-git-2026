package py.edu.uc.lp3.rest.dto;

import java.time.OffsetDateTime;

/**
 * JSON de error común a todos los handlers.
 * "error" es el código de la excepción: con él, el cliente distingue a las hermanas
 * aunque dos compartieran el código HTTP.
 */
public record ErrorRespuesta(String error, String excepcion, int estado, String mensaje, String ruta, String momento) {

    public static ErrorRespuesta de(String codigo, Exception ex, int estado, String mensaje, String ruta) {
        return new ErrorRespuesta(codigo, ex.getClass().getSimpleName(), estado, mensaje, ruta,
                OffsetDateTime.now().toString());
    }
}
