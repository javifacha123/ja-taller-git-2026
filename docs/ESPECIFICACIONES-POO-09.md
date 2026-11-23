# POO-09 — APIs cruzadas aplicado a mi dominio (Minecraft)

**Alumno:** Javier Atlasovich · **Usuario de GitHub:** `javifacha123`
**Repositorio:** <https://github.com/javifacha123/ja-taller-git-2026>
**Enlace al commit de la solución:** _(pendiente: pegar el enlace exacto al commit)_
**Trabajo individual.** El grupo sirvió solo para acordar a quién se consulta.

---

## 1. Objetivo

Publicar el **OpenAPI** de mi servicio de Minecraft, **consumir** las APIs de dos
compañeros del mismo juego, y exponer un **escenario propio** que consulta las
reglas de los dos y guarda la jugada en mi base H2 **solo si ambos la aceptan**.
Si alguno rechaza, la base queda igual y la respuesta indica de quién fue el
rechazo. Además se cierran las observaciones de la corrección de POO-08.

## 2. Quiénes son A1, A2 y A3

El documento de grupos establece que los que quedan de a dos forman un **dúo**:
cada uno consulta a su compañero y pide el segundo servicio a alguien de un
trío del mismo juego, que les deja usar su OpenAPI.

| Rol | Quién | GitHub | Puerto | OpenAPI |
|---|---|---|---|---|
| **A1** (yo) | Javier Atlasovich | `javifacha123` | 8080 | `http://localhost:8080/v3/api-docs` |
| **A2** | _(pendiente de confirmar)_ | — | 8081 | `http://localhost:8081/v3/api-docs` |
| **A3** | _(pendiente de confirmar)_ | — | 8082 | `http://localhost:8082/v3/api-docs` |

## 3. El escenario, en una frase

> Un Creeper detona y la onda alcanza a un Aldeano y a un Enderman: la jugada se
> guarda **solo si A2 acepta el daño que recibe el Aldeano y A3 acepta que el
> Enderman puede esquivar.**

## 4. URLs

### Mías, publicadas para que me consulten

| Método | URL | Qué hace |
|---|---|---|
| GET | `/v3/api-docs` | Mi documento OpenAPI 3.1 |
| GET | `/swagger-ui.html` | El mismo documento, navegable |
| POST | `/api/reglas/onda-expansiva` | **Regla 1**: cuánto daño corresponde a esa distancia |
| POST | `/api/reglas/esquive-enderman` | **Regla 2**: si al Enderman le alcanzan los bloques |
| POST | `/api/escenarios` | Mi escenario cruzado |
| GET | `/api/escenarios` | Historial de jugadas aceptadas |

Las dos reglas responden **200** al aceptar y **400 `REGLA_RECHAZADA`** al
rechazar, igual que el ejemplo del enunciado.

### Las que consulto

| Rol | Regla citada | Endpoint |
|---|---|---|
| A2 | _(pendiente)_ | _(pendiente)_ |
| A3 | _(pendiente)_ | _(pendiente)_ |

Las URLs **no están escritas a mano en el código**: se configuran con el
prefijo `companeros` en `application.properties` y se pueden pisar al arrancar.

## 5. Invariantes

1. **La jugada se guarda solo si los dos aceptan.** `EscenarioServiceImpl.jugar`
   es `@Transactional`: si A3 rechaza después de que A2 aceptó, la excepción
   corta el método antes del `save` y Spring revierte la transacción. La
   atomicidad la da la transacción, no un borrado manual.
2. **Un rechazo no cambia la base.** El historial de `/api/escenarios` solo
   contiene jugadas aprobadas por los dos.
3. **El rechazo siempre se atribuye.** `CompaneroRechazoException` guarda el rol
   (A2 o A3) y el nombre, y el handler lo dice en el JSON.
4. **«Me dijeron que no» y «no me contestaron» son distintos.**
   `CompaneroRechazoException` → 409; `CompaneroInaccesibleException` → 502.
5. **Las entidades del escenario tienen que existir y ser del tipo correcto.**
   El escenario no las crea: 404 si no existen, 400 si el tipo no corresponde.
