package py.edu.uc.lp3.rest.dto;

/** Cuerpo del POST /api/tnt: dónde se coloca el bloque. Solo datos. */
public record TntRequest(Integer x, Integer z) {
}
