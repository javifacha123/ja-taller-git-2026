package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * POO-09: cuerpo del POST /api/escenarios. Solo datos.
 *
 * <p>Las tres entidades tienen que existir ya en mi base: el escenario no las
 * crea, propone una jugada sobre entidades que están en el mundo.</p>
 *
 * <p>La potencia y el radio son opcionales. Si no vienen, se usan los del
 * Creeper. Si vienen, se somete a los compañeros la jugada propuesta: así se
 * puede probar que sus reglas rechazan valores ilegales en SUS modelos.</p>
 */
@Schema(description = "Jugada propuesta: el Creeper detona y la onda alcanza a un Aldeano y a un Enderman. Las tres entidades deben existir en esta base.")
public record EscenarioRequest(

        @Schema(description = "Nombre de la jugada, para encontrarla después en el historial.",
                example = "Emboscada en la aldea")
        String nombre,

        @Schema(description = "Id del Creeper que detona.", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long creeperId,

        @Schema(description = "Id del Aldeano que recibe la onda.", example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long aldeanoId,

        @Schema(description = "Id del Enderman que intenta esquivar.", example = "3",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long endermanId,

        @Schema(description = "Opcional. Potencia con la que se propone que detone, en vez de la del Creeper (12, o 24 si está cargado). Sirve para someter a los compañeros una jugada que sus reglas puedan rechazar.",
                example = "12")
        Integer potenciaPropuesta,

        @Schema(description = "Opcional. Radio con el que se propone que detone, en vez del del Creeper (3, o 6 si está cargado).",
                example = "3")
        Integer radioPropuesto) {
}
