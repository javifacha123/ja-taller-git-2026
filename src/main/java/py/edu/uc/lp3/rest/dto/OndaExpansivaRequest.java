package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cuerpo del POST /api/reglas/onda-expansiva. Solo datos.
 *
 * <p>Es una consulta pura: no toca la base y no necesita que exista ninguna
 * entidad. Cualquier servicio puede preguntarle a mi regla 1 cuánto daño
 * corresponde, o si el daño que calculó él es el correcto.</p>
 */
@Schema(description = """
        Jugada que se somete a la regla 1 (onda expansiva). Si se manda \
        'danioDeclarado', la regla valida ese número; si se omite, la regla \
        simplemente informa cuánto daño corresponde.""")
public record OndaExpansivaRequest(

        @Schema(description = "Potencia del explosivo. Creeper 12 (cargado 24), TNT 16.",
                example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer potencia,

        @Schema(description = "Radio del explosivo en bloques. Creeper 3 (cargado 6), TNT 4.",
                example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer radio,

        @Schema(description = "Distancia en bloques entre el centro de la explosión y la entidad.",
                example = "1.41", requiredMode = Schema.RequiredMode.REQUIRED)
        Double distancia,

        @Schema(description = """
                Opcional. Daño que el que consulta afirma que corresponde. Si \
                viene y no coincide con la fórmula, la regla responde 400 \
                REGLA_RECHAZADA.""", example = "8")
        Integer danioDeclarado) {
}
