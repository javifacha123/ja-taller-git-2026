package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Explosion;

/** Orquesta la simulacion de explosiones dentro de una transaccion. */
public interface SimulacionService {

    /**
     * Detona el explosivo indicado contra todas las entidades vivas y guarda el resultado.
     *
     * @param tipoExplosivo "creeper" o "tnt"
     * @param explosivoId   id del explosivo en su tabla
     * @return la explosion persistida, con un impacto por cada entidad alcanzada
     */
    Explosion explotar(String tipoExplosivo, Long explosivoId);
}
