package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.EntidadMinecraftRepository;
import py.edu.uc.lp3.rest.dto.AldeanoRequest;
import py.edu.uc.lp3.rest.dto.CreeperRequest;
import py.edu.uc.lp3.rest.dto.EndermanRequest;
import py.edu.uc.lp3.rest.dto.EntidadPatch;
import py.edu.uc.lp3.rest.dto.EntidadRequest;
import py.edu.uc.lp3.service.EntidadService;

import java.util.List;

/**
 * Implementación del CRUD. Acá están las tres cosas que antes vivían en las
 * entities: las validaciones, la elección del constructor sobrecargado y las
 * excepciones del dominio.
 *
 * <p>Cada método que escribe corre en una transacción: si una validación falla,
 * se lanza la excepción antes de tocar la base y la fila queda como estaba.</p>
 */
@Service
public class EntidadServiceImpl implements EntidadService {

    private final EntidadMinecraftRepository entidades;

    public EntidadServiceImpl(EntidadMinecraftRepository entidades) {
        this.entidades = entidades;
    }

    @Override
    @Transactional
    public EntidadMinecraft crear(EntidadRequest pedido) {
        if (pedido == null) {
            throw new EntidadInvalidaException("Falta el cuerpo del pedido");
        }
        EntidadMinecraft entidad = construir(pedido);
        return entidades.save(entidad);
    }

    /**
     * Decide qué constructor sobrecargado usa cada pedido. Los campos que no
     * vienen se dejan al constructor simple; los que vienen obligan al completo.
     */
    private EntidadMinecraft construir(EntidadRequest pedido) {
        return switch (pedido) {
            case AldeanoRequest a -> construirAldeano(a);
            case CreeperRequest c -> construirCreeper(c);
            case EndermanRequest e -> construirEnderman(e);
        };
    }

    private EntidadMinecraft construirAldeano(AldeanoRequest pedido) {
        boolean conPosicion = pedido.x() != null || pedido.z() != null;
        if (!conPosicion) {
            return pedido.vida() == null
                    ? nuevoAldeano(pedido.nombre())
                    : nuevoAldeano(pedido.nombre(), pedido.vida());
        }
        if (pedido.vida() == null) {
            throw new EntidadInvalidaException("Para indicar posición también hay que indicar la vida");
        }
        return nuevoAldeano(pedido.nombre(), pedido.vida(), coordenada(pedido.x()), coordenada(pedido.z()));
    }

    private EntidadMinecraft construirCreeper(CreeperRequest pedido) {
        boolean conExtras = pedido.x() != null || pedido.z() != null || pedido.cargado() != null;
        if (!conExtras) {
            return pedido.vida() == null
                    ? nuevoCreeper(pedido.nombre())
                    : nuevoCreeper(pedido.nombre(), pedido.vida());
        }
        if (pedido.vida() == null) {
            throw new EntidadInvalidaException("Para indicar posición o carga también hay que indicar la vida");
        }
        int x = coordenada(pedido.x());
        int z = coordenada(pedido.z());
        if (pedido.cargado() == null) {
            return nuevoCreeper(pedido.nombre(), pedido.vida(), x, z);
        }
        return nuevoCreeper(pedido.nombre(), pedido.vida(), x, z, pedido.cargado());
    }

    private EntidadMinecraft construirEnderman(EndermanRequest pedido) {
        boolean conPosicion = pedido.x() != null || pedido.z() != null;
        Integer bloques = pedido.bloquesTeletransporte();
        if (pedido.vida() == null) {
            if (bloques != null || conPosicion) {
                throw new EntidadInvalidaException(
                        "Para indicar bloques o posición también hay que indicar la vida");
            }
            return nuevoEnderman(pedido.nombre());
        }
        if (bloques == null && !conPosicion) {
            return nuevoEnderman(pedido.nombre(), pedido.vida());
        }
        if (bloques == null) {
            throw new EntidadInvalidaException(
                    "Para indicar posición también hay que indicar los bloques de teletransporte");
        }
        if (!conPosicion) {
            return nuevoEnderman(pedido.nombre(), pedido.vida(), bloques);
        }
        return nuevoEnderman(pedido.nombre(), pedido.vida(), bloques,
                coordenada(pedido.x()), coordenada(pedido.z()));
    }

    // ---- Una fábrica por firma: valida y recién entonces construye ----
    // Las entities no validan, así que ningún camino puede saltearse esto.

    private Aldeano nuevoAldeano(String nombre) {
        validarNombre(nombre);
        return new Aldeano(nombre);
    }

    private Aldeano nuevoAldeano(String nombre, int vida) {
        validarNombre(nombre);
        validarVida(vida);
        return new Aldeano(nombre, vida);
    }

    private Aldeano nuevoAldeano(String nombre, int vida, int x, int z) {
        validarNombre(nombre);
        validarVida(vida);
        return new Aldeano(nombre, vida, x, z);
    }

    private Creeper nuevoCreeper(String nombre) {
        validarNombre(nombre);
        return new Creeper(nombre);
    }

    private Creeper nuevoCreeper(String nombre, int vida) {
        validarNombre(nombre);
        validarVida(vida);
        return new Creeper(nombre, vida);
    }

    private Creeper nuevoCreeper(String nombre, int vida, int x, int z) {
        validarNombre(nombre);
        validarVida(vida);
        return new Creeper(nombre, vida, x, z);
    }

