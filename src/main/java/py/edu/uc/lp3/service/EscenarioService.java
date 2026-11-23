package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;

import java.util.List;

/**
 * POO-09: el escenario propio, que consulta las reglas de A2 y de A3.
 *
 * <p>La jugada se guarda en mi H2 <strong>solo si las dos</strong> la aceptan.
 * Si alguna rechaza, la base queda igual y la excepción dice de quién fue el
 * rechazo.</p>
 */
public interface EscenarioService {

    /**
     * Propone la jugada a A2 y a A3 y la persiste solo si ambos aceptan.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si faltan ids o los
     *         tipos no son los que el escenario espera
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si alguna entidad no existe
     * @throws py.edu.uc.lp3.exceptions.CompaneroRechazoException si A2 o A3 rechazan
     * @throws py.edu.uc.lp3.exceptions.CompaneroInaccesibleException si A2 o A3 no responden
     */
    Jugada jugar(EscenarioRequest pedido);

    /** Jugadas aceptadas, la más reciente primero. */
    List<Jugada> historial();
}
