package py.edu.uc.lp3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POO-09: a quién consulta mi escenario.
 *
 * <p>Las URLs y las rutas de A2 y A3 viven en {@code application.properties} y
 * se pueden pisar con variables de entorno o con argumentos al arrancar, así
 * que cambiar de compañero o de puerto NO obliga a recompilar:</p>
 *
 * <pre>
 * ./mvnw spring-boot:run -Dspring-boot.run.arguments=--companeros.a2.base-url=http://localhost:8081
 * </pre>
 *
 * <p>Esto es lo que pide el enunciado cuando dice que la URL del compañero no
 * puede estar escrita a mano en el código.</p>
 */
@ConfigurationProperties(prefix = "companeros")
public class CompanerosProperties {

    private Companero a2 = new Companero();
    private Companero a3 = new Companero();

    public Companero getA2() {
        return a2;
    }

    public void setA2(Companero a2) {
        this.a2 = a2;
    }

    public Companero getA3() {
        return a3;
    }

    public void setA3(Companero a3) {
        this.a3 = a3;
    }

    /** Datos de un compañero al que le consulto una regla. */
    public static class Companero {

        /** Nombre de la persona, para los mensajes y el README. */
        private String nombre = "(sin asignar)";

        /** Usuario de GitHub, para el mapa del README. */
        private String github = "";

        /** Raíz de su servicio, por ejemplo http://localhost:8081 */
        private String baseUrl = "";

        /** Ruta de la regla que le consulto, por ejemplo /api/reglas/onda-expansiva */
        private String ruta = "";

        /** La regla en una frase, para citarla en la respuesta y en el README. */
        private String regla = "";

        /** Dónde está su documento OpenAPI. */
        private String openapi = "";

        /**
         * Cuerpo JSON que hay que mandarle, declarado como configuración.
         *
         * <p>Cada clave es un campo del JSON del compañero. El valor puede ser
         * un literal (un id en SU base, una coordenada fija) o un hecho de mi
         * jugada entre llaves dobles, por ejemplo {@code {{potencia}}}.</p>
         *
         * <p>Los hechos disponibles son: potencia, radio, distancia,
         * distanciaAldeano, distanciaEnderman, danio, bloques,
         * bloquesDisponibles, x, z, creeperId, aldeanoId y endermanId.</p>
         *
         * <p>Esto es lo que permite consultar a compañeros cuyas reglas usan
         * otro vocabulario sin tocar una línea de código: si un compañero
         * renombra un campo, se cambia una propiedad.</p>
         */
        private Map<String, String> cuerpo = new LinkedHashMap<>();

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String github) {
            this.github = github;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getRuta() {
            return ruta;
        }

        public void setRuta(String ruta) {
            this.ruta = ruta;
        }

        public String getRegla() {
            return regla;
        }

        public void setRegla(String regla) {
            this.regla = regla;
        }

        public String getOpenapi() {
            return openapi;
        }

        public void setOpenapi(String openapi) {
            this.openapi = openapi;
        }

        public Map<String, String> getCuerpo() {
            return cuerpo;
        }

        public void setCuerpo(Map<String, String> cuerpo) {
            this.cuerpo = cuerpo;
        }

        /** true si hay una URL y una ruta cargadas. */
        public boolean estaConfigurado() {
            return baseUrl != null && !baseUrl.isBlank()
                    && ruta != null && !ruta.isBlank();
        }

        /** URL completa del endpoint que se consulta. */
        public String urlCompleta() {
            return baseUrl + ruta;
        }
    }
}
