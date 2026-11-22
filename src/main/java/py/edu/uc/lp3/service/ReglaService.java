package py.edu.uc.lp3.service;

import py.edu.uc.lp3.rest.dto.EsquiveEndermanRequest;
import py.edu.uc.lp3.rest.dto.OndaExpansivaRequest;
import py.edu.uc.lp3.rest.dto.VeredictoRegla;

/**
 * POO-09: mis dos reglas del juego, expuestas para que las consulte otro
 * servicio.
 *
 * <p>Son consultas puras: no leen ni escriben la base. Usan exactamente las
 * mismas fórmulas que la simulación, porque las dos puertas llaman a los
 * métodos estáticos de {@link ReglaExplosion}. Si una fórmula cambia, cambia
 * para las dos a la vez.</p>
 */
public interface ReglaService {

    /**
     * REGLA 1 — Onda expansiva.
     *
     * @return el veredicto con el daño que corresponde
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si faltan datos o son negativos
     * @throws py.edu.uc.lp3.exceptions.ReglaRechazadaException si la entidad está fuera
     *         del radio, o si el daño declarado no coincide con la fórmula
     */
    VeredictoRegla evaluarOndaExpansiva(OndaExpansivaRequest pedido);

    /**
     * REGLA 2 — Esquive del Enderman.
     *
     * @return el veredicto con los bloques que necesita gastar
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si faltan datos o son negativos
     * @throws py.edu.uc.lp3.exceptions.ReglaRechazadaException si está fuera del radio
     *         (no hay nada que esquivar) o si no le alcanzan los bloques
     */
    VeredictoRegla evaluarEsquiveEnderman(EsquiveEndermanRequest pedido);
}
