package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ExplosionImposibleException;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.repository.CreeperRepository;
import py.edu.uc.lp3.repository.EntidadMinecraftRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;
import py.edu.uc.lp3.service.ReglaExplosion;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.List;

/**
 * Simulación de explosiones. Acá viven las tres cosas que el enunciado pide en
 * la capa de servicios: el control de si el explosivo puede detonar (con su
 * excepción), la orquestación de la jugada y la aplicación de las dos reglas.
 *
 * <p>La simulación toca varias filas a la vez (el explosivo, cada entidad
 * alcanzada y el registro de la explosión), por eso corre en una transacción:
 * o se guarda todo, o si se lanza una excepción no se guarda nada.</p>
 *
 * <p>No pregunta de qué tipo es cada entidad. Recorre las reglas en el orden
 * declarado con {@code @Order} y usa la primera que dice
 * {@link ReglaExplosion#corresponde}: el Enderman cae en la regla del esquive y
 * el resto en la de la onda expansiva.</p>
 */
@Service
public class SimulacionServiceImpl implements SimulacionService {

    private final CreeperRepository creepers;
    private final BloqueTntRepository tnts;
    private final EntidadMinecraftRepository entidades;
    private final ExplosionRepository explosiones;
    private final List<ReglaExplosion> reglas;

    public SimulacionServiceImpl(CreeperRepository creepers, BloqueTntRepository tnts,
                                 EntidadMinecraftRepository entidades, ExplosionRepository explosiones,
                                 List<ReglaExplosion> reglas) {
        this.creepers = creepers;
        this.tnts = tnts;
        this.entidades = entidades;
        this.explosiones = explosiones;
        this.reglas = reglas;
    }

    @Override
    @Transactional
    public Explosion explotar(String tipoExplosivo, Long explosivoId) {
        Explosivo fuente = buscarExplosivo(tipoExplosivo, explosivoId);
        exigirQuePuedaExplotar(fuente);

        // Las entidades vivas se leen ANTES de consumir el explosivo, para que
        // un Creeper que está por explotar siga en la lista y no se dañe a sí mismo.
        List<EntidadMinecraft> vivas = entidades.findByVidaGreaterThan(0);

        fuente.consumir();
        Explosion explosion = new Explosion(fuente);
        for (EntidadMinecraft entidad : vivas) {
            if (entidad == fuente) {
                continue;               // el Creeper no se daña a sí mismo: ya se consumió
            }
            Impacto impacto = reglaPara(entidad).aplicar(entidad, fuente);
            if (impacto != null) {
                explosion.registrar(impacto);
            }
        }
        // El explosivo y las entidades modificadas se guardan solos al confirmar
        // la transacción (JPA detecta los cambios); la explosión es nueva y se inserta.
        return explosiones.save(explosion);
    }

    /** Primera regla que gobierna a esa entidad; las reglas vienen ordenadas. */
    private ReglaExplosion reglaPara(EntidadMinecraft entidad) {
        return reglas.stream()
                .filter(regla -> regla.corresponde(entidad))
                .findFirst()
                .orElseThrow(() -> new EntidadInvalidaException(
                        "No hay una regla de explosión para " + entidad.tipoEntidad()));
    }

    /** Regla de juego: un explosivo detona una sola vez. */
    private void exigirQuePuedaExplotar(Explosivo fuente) {
        if (fuente.puedeExplotar()) {
            return;
        }
        if (fuente instanceof Creeper creeper) {
            throw new ExplosionImposibleException(
                    "El Creeper '" + creeper.getNombre() + "' no puede explotar: ya explotó o no tiene vida");
        }
        throw new ExplosionImposibleException("La TNT " + fuente.getId() + " ya fue detonada");
    }

    private Explosivo buscarExplosivo(String tipo, Long id) {
        if (id == null) {
            throw new EntidadInvalidaException("Falta explosivoId");
        }
        String tipoNormalizado = tipo == null ? "" : tipo.trim().toLowerCase();
        return switch (tipoNormalizado) {
            case "creeper" -> creepers.findById(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("un Creeper", id));
            case "tnt" -> tnts.findById(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("una TNT", id));
            default -> throw new EntidadInvalidaException("tipoExplosivo debe ser 'creeper' o 'tnt'");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<Explosion> historial() {
        return explosiones.findAllByOrderByMomentoDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Explosion buscar(Long id) {
        if (id == null) {
            throw new EntidadInvalidaException("Falta el id de la explosión");
        }
        return explosiones.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("una explosión", id));
    }
}
