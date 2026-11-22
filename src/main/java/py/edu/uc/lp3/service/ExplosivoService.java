package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Explosivo;

import java.util.List;

/**
 * Lectura polimórfica por el protocolo {@link Explosivo}: junta las dos
 * familias (Creeper y BloqueTnt) en una sola lista y les habla solo con los
 * mensajes de la interface.
 */
public interface ExplosivoService {

    /**
     * @param soloDisponibles true para dejar fuera los que ya explotaron
     */
    List<Explosivo> listar(boolean soloDisponibles);
}
