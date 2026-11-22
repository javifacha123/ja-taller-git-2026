package py.edu.uc.lp3.constants;

/** Rutas de la API REST centralizadas en un solo lugar (como en el template). */
public final class ApiPaths {

    private static final String BASE_API = "/api";

    /** CRUD de la familia EntidadMinecraft (Creeper, Aldeano, Enderman). */
    public static final String ENTIDADES = BASE_API + "/entidades";

    /** Lectura polimórfica por el protocolo Explosivo (Creeper + TNT). */
    public static final String EXPLOSIVOS = BASE_API + "/explosivos";

    /** Bloques de TNT. */
    public static final String TNT = BASE_API + "/tnt";

    /** Simulación: cada POST crea una explosión; GET muestra el historial. */
    public static final String EXPLOSIONES = BASE_API + "/explosiones";

    /** POO-09: mis dos reglas publicadas, para que las consulte un compañero. */
    public static final String REGLAS = BASE_API + "/reglas";

    /** POO-09: regla 1, onda expansiva. */
    public static final String REGLA_ONDA = REGLAS + "/onda-expansiva";

    /** POO-09: regla 2, esquive del Enderman. */
    public static final String REGLA_ESQUIVE = REGLAS + "/esquive-enderman";

    /** POO-09: mi escenario, que consulta las reglas de A2 y de A3. */
    public static final String ESCENARIOS = BASE_API + "/escenarios";

    private ApiPaths() {
    }
}