6. **Las dos reglas tienen una sola implementación.** Las fórmulas son métodos
   estáticos de `ReglaExplosion`, usados tanto por la simulación interna como
   por los endpoints que consulta un compañero. Si una regla cambia, se toca en
   un solo lugar.

## 6. Cómo probarlo

### Arrancar los tres servicios

```bash
# A1 (este repositorio)
./mvnw spring-boot:run

# A2
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# A3
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

### Recorrer la colección de Insomnia

`insomnia/ja-minecraft-poo09.insomnia.json` → *Import* → *File*. Trae el
escenario feliz, **un rechazo por compañero**, los errores del escenario y, en
la descripción de cada pedido, la respuesta verificada.

### Prueba rápida por línea de comandos

```bash
A=localhost:8080
# Las tres entidades del escenario
curl -s -X POST $A/api/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"creeper","nombre":"Creepy","vida":20,"x":0,"z":0}'
curl -s -X POST $A/api/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"aldeano","nombre":"Testificate","vida":20,"x":1,"z":1}'
curl -s -X POST $A/api/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"enderman","nombre":"Slender","vida":40,"bloquesTeletransporte":8,"x":2,"z":0}'

# Escenario feliz: 201, jugada guardada
curl -s -X POST $A/api/escenarios -H 'Content-Type: application/json' \
  -d '{"nombre":"Emboscada en la aldea","creeperId":1,"aldeanoId":2,"endermanId":3}'

# El historial muestra la jugada con los dos veredictos
curl -s $A/api/escenarios
```

### Probarlo sin los compañeros

El circuito completo se verifica apuntando A2 y A3 a mi propio servicio, porque
mis endpoints de reglas aceptan los campos que el escenario envía:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8099 \
  --spring.datasource.url=jdbc:h2:mem:prueba \
  --companeros.a2.base-url=http://localhost:8099 \
  --companeros.a2.ruta=/api/reglas/onda-expansiva \
  --companeros.a3.base-url=http://localhost:8099 \
  --companeros.a3.ruta=/api/reglas/esquive-enderman"
```

Resultados obtenidos con esa configuración:

| Prueba | Resultado |
|---|---|
| Escenario feliz | 201, jugada guardada con 8 de daño y 2 bloques, con los dos veredictos |
| Rechazo de A2 (Aldeano a 14,14 del Creeper, radio 3) | 409 `COMPANERO_RECHAZO`, atribuido a **A2** |
| Rechazo de A3 (Enderman con 1 bloque, necesita 2) | 409 `COMPANERO_RECHAZO`, atribuido a **A3** |
| Historial tras los dos rechazos | **una sola** jugada |

## 7. Situación de POO-08

Las tres observaciones de la corrección están cerradas. La tabla completa, con
qué decía cada una y dónde verla en el código, está en el README bajo
«Situación de POO-08».

| # | Observación | Estado |
|---|---|---|
| 1 | Faltaba la capa de servicios en el CRUD | **Cerrada**: cuatro contratos nuevos con sus implementaciones; ningún controller toca un repositorio |
| 2 | El dominio lanzaba excepciones y tenía lógica de negocio | **Cerrada**: cero `throw` en `domain/` y en `rest/dto/`; validaciones y reglas en la capa de servicios |
| 3 | Documentación (README inexacto y errores de ortografía) | **Cerrada**: se corrigió el código para que el README sea exacto y se barrieron las tildes del repositorio |

La refactorización **no cambió el comportamiento observable**: se verificó
reproduciendo el escenario que el docente había probado y los números
coincidieron.

## 8. Dónde está cada cosa

| Entregable | Dónde |
|---|---|
| Código | <https://github.com/javifacha123/ja-taller-git-2026> |
| README al día | `README.md` |
| Bitácora de IA | `BITACORA.md` |
| Colección de Insomnia | `insomnia/ja-minecraft-poo09.insomnia.json` |
| Este documento | `docs/ESPECIFICACIONES-POO-09.md` |
