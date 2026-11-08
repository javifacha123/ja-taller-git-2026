package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.repository.CreeperRepository;
import py.edu.uc.lp3.repository.EntidadMinecraftRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.List;

/**
 * La simulacion toca varias filas a la vez (el explosivo, cada entidad alcanzada
 * y el registro de la explosion), por eso corre en una transaccion: o se guarda
 * todo o, si el dominio lanza una excepcion, no se guarda nada.
 * Las reglas no estan aca: estan en Explosion.provocar y en recibirExplosion.
 */
@Service
public class SimulacionServiceImpl implements SimulacionService {

    private final CreeperRepository creepers;
    private final BloqueTntRepository tnts;
    private final EntidadMinecraftRepository entidades;
    private final ExplosionRepository explosiones;

    public SimulacionServiceImpl(CreeperRepository creepers, BloqueTntRepository tnts,
                                 EntidadMinecraftRepository entidades, ExplosionRepository explosiones) {
        this.creepers = creepers;
        this.tnts = tnts;
        this.entidades = entidades;
        this.explosiones = explosiones;
    }

    @Override
    @Transactional
    public Explosion explotar(String tipoExplosivo, Long explosivoId) {
        Explosivo fuente = buscarExplosivo(tipoExplosivo, explosivoId);
        List<EntidadMinecraft> vivas = entidades.findByVidaGreaterThan(0);
        Explosion explosion = Explosion.provocar(fuente, vivas);
        // El explosivo y las entidades modificadas se guardan solos al confirmar la
        // transaccion (JPA detecta los cambios); la explosion es nueva y se inserta.
        return explosiones.save(explosion);
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
}
