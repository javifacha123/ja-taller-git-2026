package py.edu.uc.lp3.rest.dto;

/** Cuerpo del POST /api/explosiones: que explosivo detonar. */
public record SimulacionRequest(String tipoExplosivo, Long explosivoId) {
}
