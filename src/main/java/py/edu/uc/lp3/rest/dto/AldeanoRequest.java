package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

public record AldeanoRequest(String nombre, Integer vida, Integer x, Integer z) implements EntidadRequest {

    @Override
    public EntidadMinecraft crear() {
        if (x == null && z == null) {
            return vida == null ? new Aldeano(nombre) : new Aldeano(nombre, vida);
        }
        if (vida == null) {
            throw new EntidadInvalidaException("Para indicar posición también hay que indicar la vida");
        }
        return new Aldeano(nombre, vida, EntidadRequest.coordenada(x), EntidadRequest.coordenada(z));
    }
}
