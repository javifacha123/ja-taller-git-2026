package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.repository.CreeperRepository;
import py.edu.uc.lp3.service.ExplosivoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Junta las dos familias que implementan {@link Explosivo} en una sola lista
 * y las filtra hablándoles solo por la interface: no pregunta si detrás hay un
 * Creeper o una TNT.
 */
@Service
public class ExplosivoServiceImpl implements ExplosivoService {

    private final CreeperRepository creepers;
    private final BloqueTntRepository tnts;

    public ExplosivoServiceImpl(CreeperRepository creepers, BloqueTntRepository tnts) {
        this.creepers = creepers;
        this.tnts = tnts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Explosivo> listar(boolean soloDisponibles) {
        List<Explosivo> explosivos = new ArrayList<>();
        explosivos.addAll(creepers.findAll());
        explosivos.addAll(tnts.findAll());
        return explosivos.stream()
                .filter(explosivo -> !soloDisponibles || explosivo.puedeExplotar())
                .toList();
    }
}
