package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

public record EndermanRequest(String nombre, Integer vida, Integer bloquesTeletransporte, Integer x, Integer z)
        implements EntidadRequest {

    @Override
    public EntidadMinecraft crear() {
        boolean conPosicion = x != null || z != null;
        if (vida == null) {
            if (bloquesTeletransporte != null || conPosicion) {
                throw new EntidadInvalidaException("Para indicar bloques o posición también hay que indicar la vida");
            }
            return new Enderman(nombre);
        }
        if (bloquesTeletransporte == null && !conPosicion) {
            return new Enderman(nombre, vida);
        }
        if (bloquesTeletransporte == null) {
            throw new EntidadInvalidaException("Para indicar posición también hay que indicar los bloques de teletransporte");
        }
        if (!conPosicion) {
            return new Enderman(nombre, vida, bloquesTeletransporte);
        }
        return new Enderman(nombre, vida, bloquesTeletransporte,
                EntidadRequest.coordenada(x), EntidadRequest.coordenada(z));
    }
}
