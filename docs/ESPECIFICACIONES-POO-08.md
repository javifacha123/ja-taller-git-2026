# Especificaciones POO-08 — CRUD REST, H2 y explosiones en Minecraft

**Alumno:** Javier Atlasovich · **Asignatura:** Lenguaje de Programación 3 (CYT646)
**Repositorio:** <https://github.com/javifacha123/ja-taller-git-2026>
**Commit de la solución:** <https://github.com/javifacha123/ja-taller-git-2026/commit/84d8224b0348d1c94effd562c4d3892d77f818c5>

## 1. Objetivo

Publicar un servicio HTTP con Spring Boot 4 sobre mi modelado de **Minecraft** que guarda su estado en **H2** y permite:

- crear, leer, actualizar y borrar entidades de la familia `EntidadMinecraft` (`Creeper`, `Aldeano`, `Enderman`);
- simular **explosiones** de un `Creeper` o de un bloque de TNT, aplicando dos reglas del juego y guardando el resultado;
- consultar los explosivos a través de la interface de dominio `Explosivo`;
- recibir un error JSON distinto para cada tipo de excepción del dominio.

Arranque: `./mvnw spring-boot:run` (puerto 8080). Consola H2: `/h2-console`, JDBC URL `jdbc:h2:file:./data/minecraft-db`, usuario `sa`, sin contraseña.

## 2. Clases que intervienen

| Clase | Rol |
|---|---|
| `EntidadMinecraft` (abstracta, `@Entity`, `SINGLE_TABLE`) | Padre de la familia: nombre, vida, posición `x`/`z`; método abstracto `moverse()`; regla 1 en `recibirExplosion`. |
| `Creeper` | Hija; implementa `Explosivo`; puede estar `cargado` (doble potencia y radio). |
| `Aldeano` | Hija; recibe el daño de las explosiones. |
| `Enderman` | Hija; sobreescribe `recibirExplosion` con la regla 2 (esquive por teletransporte). |
| `Explosivo` (interface) | Protocolo implementado por `Creeper` y `BloqueTnt` (familias distintas). |
| `BloqueTnt` | Segundo explosivo, persistido en su propia tabla. |
| `Explosion` + `Impacto` | Registro persistido de cada simulación y de lo que le pasó a cada entidad. |
| `MinecraftException` + 3 hijas | `EntidadInvalidaException` (400), `EntidadNoEncontradaException` (404), `ExplosionImposibleException` (409). |

## 3. URLs

| Método | URL | Cuerpo | Respuesta |
|---|---|---|---|
| GET | `/` | — | 200 texto |
| POST | `/api/entidades` | `{"tipo":"creeper","nombre":"Creepy","vida":20,"x":0,"z":0}` | 201 + entidad con `id` |
| GET | `/api/entidades` (`?nombre=`, `?soloVivas=true`) | — | 200 lista |
| GET | `/api/entidades/{id}` | — | 200 / 404 |
| PATCH | `/api/entidades/{id}` | `{"nombre":"...","x":1,"z":1,"bloquesTeletransporte":5}` (todos opcionales) | 200 / 400 / 404 |
| DELETE | `/api/entidades/{id}` | — | 204 / 404 |
| POST | `/api/tnt` | `{"x":3,"z":0}` | 201 |
| GET | `/api/tnt`, `/api/tnt/{id}` | — | 200 / 404 |
| GET | `/api/explosivos` (`?disponibles=true`) | — | 200 lista de `Explosivo` |
| POST | `/api/explosiones` | `{"tipoExplosivo":"creeper","explosivoId":1}` | 201 / 400 / 404 / 409 |
| GET | `/api/explosiones`, `/api/explosiones/{id}` | — | 200 / 404 |

## 4. Invariantes

1. Toda entidad tiene **nombre no vacío** y **vida inicial > 0** (lo valida cada constructor).
2. La **vida nunca baja de 0** y solo cambia por mensajes del juego; un PATCH con `vida` se rechaza (400).
3. Los **bloques de teletransporte** del Enderman nunca son negativos.
4. Una entidad **sin vida no puede moverse**.
5. Un explosivo **detona una sola vez**: un Creeper explotado o muerto, o una TNT ya detonada, lanzan `ExplosionImposibleException` (409) y la base no cambia.
6. Un pedido ilegal **no deja basura**: la excepción se lanza antes de guardar; la simulación corre en una transacción que se revierte completa.
7. El `id` lo asigna la base; el JSON de creación no lo trae.

## 5. Reglas del juego

1. **Onda expansiva:** al detonar un explosivo, cada entidad viva a distancia `d ≤ radio` pierde `ceil(potencia × (radio + 1 − d) / (radio + 1))` de vida, y el explosivo se consume.
2. **Esquive del Enderman:** si la onda alcanza a un Enderman y tiene al menos `ceil(radio + 1 − d)` bloques de teletransporte, los gasta, aparece justo fuera del radio y no recibe daño; si no, recibe el daño de la regla 1.

Creeper: potencia 12, radio 3 (cargado: 24 y 6). TNT: potencia 16, radio 4.

## 6. Cómo probarlo

1. Detener la app si estaba corriendo, borrar la carpeta `data/` y ejecutar `./mvnw spring-boot:run`.
2. En Insomnia: *Import* → archivo `insomnia/ja-minecraft-poo08.insomnia.json`.
3. Recorrer las carpetas en orden:
   - `00` `GET /` → 200.
   - `01` crear Creeper (1), Aldeano (2), Enderman (3) y Aldeano lejano (4) → 201; listar; buscar por id; PATCH al Aldeano (nombre y `z=1`) y volver a leerlo; DELETE del 4 → 204 y su GET → 404.
   - `02` POST con `vida: -5` → 400 `ENTIDAD_INVALIDA`, y la lista no creció; GET/DELETE de id 9999 → 404 `ENTIDAD_NO_ENCONTRADA`.
   - `03` colocar TNT en (3,0) y `GET /api/explosivos` → Creeper y TNT juntos.
   - `04` explota el Creeper → Aldeano vida 12, Enderman esquiva y queda en (4,0) con 6 bloques, Creeper vida 0; explota la TNT → Aldeano vida 3, Enderman en (8,0) con 2 bloques.
   - `05` detonar otra vez el Creeper o la TNT → 409 `EXPLOSION_IMPOSIBLE`.
4. En `/h2-console` ejecutar `SELECT * FROM ENTIDAD_MINECRAFT;` y `SELECT * FROM EXPLOSION_IMPACTO;` para ver el estado guardado.
