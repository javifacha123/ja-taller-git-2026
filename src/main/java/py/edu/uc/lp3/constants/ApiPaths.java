package py.edu.uc.lp3.constants;

/** Rutas de la API REST centralizadas en un solo lugar (como en el template). */
public final class ApiPaths {

    private static final String BASE_API = "/api";

    /** CRUD de la familia EntidadMinecraft (Creeper, Aldeano, Enderman). */
    public static final String ENTIDADES = BASE_API + "/entidades";

    /** Lectura polimorfica por el protocolo Explosivo (Creeper + TNT). */
    public static final String EXPLOSIVOS = BASE_API + "/explosivos";

    /** Bloques de TNT. */
    public static final String TNT = BASE_API + "/tnt";

    /** Simulacion: cada POST crea una explosion; GET muestra el historial. */
    public static final String EXPLOSIONES = BASE_API + "/explosiones";

    private ApiPaths() {
    }
}
