package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Explosion;

import java.util.List;

/** Orquesta la simulación de explosiones dentro de una transacción. */
public interface SimulacionService {

    /**
     * Detona el explosivo indicado contra todas las entidades vivas, aplica las
     * dos reglas del juego y guarda el resultado.
     *
     * @param tipoExplosivo "creeper" o "tnt"
     * @param explosivoId   id del explosivo en su tabla
     * @return la explosión persistida, con un impacto por cada entidad alcanzada
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si el tipo o el id no sirven
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si el explosivo no existe
     * @throws py.edu.uc.lp3.exceptions.ExplosionImposibleException si ya no puede explotar
     */
    Explosion explotar(String tipoExplosivo, Long explosivoId);

    /** Historial de explosiones, la más reciente primero. */
    List<Explosion> historial();

    /**
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si no existe
     */
    Explosion buscar(Long id);
}
