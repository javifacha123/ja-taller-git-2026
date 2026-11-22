package py.edu.uc.lp3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publica el documento OpenAPI 3 del servicio.
 *
 * <p>El documento queda en {@code /v3/api-docs} (JSON) y la interfaz para
 * recorrerlo en {@code /swagger-ui.html}. Un compañero puede importar esa URL
 * en Insomnia sin pedirme una sola clase Java.</p>
 *
 * <p>La descripción de abajo explica las DOS reglas del juego con su fórmula,
 * porque el enunciado de POO-09 pide que las reglas y la simulación se
 * entiendan desde el documento, no desde el código.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String DESCRIPCION = """
            Servicio HTTP del modelado de **Minecraft** (LP3 - UC 2026).

            Guarda su estado en una base H2 en archivo y expone el CRUD de la \
            familia `EntidadMinecraft` (Creeper, Aldeano, Enderman), los bloques \
            de TNT, una lectura polimórfica por la interface de dominio \
            `Explosivo` y la simulación de explosiones.

            ## Mis dos reglas del juego

            Las dos están publicadas como endpoints en `/api/reglas`, así otro \
            servicio puede consultarlas sin conocer mi código.

            **Regla 1 - Onda expansiva.** Cuando un explosivo detona, toda \
            entidad viva que esté a una distancia `d` menor o igual al radio \
            pierde vida según:

                danio = ceil(potencia * (radio + 1 - d) / (radio + 1))

            Cuanto más cerca del centro, más daño; en el borde del radio, el \
            mínimo. El explosivo se consume y no puede detonar dos veces.

            **Regla 2 - Esquive del Enderman.** Si la onda alcanza a un Enderman \
            y le quedan al menos `ceil(radio + 1 - d)` bloques de \
            teletransporte, los gasta, reaparece justo afuera del radio en la \
            dirección opuesta a la explosión y no recibe daño. Si no le \
            alcanzan los bloques, le toca la regla 1.

            Valores del dominio: el Creeper tiene potencia 12 y radio 3 \
            (cargado: 24 y 6). El bloque de TNT, potencia 16 y radio 4.

            ## Errores

            Cada excepción del dominio responde con su propio código en el JSON:

            | Código | HTTP | Cuándo |
            |---|---|---|
            | `ENTIDAD_INVALIDA` | 400 | un valor rompe un invariante (vida <= 0, nombre vacío, bloques negativos) |
            | `ENTIDAD_NO_ENCONTRADA` | 404 | el id no existe en la base |
            | `EXPLOSION_IMPOSIBLE` | 409 | el pedido es correcto, pero el explosivo ya detonó |
            | `REGLA_RECHAZADA` | 400 | una de mis dos reglas rechaza la jugada propuesta |
            | `COMPANERO_RECHAZO` | 409 | la API de A2 o de A3 rechazó el escenario (el JSON dice cuál) |
            | `COMPANERO_INACCESIBLE` | 502 | la API de A2 o de A3 no respondió |

            ## Consola de la base

            `/h2-console`, con JDBC URL `jdbc:h2:file:./data/minecraft-db` y \
            usuario `sa` sin contraseña.
            """;

    @Bean
    public OpenAPI minecraftOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Minecraft - Javier Atlasovich (LP3 UC 2026)")
                .version("POO-09")
                .description(DESCRIPCION)
                .contact(new Contact()
                        .name("Javier Atlasovich")
                        .url("https://github.com/javifacha123/ja-taller-git-2026"))
                .license(new License()
                        .name("Apache License 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
