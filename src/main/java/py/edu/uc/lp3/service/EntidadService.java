package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.rest.dto.EntidadPatch;
import py.edu.uc.lp3.rest.dto.EntidadRequest;

import java.util.List;

/**
 * Lógica de negocio del CRUD de la familia EntidadMinecraft.
 *
 * <p>Es la única capa que valida, que decide qué constructor sobrecargado usar
 * y que lanza las excepciones del dominio. El controller solo traduce HTTP y
 * los repositorios solo se usan desde acá.</p>
 */
public interface EntidadService {

    /**
     * Valida el pedido y crea la entidad del subtipo que corresponda.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si algún valor
     *         dejaría la entidad en un estado imposible
     */
    EntidadMinecraft crear(EntidadRequest pedido);

    /**
     * Lista lo persistido.
     *
     * @param nombre    parte del nombre a buscar, o {@code null} para no filtrar
     * @param soloVivas true para devolver solo las entidades con vida
     */
    List<EntidadMinecraft> listar(String nombre, boolean soloVivas);

    /**
     * Busca una entidad por id.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si no existe
     */
    EntidadMinecraft buscar(Long id);

    /**
     * Cambia solo los campos que vienen en el pedido.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si se intenta
     *         editar la vida, mover una entidad muerta o poner bloques negativos
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si no existe
     */
    EntidadMinecraft actualizar(Long id, EntidadPatch cambios);

    /**
     * POO-06: arma una entidad desde los parámetros de la URL y le aplica el
     * daño pedido, SIN persistirla. Usa los constructores sobrecargados y las
     * dos firmas de {@code aplicarDanio}.
     *
     * @param tipo      "creeper", "aldeano" o "enderman"
     * @param vida      opcional; sin ella se usa el constructor simple
     * @param bloques   opcional; solo para el Enderman
     * @param danio     opcional; si viene, se le aplica
     * @param armadura  opcional; con true el daño que llega es la mitad
     * @throws py.edu.uc.lp3.exceptions.EntidadInvalidaException si algún valor es ilegal
     */
    EntidadMinecraft armarSinPersistir(String tipo, String nombre, Integer vida,
                                       Integer bloques, Integer danio, Boolean armadura);

    /**
     * Saca la entidad del mundo y borra su fila.
     *
     * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si no existe
     */
    void borrar(Long id);
}
