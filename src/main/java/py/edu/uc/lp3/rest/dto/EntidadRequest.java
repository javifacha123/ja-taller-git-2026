package py.edu.uc.lp3.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import py.edu.uc.lp3.domain.EntidadMinecraft;

/**
 * Cuerpo JSON del POST /api/entidades. La propiedad "tipo" decide que record
 * arma Jackson (creeper, aldeano o enderman). Cada record traduce el JSON a
 * una llamada al constructor sobrecargado que corresponde: asi las reglas de
 * construccion (vida > 0, nombre obligatorio...) las sigue validando el dominio.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreeperRequest.class, name = "creeper"),
        @JsonSubTypes.Type(value = AldeanoRequest.class, name = "aldeano"),
        @JsonSubTypes.Type(value = EndermanRequest.class, name = "enderman")
})
public sealed interface EntidadRequest permits CreeperRequest, AldeanoRequest, EndermanRequest {

    /** Construye la entidad de dominio; si algun valor es ilegal, el constructor lanza la excepcion. */
    EntidadMinecraft crear();

    static int coordenada(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
