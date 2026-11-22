package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cuerpo del POST /api/reglas/esquive-enderman. Solo datos.
 *
 * <p>Consulta pura a mi regla 2: con este radio, esta distancia y estos bloques
 * de teletransporte, ¿el Enderman logra salir del radio?</p>
 */
@Schema(description = """
        Jugada que se somete a la regla 2 (esquive del Enderman). La regla \
        acepta si los bloques disponibles alcanzan para salir del radio.""")
public record EsquiveEndermanRequest(

        @Schema(description = "Radio del explosivo en bloques.",
                example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer radio,

        @Schema(description = "Distancia entre el centro de la explosión y el Enderman.",
                example = "2.0", requiredMode = Schema.RequiredMode.REQUIRED)
        Double distancia,

        @Schema(description = "Bloques de teletransporte que le quedan al Enderman.",
                example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer bloquesDisponibles) {
}
