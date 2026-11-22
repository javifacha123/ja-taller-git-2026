package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.BloqueTnt;
import py.edu.uc.lp3.rest.dto.TntRequest;

import java.util.List;

/** Lógica de negocio de los bloques de TNT. */
public interface TntService {

    /**
     * Valida el pedido y coloca un bloque de TNT.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si falta x o z
     */
    BloqueTnt colocar(TntRequest pedido);

    List<BloqueTnt> listar();

    /**
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si no existe
     */
    BloqueTnt buscar(Long id);
}
