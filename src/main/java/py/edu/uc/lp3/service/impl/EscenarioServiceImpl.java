package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.config.CompanerosProperties;
import py.edu.uc.lp3.config.CompanerosProperties.Companero;
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
import java.util.function.Supplier;

/**
 * POO-09: arma la jugada, la somete a las reglas de los dos compañeros y la
 * guarda solo si las dos la aceptan.
 *
 * <p>Orden de la operación:</p>
 * <ol>
 *   <li>Lee de mi base el Creeper, el Aldeano y el Enderman.</li>
 *   <li>Calcula con MIS reglas qué debería pasar: cuánto daño recibe el
 *       Aldeano (regla 1) y cuántos bloques necesita el Enderman (regla 2).</li>
 *   <li>Somete la jugada a los dos compañeros, cada uno por SUS reglas. El
 *       cuerpo que recibe cada uno se declara en {@code companeros.aN.cuerpo},
 *       porque sus reglas usan otro vocabulario que las mías.</li>
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
        // Potencia y radio: los del Creeper, salvo que el pedido proponga otros.
        int potencia = exigirPositivo(pedido.potenciaPropuesta(), creeper.potenciaExplosion(), "potenciaPropuesta");
        int radio = exigirPositivo(pedido.radioPropuesto(), creeper.radioExplosion(), "radioPropuesto");
        int danio = ReglaExplosion.danioPorDistancia(potencia, radio, distanciaAldeano);
        int bloques = ReglaExplosion.bloquesNecesarios(radio, distanciaEnderman);

        // Los hechos de la jugada, con el nombre por el que los conoce cada
        // plantilla de cuerpo configurada.
        Map<String, Object> hechos = new LinkedHashMap<>();
        hechos.put("potencia", potencia);
        hechos.put("radio", radio);
        hechos.put("distancia", redondear(distanciaAldeano));
        hechos.put("distanciaAldeano", redondear(distanciaAldeano));
        hechos.put("distanciaEnderman", redondear(distanciaEnderman));
        hechos.put("danio", danio);
        hechos.put("bloques", bloques);
        hechos.put("bloquesDisponibles", enderman.getBloquesTeletransporte());
        hechos.put("x", creeper.getX());
        hechos.put("z", creeper.getZ());
        hechos.put("creeperId", creeper.getId());
        hechos.put("aldeanoId", aldeano.getId());
        hechos.put("endermanId", enderman.getId());

        // Cada compañero recibe el cuerpo que declara su plantilla configurada.
        String veredictoA2 = companeroClient.consultar(companeros.getA2(), "A2",
                cuerpoPara(companeros.getA2(), hechos,
                        () -> cuerpoOnda(potencia, radio, distanciaAldeano, danio)));
        String veredictoA3 = companeroClient.consultar(companeros.getA3(), "A3",
                cuerpoPara(companeros.getA3(), hechos,
                        () -> cuerpoEsquive(potencia, radio, distanciaEnderman,
                                enderman.getBloquesTeletransporte())));

        // Solo se llega acá si NINGUNO lanzó: las dos aceptaron.
        String nombre = pedido.nombre() == null || pedido.nombre().isBlank()
                ? "Jugada sin nombre"
                : pedido.nombre();
        Jugada jugada = new Jugada(nombre, creeper, aldeano, enderman, potencia, radio, danio, bloques);
        jugada.registrarVeredictos(
                companeros.getA2().getNombre(), recortar(veredictoA2),
                companeros.getA3().getNombre(), recortar(veredictoA3));
        return jugadas.save(jugada);
    }

    /**
     * Arma el cuerpo que se le manda a un compañero.
     *
     * <p>Si el compañero tiene una plantilla configurada en
     * {@code companeros.aN.cuerpo}, se usa esa: cada valor puede ser un literal
     * (un id en SU base, una coordenada fija) o un hecho de mi jugada entre
     * llaves dobles, como {@code {{potencia}}}. Si no hay plantilla, se cae al
     * cuerpo por defecto, pensado para un compañero con reglas como las mías.</p>
     *
     * <p>Gracias a esto, consultar a alguien cuyas reglas usan otro vocabulario
     * no requiere tocar código: se declara el mapeo en una propiedad.</p>
     */
    private Map<String, Object> cuerpoPara(Companero companero, Map<String, Object> hechos,
                                           Supplier<Map<String, Object>> porDefecto) {
        Map<String, String> plantilla = companero.getCuerpo();
        if (plantilla == null || plantilla.isEmpty()) {
            return porDefecto.get();
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        for (Map.Entry<String, String> campo : plantilla.entrySet()) {
            cuerpo.put(campo.getKey(), resolver(campo.getValue(), hechos, companero));
        }
        return cuerpo;
    }

    /** Un valor de la plantilla: {{hecho}} se reemplaza; lo demás es literal. */
    private Object resolver(String valor, Map<String, Object> hechos, Companero companero) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.startsWith("{{") && texto.endsWith("}}")) {
            String hecho = texto.substring(2, texto.length() - 2).trim();
            Object encontrado = hechos.get(hecho);
            if (encontrado == null) {
                throw new EntidadInvalidaException("La plantilla de " + companero.getNombre()
                        + " pide el hecho '" + hecho + "', que el escenario no calcula. "
                        + "Los disponibles son: " + hechos.keySet());
            }
            return encontrado;
        }
        return comoNumeroSiSePuede(texto);
    }

    /** Un literal numérico viaja como número en el JSON, no como texto. */
    private static Object comoNumeroSiSePuede(String texto) {
        try {
            if (texto.contains(".")) {
                return Double.valueOf(texto);
            }
            return Long.valueOf(texto);
        } catch (NumberFormatException noEraNumero) {
            return texto;
        }
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

    /** Usa el valor propuesto si vino, y si no el del Creeper. Siempre > 0. */
    private static int exigirPositivo(Integer propuesto, int porDefecto, String campo) {
        if (propuesto == null) {
            return porDefecto;
        }
        if (propuesto <= 0) {
            throw new EntidadInvalidaException("'" + campo + "' debe ser mayor que 0");
        }
        return propuesto;
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
