package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.ReglaRechazadaException;
import py.edu.uc.lp3.rest.dto.EsquiveEndermanRequest;
import py.edu.uc.lp3.rest.dto.OndaExpansivaRequest;
import py.edu.uc.lp3.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.service.ReglaExplosion;
import py.edu.uc.lp3.service.ReglaService;

/**
 * Evalúa mis dos reglas sobre números sueltos, sin tocar la base.
 *
 * <p>No duplica las fórmulas: llama a los métodos estáticos de
 * {@link ReglaExplosion}, los mismos que usa la simulación. Esa es la respuesta
 * a la pregunta de anclaje del enunciado: si una regla cambia, se toca en un
 * solo lugar y tanto la simulación como el endpoint que consulta un compañero
 * quedan de acuerdo.</p>
 */
@Service
public class ReglaServiceImpl implements ReglaService {

    @Override
    public VeredictoRegla evaluarOndaExpansiva(OndaExpansivaRequest pedido) {
        if (pedido == null) {
            throw new EntidadInvalidaException("Falta el cuerpo del pedido");
        }
        int potencia = exigirEntero(pedido.potencia(), "potencia");
        int radio = exigirEntero(pedido.radio(), "radio");
        double distancia = exigirDistancia(pedido.distancia());
        if (potencia <= 0) {
            throw new EntidadInvalidaException("La potencia debe ser mayor que 0");
        }
        if (radio <= 0) {
            throw new EntidadInvalidaException("El radio debe ser mayor que 0");
        }
        if (distancia > radio) {
            throw new ReglaRechazadaException("onda-expansiva",
                    "La onda no alcanza: la distancia " + distancia
                            + " es mayor que el radio " + radio + ", así que no corresponde daño");
        }
        int danio = ReglaExplosion.danioPorDistancia(potencia, radio, distancia);
        if (pedido.danioDeclarado() != null && pedido.danioDeclarado() != danio) {
            throw new ReglaRechazadaException("onda-expansiva",
                    "El daño declarado (" + pedido.danioDeclarado()
                            + ") no es el que corresponde por la onda expansiva (" + danio + ")");
        }
        return VeredictoRegla.ondaExpansiva(danio,
                "A " + distancia + " bloques de un explosivo de potencia " + potencia
                        + " y radio " + radio + " corresponden " + danio + " de daño");
    }

    @Override
    public VeredictoRegla evaluarEsquiveEnderman(EsquiveEndermanRequest pedido) {
        if (pedido == null) {
            throw new EntidadInvalidaException("Falta el cuerpo del pedido");
        }
        int radio = exigirEntero(pedido.radio(), "radio");
        int disponibles = exigirEntero(pedido.bloquesDisponibles(), "bloquesDisponibles");
        double distancia = exigirDistancia(pedido.distancia());
        if (radio <= 0) {
            throw new EntidadInvalidaException("El radio debe ser mayor que 0");
        }
        if (disponibles < 0) {
            throw new EntidadInvalidaException("Los bloques de teletransporte no pueden ser negativos");
        }
        if (distancia > radio) {
            throw new ReglaRechazadaException("esquive-enderman",
                    "La onda no alcanza al Enderman (distancia " + distancia
                            + " mayor que el radio " + radio + "): no hay nada que esquivar");
        }
        int necesarios = ReglaExplosion.bloquesNecesarios(radio, distancia);
        if (disponibles < necesarios) {
            throw new ReglaRechazadaException("esquive-enderman",
                    "El Enderman necesita " + necesarios + " bloques de teletransporte para salir del radio "
                            + radio + " y solo tiene " + disponibles + ": no puede esquivar");
        }
        return VeredictoRegla.esquive(necesarios,
                "Con " + disponibles + " bloques el Enderman gasta " + necesarios
                        + " y reaparece a " + (radio + 1) + " bloques del centro, sin recibir daño");
    }

    private static int exigirEntero(Integer valor, String campo) {
        if (valor == null) {
            throw new EntidadInvalidaException("Falta el campo '" + campo + "'");
        }
        return valor;
    }

    private static double exigirDistancia(Double distancia) {
        if (distancia == null) {
            throw new EntidadInvalidaException("Falta el campo 'distancia'");
        }
        if (distancia < 0) {
            throw new EntidadInvalidaException("La distancia no puede ser negativa");
        }
        return distancia;
    }
}
