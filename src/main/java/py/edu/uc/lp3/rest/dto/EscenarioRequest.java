package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * POO-09: cuerpo del POST /api/escenarios. Solo datos.
 *
 * <p>Las tres entidades tienen que existir ya en mi base: el escenario no las
 * crea, propone una jugada sobre entidades que están en el mundo.</p>
 */
@Schema(description = """
        Jugada propuesta: el Creeper detona y la onda alcanza a un Aldeano y a \
        un Enderman. Las tres entidades deben existir en esta base.""")
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
        Long endermanId) {
}
