package py.edu.uc.lp3.rest.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ExplosionImposibleException;
import py.edu.uc.lp3.exceptions.MinecraftException;
import py.edu.uc.lp3.exceptions.ReglaRechazadaException;
import py.edu.uc.lp3.rest.dto.ErrorRespuesta;

/**
 * Traduce las excepciones del dominio a HTTP. Hay un handler por tipo:
 * Spring elige el @ExceptionHandler más específico para la clase lanzada,
 * así cada hija tiene su código de estado y su código en el JSON.
 */
@RestControllerAdvice
public class MinecraftExceptionHandler {

    /** Invariante de construcción o de un mensaje roto: el pedido trae valores ilegales. */
    @ExceptionHandler(EntidadInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> entidadInvalida(EntidadInvalidaException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.BAD_REQUEST, ex.getCodigo(), ex, ex.getMessage(), pedido);
    }

    /** El id no existe en la base. */
    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrada(EntidadNoEncontradaException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.NOT_FOUND, ex.getCodigo(), ex, ex.getMessage(), pedido);
    }

    /** Pedido bien formado, pero el estado del mundo no lo permite: conflicto. */
    @ExceptionHandler(ExplosionImposibleException.class)
    public ResponseEntity<ErrorRespuesta> explosionImposible(ExplosionImposibleException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.CONFLICT, ex.getCodigo(), ex, ex.getMessage(), pedido);
    }

    /** POO-09: una de mis dos reglas rechazó la jugada que me propusieron. */
    @ExceptionHandler(ReglaRechazadaException.class)
    public ResponseEntity<ErrorRespuesta> reglaRechazada(ReglaRechazadaException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.BAD_REQUEST, ex.getCodigo(), ex,
                "La regla '" + ex.getRegla() + "' rechazó la jugada: " + ex.getMessage(), pedido);
    }

    /** Red de seguridad: una hija futura sin handler propio. */
    @ExceptionHandler(MinecraftException.class)
    public ResponseEntity<ErrorRespuesta> otraDelDominio(MinecraftException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, ex.getCodigo(), ex, ex.getMessage(), pedido);
    }

    /** JSON mal escrito o "tipo" desconocido: no llega a ser un objeto del dominio. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> jsonIlegible(HttpMessageNotReadableException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.BAD_REQUEST, "JSON_INVALIDO", ex,
                "El cuerpo no es un JSON válido o el 'tipo' no es creeper, aldeano ni enderman", pedido);
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String codigo, Exception ex,
                                                     String mensaje, HttpServletRequest pedido) {
        ErrorRespuesta cuerpo = ErrorRespuesta.de(codigo, ex, estado.value(), mensaje, pedido.getRequestURI());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
