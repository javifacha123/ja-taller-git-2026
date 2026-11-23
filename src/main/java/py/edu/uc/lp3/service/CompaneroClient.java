package py.edu.uc.lp3.service;

import py.edu.uc.lp3.config.CompanerosProperties.Companero;

import java.util.Map;

/**
 * POO-09: habla con la API de un compañero.
 *
 * <p>Toda la dependencia con servicios ajenos entra por acá. El servicio del
 * escenario no sabe de HTTP: le pide a este cliente que consulte y recibe una
 * frase, o una excepción si el compañero rechazó o no respondió.</p>
 */
public interface CompaneroClient {

    /**
     * Le propone la jugada a un compañero y espera su veredicto.
     *
     * @param companero datos configurados (URL, ruta, nombre)
     * @param rol       "A2" o "A3", para poder atribuir el rechazo
     * @param cuerpo    los datos de la jugada que viajan como JSON
     * @return la respuesta del compañero, en texto, cuando acepta
     * @throws py.edu.uc.lp3.exceptions.CompaneroRechazoException si respondió 4xx
     * @throws py.edu.uc.lp3.exceptions.CompaneroInaccesibleException si no respondió o falló
     */
    String consultar(Companero companero, String rol, Map<String, Object> cuerpo);
}
