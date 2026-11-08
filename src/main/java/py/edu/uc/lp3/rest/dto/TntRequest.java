package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.BloqueTnt;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

/** Cuerpo del POST /api/tnt: donde se coloca el bloque. */
public record TntRequest(Integer x, Integer z) {

    public BloqueTnt crear() {
        if (x == null || z == null) {
            throw new EntidadInvalidaException("Para colocar una TNT hay que indicar x y z");
        }
        return new BloqueTnt(x, z);
    }
}