    private Creeper nuevoCreeper(String nombre, int vida, int x, int z, boolean cargado) {
        validarNombre(nombre);
        validarVida(vida);
        return new Creeper(nombre, vida, x, z, cargado);
    }

    private Enderman nuevoEnderman(String nombre) {
        validarNombre(nombre);
        return new Enderman(nombre);
    }

    private Enderman nuevoEnderman(String nombre, int vida) {
        validarNombre(nombre);
        validarVida(vida);
        return new Enderman(nombre, vida);
    }

    private Enderman nuevoEnderman(String nombre, int vida, int bloques) {
        validarNombre(nombre);
        validarVida(vida);
        validarBloques(bloques);
        return new Enderman(nombre, vida, bloques);
    }

    private Enderman nuevoEnderman(String nombre, int vida, int bloques, int x, int z) {
        validarNombre(nombre);
        validarVida(vida);
        validarBloques(bloques);
        return new Enderman(nombre, vida, bloques, x, z);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadMinecraft> listar(String nombre, boolean soloVivas) {
        if (nombre != null && !nombre.isBlank()) {
            return entidades.findByNombreContainingIgnoreCase(nombre);
        }
        if (soloVivas) {
            return entidades.findByVidaGreaterThan(0);
        }
        return entidades.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EntidadMinecraft buscar(Long id) {
        return buscarOFallar(id);
    }

    @Override
    @Transactional
    public EntidadMinecraft actualizar(Long id, EntidadPatch cambios) {
        EntidadMinecraft entidad = buscarOFallar(id);
        if (cambios == null) {
            return entidad;
        }
        if (cambios.vida() != null) {
            throw new EntidadInvalidaException("La vida no se edita: cambia solo por las reglas del juego");
        }
        if (cambios.nombre() != null) {
            validarNombre(cambios.nombre());
            entidad.renombrar(cambios.nombre());
        }
        if (cambios.x() != null || cambios.z() != null) {
            if (!entidad.estaVivo()) {
                throw new EntidadInvalidaException("Una entidad sin vida no puede moverse");
            }
            int nuevoX = cambios.x() != null ? cambios.x() : entidad.getX();
            int nuevoZ = cambios.z() != null ? cambios.z() : entidad.getZ();
            entidad.ubicarEn(nuevoX, nuevoZ);
        }
        if (cambios.bloquesTeletransporte() != null) {
            if (!(entidad instanceof Enderman enderman)) {
                throw new EntidadInvalidaException("Solo un Enderman tiene bloques de teletransporte");
            }
            validarBloques(cambios.bloquesTeletransporte());
            enderman.fijarBloquesTeletransporte(cambios.bloquesTeletransporte());
        }
        return entidades.save(entidad);
    }

    @Override
    public EntidadMinecraft armarSinPersistir(String tipo, String nombre, Integer vida,
                                              Integer bloques, Integer danio, Boolean armadura) {
        EntidadMinecraft entidad = construirPorTipo(tipo, nombre, vida, bloques);
        if (danio != null) {
            validarDanio(danio);
            if (armadura != null) {
                entidad.aplicarDanio(danio, armadura);   // sobrecarga con armadura
            } else {
                entidad.aplicarDanio(danio);             // sobrecarga simple
            }
        }
        return entidad;
    }

    /** Construcción de POO-06: el tipo llega como texto en la URL. */
    private EntidadMinecraft construirPorTipo(String tipo, String nombre, Integer vida, Integer bloques) {
        String tipoNormalizado = tipo == null ? "" : tipo.trim().toLowerCase();
        return switch (tipoNormalizado) {
            case "creeper" -> vida == null
                    ? nuevoCreeper(nombre)
                    : nuevoCreeper(nombre, vida);
            case "aldeano" -> vida == null
                    ? nuevoAldeano(nombre)
                    : nuevoAldeano(nombre, vida);
            case "enderman" -> construirEndermanPorTipo(nombre, vida, bloques);
            default -> throw new EntidadInvalidaException("Tipo de entidad no válido: " + tipo);
        };
    }

    private EntidadMinecraft construirEndermanPorTipo(String nombre, Integer vida, Integer bloques) {
        if (vida == null && bloques == null) {
            return nuevoEnderman(nombre);
        }
        if (bloques == null) {
            return nuevoEnderman(nombre, vida);
        }
        if (vida == null) {
            throw new EntidadInvalidaException("Para indicar bloques también hay que indicar la vida");
        }
        return nuevoEnderman(nombre, vida, bloques);
    }

    @Override
    @Transactional
    public void borrar(Long id) {
        EntidadMinecraft entidad = buscarOFallar(id);
        entidad.desaparecer();      // hace "despawn" antes de salir de la base
        entidades.delete(entidad);
    }

    private EntidadMinecraft buscarOFallar(Long id) {
        if (id == null) {
            throw new EntidadInvalidaException("Falta el id de la entidad");
        }
        return entidades.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("una entidad", id));
    }

    // ---------------- Invariantes del dominio ----------------

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new EntidadInvalidaException("El nombre es obligatorio");
        }
    }

    private static void validarVida(int vida) {
        if (vida <= 0) {
            throw new EntidadInvalidaException("La vida inicial debe ser mayor que 0");
        }
    }

    private static void validarDanio(int danio) {
        if (danio < 0) {
            throw new EntidadInvalidaException("El daño no puede ser negativo");
        }
    }

    private static void validarBloques(int bloques) {
        if (bloques < 0) {
            throw new EntidadInvalidaException("Los bloques de teletransporte no pueden ser negativos");
        }
    }

    private static int coordenada(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
