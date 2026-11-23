package py.edu.uc.lp3.service.impl;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import py.edu.uc.lp3.config.CompanerosProperties.Companero;
import py.edu.uc.lp3.exceptions.CompaneroInaccesibleException;
import py.edu.uc.lp3.exceptions.CompaneroRechazoException;
import py.edu.uc.lp3.service.CompaneroClient;

import java.time.Duration;
import java.util.Map;

/**
 * Implementación HTTP del cliente de compañeros.
 *
 * <p>Interpreta la respuesta por el CÓDIGO DE ESTADO, no por el cuerpo: 2xx es
 * aceptar y 4xx es rechazar. Es lo que hace el ejemplo del enunciado y tiene
 * una ventaja práctica: sirve con cualquier compañero sin importar cómo haya
 * armado su JSON, porque el contrato HTTP es el mismo para todos.</p>
 *
 * <p>Los tiempos de espera son cortos a propósito: si el servicio de un
 * compañero está apagado, el escenario tiene que fallar rápido y con un mensaje
 * claro, no quedarse colgado.</p>
 */
@Component
public class CompaneroClientHttp implements CompaneroClient {

    private static final Duration ESPERA_CONEXION = Duration.ofSeconds(3);
    private static final Duration ESPERA_RESPUESTA = Duration.ofSeconds(5);

    private final RestClient cliente;

    public CompaneroClientHttp() {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(ESPERA_CONEXION);
        fabrica.setReadTimeout(ESPERA_RESPUESTA);
        this.cliente = RestClient.builder().requestFactory(fabrica).build();
    }

    @Override
    public String consultar(Companero companero, String rol, Map<String, Object> cuerpo) {
        if (!companero.estaConfigurado()) {
            throw new CompaneroInaccesibleException(rol, companero.getNombre(),
                    "No hay una URL configurada para " + rol
                            + ". Cargar companeros." + rol.toLowerCase() + ".base-url y .ruta");
        }
        String url = companero.urlCompleta();
        ResponseEntity<String> respuesta;
        try {
            respuesta = cliente.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .onStatus(estado -> true, (pedido, resp) -> { })   // no lanzar por código
                    .toEntity(String.class);
        } catch (Exception ex) {
            throw new CompaneroInaccesibleException(rol, companero.getNombre(),
                    "No se pudo contactar la API de " + rol + " (" + companero.getNombre()
                            + ") en " + url + ": " + ex.getMessage());
        }
        String detalle = respuesta.getBody() == null ? "" : respuesta.getBody();
        if (respuesta.getStatusCode().is2xxSuccessful()) {
            return detalle;
        }
        if (respuesta.getStatusCode().is4xxClientError()) {
            throw new CompaneroRechazoException(rol, companero.getNombre(),
                    rol + " (" + companero.getNombre() + ") rechazó la jugada con "
                            + respuesta.getStatusCode().value() + ": " + detalle);
        }
        throw new CompaneroInaccesibleException(rol, companero.getNombre(),
                "La API de " + rol + " (" + companero.getNombre() + ") respondió "
                        + respuesta.getStatusCode().value() + ": " + detalle);
    }
}
