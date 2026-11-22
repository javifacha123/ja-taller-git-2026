package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.BloqueTnt;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.BloqueTntRepository;
import py.edu.uc.lp3.rest.dto.TntRequest;
import py.edu.uc.lp3.service.TntService;

import java.util.List;

/** Coloca y consulta bloques de TNT. Valida antes de construir. */
@Service
public class TntServiceImpl implements TntService {

    private final BloqueTntRepository tnts;

    public TntServiceImpl(BloqueTntRepository tnts) {
        this.tnts = tnts;
    }

    @Override
    @Transactional
    public BloqueTnt colocar(TntRequest pedido) {
        if (pedido == null || pedido.x() == null || pedido.z() == null) {
            throw new EntidadInvalidaException("Para colocar una TNT hay que indicar x y z");
        }
        return tnts.save(new BloqueTnt(pedido.x(), pedido.z()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueTnt> listar() {
        return tnts.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BloqueTnt buscar(Long id) {
        if (id == null) {
            throw new EntidadInvalidaException("Falta el id de la TNT");
        }
        return tnts.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("una TNT", id));
    }
}
