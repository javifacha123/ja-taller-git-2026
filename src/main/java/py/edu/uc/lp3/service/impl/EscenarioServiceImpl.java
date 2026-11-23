package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.config.CompanerosProperties;
import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.EntidadMinecraftRepository;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.service.CompaneroClient;
import py.edu.uc.lp3.service.EscenarioService;
import py.edu.uc.lp3.service.ReglaExplosion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * POO-09: arma la jugada, la somete a las reglas de los dos compañeros y la
 * guarda solo si las dos la aceptan.
 *
 * <p>Orden de la operación:</p>
 * <ol>
 *   <li>Lee de mi base el Creeper, el Aldeano y el Enderman.</li>
 *   <li>Calcula con MIS reglas qué debería pasar: cuánto daño recibe el
 *       Aldeano (regla 1) y cuántos bloques necesita el Enderman (regla 2).</li>
 *   <li>Le propone a <strong>A2</strong> la mitad del daño y a <strong>A3</strong>
 *       la mitad del esquive. Cada uno responde por SUS reglas.</li>
 *   <li>Si los dos aceptan, guarda la {@link Jugada}. Si alguno rechaza, lanza
 *       la excepción y la transacción no deja nada escrito.</li>
 * </ol>
 *
 * <p>El método es {@code @Transactional}: aunque A3 rechace después de que A2
 * aceptó, no queda ninguna fila a medias. La atomicidad la da la transacción,
 * no un borrado manual.</p>
 */
@Service
public class EscenarioServiceImpl implements EscenarioService {

    private final EntidadMinecraftRepository entidades;
    private final JugadaRepository jugadas;
    private final CompaneroClient companeroClient;
    private final CompanerosProperties companeros;

    public EscenarioServiceImpl(EntidadMinecraftRepository entidades, JugadaRepository jugadas,
                                CompaneroClient companeroClient, CompanerosProperties companeros) {
        this.entidades = entidades;
        this.jugadas = jugadas;
        this.companeroClient = companeroClient;
        this.companeros = companeros;
    }

    @Override
    @Transactional
    public Jugada jugar(EscenarioRequest pedido) {
        if (pedido == null) {
            throw new EntidadInvalidaException("Falta el cuerpo del pedido");
        }
        Creeper creeper = buscar(pedido.creeperId(), Creeper.class, "un Creeper");
        Aldeano aldeano = buscar(pedido.aldeanoId(), Aldeano.class, "un Aldeano");
        Enderman enderman = buscar(pedido.endermanId(), Enderman.class, "un Enderman");

        // Lo que dicen MIS reglas sobre esta jugada.
        double distanciaAldeano = aldeano.distanciaA(creeper.getX(), creeper.getZ());
        double distanciaEnderman = enderman.distanciaA(creeper.getX(), creeper.getZ());
        int potencia = creeper.potenciaExplosion();
        int radio = creeper.radioExplosion();
        int danio = ReglaExplosion.danioPorDistancia(potencia, radio, distanciaAldeano);
        int bloques = ReglaExplosion.bloquesNecesarios(radio, distanciaEnderman);

        // A2 valida la mitad del daño; A3, la mitad del esquive.
        String veredictoA2 = companeroClient.consultar(companeros.getA2(), "A2",
                cuerpoOnda(potencia, radio, distanciaAldeano, danio));
        String veredictoA3 = companeroClient.consultar(companeros.getA3(), "A3",
                cuerpoEsquive(potencia, radio, distanciaEnderman, enderman.getBloquesTeletransporte()));

        // Solo se llega acá si NINGUNO lanzó: las dos aceptaron.
        String nombre = pedido.nombre() == null || pedido.nombre().isBlank()
                ? "Jugada sin nombre"
                : pedido.nombre();
        Jugada jugada = new Jugada(nombre, creeper, aldeano, enderman, danio, bloques);
        jugada.registrarVeredictos(
                companeros.getA2().getNombre(), recortar(veredictoA2),
                companeros.getA3().getNombre(), recortar(veredictoA3));
        return jugadas.save(jugada);
    }

    /**
     * Datos de la mitad "daño" de la jugada.
     *
     * <p>Se mandan los hechos con varios nombres porque cada compañero bautizó
     * sus campos a su manera; los que no le sirven, los ignora. Si A2 necesita
     * una forma distinta, se cambia solo este método.</p>
     */
    private Map<String, Object> cuerpoOnda(int potencia, int radio, double distancia, int danio) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("potencia", potencia);
        cuerpo.put("radio", radio);
        cuerpo.put("distancia", redondear(distancia));
        cuerpo.put("danio", danio);
        cuerpo.put("danioDeclarado", danio);
        return cuerpo;
    }

    /** Datos de la mitad "esquive" de la jugada. Mismo criterio que arriba. */
    private Map<String, Object> cuerpoEsquive(int potencia, int radio, double distancia, int disponibles) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("potencia", potencia);
        cuerpo.put("radio", radio);
        cuerpo.put("distancia", redondear(distancia));
        cuerpo.put("bloquesDisponibles", disponibles);
        cuerpo.put("bloquesTeletransporte", disponibles);
        return cuerpo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugada> historial() {
        return jugadas.findAllByOrderByMomentoDesc();
    }

    private <T extends EntidadMinecraft> T buscar(Long id, Class<T> tipo, String descripcion) {
        if (id == null) {
            throw new EntidadInvalidaException("Falta el id de " + descripcion);
        }
        EntidadMinecraft entidad = entidades.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(descripcion, id));
        if (!tipo.isInstance(entidad)) {
            throw new EntidadInvalidaException("La entidad " + id + " es "
                    + entidad.tipoEntidad() + " y el escenario esperaba " + descripcion);
        }
        return tipo.cast(entidad);
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Las respuestas ajenas pueden ser largas; en la base guardo lo esencial. */
    private static String recortar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= 900 ? texto : texto.substring(0, 900) + "...";
    }
}
