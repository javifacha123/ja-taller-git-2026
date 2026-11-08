package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

public record CreeperRequest(String nombre, Integer vida, Integer x, Integer z, Boolean cargado)
        implements EntidadRequest {

    @Override
    public EntidadMinecraft crear() {
        if (x == null && z == null && cargado == null) {
            return vida == null ? new Creeper(nombre) : new Creeper(nombre, vida);
        }
        if (vida == null) {
            throw new EntidadInvalidaException("Para indicar posición o carga también hay que indicar la vida");
        }
        if (cargado == null) {
            return new Creeper(nombre, vida, EntidadRequest.coordenada(x), EntidadRequest.coordenada(z));
        }
        return new Creeper(nombre, vida, EntidadRequest.coordenada(x), EntidadRequest.coordenada(z), cargado);
    }
}
