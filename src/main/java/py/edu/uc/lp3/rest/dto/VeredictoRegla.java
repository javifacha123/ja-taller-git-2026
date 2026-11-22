package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta de una de mis dos reglas cuando ACEPTA la jugada (HTTP 200).
 *
 * <p>Cuando la rechaza, el servicio lanza {@code ReglaRechazadaException} y el
 * handler responde 400 con el JSON de error común, cuyo campo {@code error}
 * vale {@code REGLA_RECHAZADA}. Es la misma forma que usa el ejemplo del
 * enunciado: 200 si la regla ajena deja pasar la jugada, 400 si no.</p>
 */
@Schema(description = "Veredicto favorable de una regla, con el número que la regla calculó.")
public record VeredictoRegla(

        @Schema(description = "Regla que respondió.", example = "onda-expansiva")
        String regla,

        @Schema(description = "Siempre true: el rechazo viaja como 400, no como false.",
                example = "true")
        boolean aceptada,

        @Schema(description = """
                Daño que corresponde por la regla 1, o null si respondió la \
                regla 2.""", example = "8")
        Integer danioCalculado,

        @Schema(description = """
                Bloques que el Enderman necesita gastar por la regla 2, o null \
                si respondió la regla 1.""", example = "2")
        Integer bloquesNecesarios,

        @Schema(description = "Explicación en una frase, para que el que consulta la registre.")
        String motivo) {

    /** Veredicto de la regla 1. */
    public static VeredictoRegla ondaExpansiva(int danio, String motivo) {
        return new VeredictoRegla("onda-expansiva", true, danio, null, motivo);
    }

    /** Veredicto de la regla 2. */
    public static VeredictoRegla esquive(int bloquesNecesarios, String motivo) {
        return new VeredictoRegla("esquive-enderman", true, null, bloquesNecesarios, motivo);
    }
}
