package py.edu.uc.lp3.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Cuerpo JSON del POST /api/entidades. La propiedad {@code tipo} decide qué
 * record arma Jackson (creeper, aldeano o enderman).
 *
 * <p>Los tres records son solo datos: no validan y no construyen nada. Quién
 * revisa los valores y qué constructor sobrecargado se usa lo decide
 * {@code EntidadService}.</p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreeperRequest.class, name = "creeper"),
        @JsonSubTypes.Type(value = AldeanoRequest.class, name = "aldeano"),
        @JsonSubTypes.Type(value = EndermanRequest.class, name = "enderman")
})
public sealed interface EntidadRequest permits CreeperRequest, AldeanoRequest, EndermanRequest {

    /** Nombre pedido para la entidad. */
    String nombre();

    /** Vida pedida, o {@code null} para usar la del constructor simple. */
    Integer vida();

    /** Coordenada X pedida, o {@code null}. */
    Integer x();

    /** Coordenada Z pedida, o {@code null}. */
    Integer z();
}
