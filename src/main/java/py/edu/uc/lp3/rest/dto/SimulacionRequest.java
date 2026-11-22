package py.edu.uc.lp3.rest.dto;

/** Cuerpo del POST /api/explosiones: qué explosivo detonar. */
public record SimulacionRequest(String tipoExplosivo, Long explosivoId) {
}
