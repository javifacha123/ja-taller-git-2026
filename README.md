# ja-taller-git-2026

## Taller Git 2026 - Minecraft

Servicio HTTP con Spring Boot (Java 21, Maven) que modela entidades de Minecraft con herencia, sobreescritura, sobrecarga, polimorfismo y ocultamiento de información. Desde el ejercicio **POO-08** el servicio guarda su estado en una base **H2**, expone un **CRUD REST** de la familia `EntidadMinecraft`, simula **explosiones** con dos reglas del juego y traduce una **jerarquía de excepciones** del dominio a respuestas HTTP.

**Licencia:** Apache License 2.0 (ver archivo `LICENSE`).

---

## Cómo arrancar

Requisito: Java 21. No hace falta instalar Maven; el proyecto trae el *wrapper*.

~~~bash
./mvnw spring-boot:run
~~~

- API: <http://localhost:8080/> responde `API Minecraft funcionando correctamente`.
- Consola H2: <http://localhost:8080/h2-console>. En el formulario usar **JDBC URL** `jdbc:h2:file:./data/minecraft-db`, usuario `sa`, contraseña vacía. Ahí se ven las tablas `ENTIDAD_MINECRAFT`, `BLOQUE_TNT`, `EXPLOSION` y `EXPLOSION_IMPACTO`.
- La base vive en la carpeta `data/` (ignorada por Git). **Para empezar de cero** (por ejemplo, antes de recorrer la colección de Insomnia) se detiene la aplicación y se borra esa carpeta.

## Propiedades de H2 (`src/main/resources/application.properties`)

| Propiedad | Valor | Qué hace |
|---|---|---|
| `spring.datasource.url` | `jdbc:h2:file:./data/minecraft-db;AUTO_SERVER=TRUE` | Guarda la base en un archivo dentro de `data/`, así el estado sobrevive a un reinicio; `AUTO_SERVER` deja que la consola abra el archivo mientras la app corre. |
| `spring.datasource.username` | `sa` | Usuario de la base (el administrador por defecto de H2). |
| `spring.datasource.password` | *(vacío)* | Sin contraseña, alcanza para el perfil de desarrollo. |
| `spring.datasource.driver-class-name` | `org.h2.Driver` | Driver JDBC que habla con H2. |
| `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate crea las tablas que faltan a partir de las entidades y **no** borra las filas existentes al arrancar. |
| `spring.jpa.show-sql` | `true` | Imprime en la consola cada SQL que genera Hibernate. |
| `spring.jpa.properties.hibernate.format_sql` | `true` | Ese SQL sale indentado y legible. |
| `spring.jpa.open-in-view` | `false` | La sesión de JPA se cierra al terminar la operación del repositorio o la transacción, no al terminar de escribir el JSON. |
| `spring.h2.console.enabled` | `true` | Habilita la consola web de H2. |
| `spring.h2.console.path` | `/h2-console` | Ruta donde se publica la consola. |

Dependencias agregadas en `pom.xml`: `spring-boot-starter-data-jpa`, `com.h2database:h2` (runtime) y `spring-boot-h2console` (en Spring Boot 4 la consola de H2 es un módulo aparte).

## Estructura de paquetes

~~~
py.edu.uc.lp3
├── Application
├── constants        ApiPaths (todas las rutas de la API)
├── domain           EntidadMinecraft, Creeper, Aldeano, Enderman  (familia persistida)
│                    Explosivo (interface), BloqueTnt, Explosion, Impacto
├── exceptions       MinecraftException + EntidadInvalidaException,
│                    EntidadNoEncontradaException, ExplosionImposibleException
├── repository       EntidadMinecraftRepository, CreeperRepository,
│                    BloqueTntRepository, ExplosionRepository   (solo interfaces)
├── service          SimulacionService (interface)
│   └── impl         SimulacionServiceImpl (transacción de la simulación)
└── rest
    ├── controller   IndexController, EntidadController, ExplosivoController,
    │                TntController, ExplosionController, MinecraftController (POO-06)
    ├── dto          EntidadRequest (+ CreeperRequest, AldeanoRequest, EndermanRequest),
    │                EntidadPatch, TntRequest, SimulacionRequest, ExplosivoResumen, ErrorRespuesta
    └── handler      MinecraftExceptionHandler (un @ExceptionHandler por tipo)
~~~

## Diagrama de clases

~~~mermaid
classDiagram
    direction TB

    %% ---------- Familia persistida (herencia) ----------
    class EntidadMinecraft {
        <<abstract>>
        -Long id
        -String nombre
        -int vida
        -int x
        -int z
        #EntidadMinecraft()
        +EntidadMinecraft(String nombre)
        +EntidadMinecraft(String nombre, int vida)
        +EntidadMinecraft(String nombre, int vida, int x, int z)
        +moverse() String*
        +recibirDanio(int dano)
        +recibirDanio(int dano, boolean conArmadura)
        +recibirExplosion(Explosivo fuente) Impacto
        +renombrar(String nombre)
        +moverA(int x, int z)
        +estaVivo() boolean
        +desaparecer()
    }
    class Creeper {
        -Boolean cargado
        -Boolean explotado
        +Creeper(String nombre)
        +Creeper(String nombre, int vida)
        +Creeper(String nombre, int vida, int x, int z)
        +Creeper(String nombre, int vida, int x, int z, boolean cargado)
        +moverse() String
        +detonar()
    }
    class Aldeano {
        +Aldeano(String nombre)
        +Aldeano(String nombre, int vida)
        +Aldeano(String nombre, int vida, int x, int z)
        +moverse() String
    }
    class Enderman {
        -Integer bloquesTeletransporte
        +Enderman(String nombre)
        +Enderman(String nombre, int vida)
        +Enderman(String nombre, int vida, int bloques)
        +Enderman(String nombre, int vida, int bloques, int x, int z)
        +moverse() String
        +recibirExplosion(Explosivo fuente) Impacto
    }
    EntidadMinecraft <|-- Creeper
    EntidadMinecraft <|-- Aldeano
    EntidadMinecraft <|-- Enderman

    %% ---------- Protocolo de dominio (implementación) ----------
    class Explosivo {
        <<interface>>
        +potenciaExplosion() int
        +radioExplosion() int
        +puedeExplotar() boolean
        +detonar()
        +nombreExplosivo() String
        +tipoExplosivo() String
    }
    class BloqueTnt {
        <<entity>>
        -Long id
        -int x
        -int z
        -boolean detonado
        +BloqueTnt(int x, int z)
    }
    Explosivo <|.. Creeper
    Explosivo <|.. BloqueTnt

    class Explosion {
        <<entity>>
        +provocar(Explosivo fuente, List entidades) Explosion$
    }
    class Impacto {
        <<embeddable>>
    }
    Explosion *-- Impacto : impactos
    Explosion ..> Explosivo : detona
    Explosion ..> EntidadMinecraft : recibirExplosion

    %% ---------- Repositorios (uso) ----------
    class EntidadMinecraftRepository {
        <<interface>>
        +findByVidaGreaterThan(int vida) List
        +findByNombreContainingIgnoreCase(String nombre) List
    }
    class JpaRepository~T, ID~ {
        <<interface>>
    }
    JpaRepository <|-- EntidadMinecraftRepository

    class EntidadController
    class ExplosivoController
    class ExplosionController
    class SimulacionService {
        <<interface>>
        +explotar(String tipo, Long id) Explosion
    }
    EntidadController --> EntidadMinecraftRepository : usa
    EntidadController ..> EntidadMinecraft : mensajes
    ExplosivoController ..> Explosivo : lista por la interface
    ExplosionController --> SimulacionService : usa
    SimulacionService ..> Explosion : provocar

    %% ---------- Jerarquía de excepciones ----------
    class MinecraftException {
        <<abstract>>
        +getCodigo() String*
    }
    class EntidadInvalidaException
    class EntidadNoEncontradaException
    class ExplosionImposibleException
    MinecraftException <|-- EntidadInvalidaException
    MinecraftException <|-- EntidadNoEncontradaException
    MinecraftException <|-- ExplosionImposibleException
    class MinecraftExceptionHandler {
        <<RestControllerAdvice>>
    }
    MinecraftExceptionHandler ..> MinecraftException : un handler por tipo
~~~

**Cómo leer el diagrama**

- **Herencia** (`<|--`, flecha llena): `Creeper`, `Aldeano` y `Enderman` *son una* `EntidadMinecraft`; `EntidadMinecraftRepository` *es un* `JpaRepository`.
- **Implementación de interface** (`<|..`, flecha punteada con triángulo): `Creeper` y `BloqueTnt` cumplen el protocolo `Explosivo` aunque son de familias distintas.
- **Uso del repositorio** (`-->` "usa"): el controller depende de la interfaz `EntidadMinecraftRepository`, nunca de una clase concreta; Spring inyecta la implementación generada.
- **Jerarquía de excepciones** (abajo): `MinecraftException` es la base abstracta y no comprobada; las tres hijas nombran la regla que falló y el handler atiende cada una por separado.

## Endpoints

| Método | URL | Qué hace | Éxito |
|---|---|---|---|
| GET | `/` | Confirma que la aplicación vive | 200 |
| POST | `/api/entidades` | Crea un Creeper, Aldeano o Enderman (campo `tipo`) | 201 + `id` + `Location` |
| GET | `/api/entidades` | Lista lo persistido (`?nombre=` o `?soloVivas=true` opcionales) | 200 |
| GET | `/api/entidades/{id}` | Devuelve una entidad | 200 / 404 |
| PATCH | `/api/entidades/{id}` | Cambia `nombre`, `x`, `z` o `bloquesTeletransporte` | 200 / 400 / 404 |
| DELETE | `/api/entidades/{id}` | La entidad hace *despawn* (se borra la fila) | 204 / 404 |
| POST | `/api/tnt` | Coloca un bloque de TNT en `x`, `z` | 201 |
| GET | `/api/tnt`, `/api/tnt/{id}` | Lista o busca TNT | 200 / 404 |
| GET | `/api/explosivos` | **Endpoint de la interface**: Creepers y TNT como `Explosivo` (`?disponibles=true`) | 200 |
| POST | `/api/explosiones` | **Simulación**: detona `{tipoExplosivo, explosivoId}` | 201 / 400 / 404 / 409 |
| GET | `/api/explosiones`, `/api/explosiones/{id}` | Historial persistido de explosiones | 200 / 404 |
| GET | `/minecraft/entidad?...` | Endpoint de POO-06 (no persiste), se conserva | 200 / 400 |

Ejemplo de creación (el subtipo se elige con `tipo`):

~~~json
{ "tipo": "enderman", "nombre": "Slender", "vida": 40, "bloquesTeletransporte": 8, "x": 2, "z": 0 }
~~~

Ejemplo de JSON de error (igual para todos los handlers):

~~~json
{ "error": "ENTIDAD_INVALIDA", "excepcion": "EntidadInvalidaException", "estado": 400,
  "mensaje": "La vida inicial debe ser mayor que 0", "ruta": "/api/entidades", "momento": "2026-11-08T15:40:12-03:00" }
~~~

## Las dos reglas del juego

Participan tres clases de la familia: **`Creeper`** (explota), **`Aldeano`** (recibe el daño) y **`Enderman`** (esquiva). La `BloqueTnt` es una segunda fuente de explosión gracias a la interface `Explosivo`.

1. **Onda expansiva** (`EntidadMinecraft.recibirExplosion`): cuando un `Explosivo` detona, toda entidad viva a una distancia `d` menor o igual al radio recibe `ceil(potencia × (radio + 1 − d) / (radio + 1))` de daño y el explosivo se consume (el Creeper queda con vida 0 y `explotado = true`).
2. **Esquive del Enderman** (`Enderman.recibirExplosion`, sobreescribe la regla 1): si la onda alcanza a un Enderman y le quedan al menos `ceil(radio + 1 − d)` bloques de teletransporte, los gasta, aparece justo afuera del radio y no recibe daño; si no le alcanzan, recibe el daño normal.

| Explosivo | Potencia | Radio |
|---|---|---|
| Creeper | 12 | 3 |
| Creeper cargado | 24 | 6 |
| TNT | 16 | 4 |

**Resultado esperado con la colección de Insomnia** (base vacía):

| Paso | Creepy (Creeper, 0,0) | Testificate (Aldeano, 1,1) | Slender (Enderman, 2,0) |
|---|---|---|---|
| Antes | vida 20 | vida 20 | vida 40, 8 bloques |
| Explota el Creeper | vida 0, `explotado` | d = 1,41 → 8 de daño, vida 12 | d = 2 → gasta 2 bloques, salta a (4,0), vida 40 |
| Explota la TNT en (3,0) | sin cambios (ya no está vivo) | d = 2,24 → 9 de daño, vida 3 | d = 1 → gasta 4 bloques, salta a (8,0), vida 40 |
| Explota de nuevo el Creeper | 409 `EXPLOSION_IMPOSIBLE` | sin cambios | sin cambios |

## Qué cambió con la sobrecarga y con la sobreescritura

**Sobrecarga** (misma clase, mismo nombre, otra lista de argumentos):

- Constructores: `EntidadMinecraft`, `Creeper` y `Aldeano` tienen `(nombre)`, `(nombre, vida)` y, desde POO-08, `(nombre, vida, x, z)`. `Creeper` suma `(nombre, vida, x, z, cargado)`. `Enderman` tiene cuatro: `(nombre)`, `(nombre, vida)`, `(nombre, vida, bloques)` y `(nombre, vida, bloques, x, z)`. Los simples delegan con `this(...)` y las hijas llaman a `super(...)`. Todos validan, así que el objeto nunca queda en un estado ilegal. El DTO de cada subtipo elige el constructor según los campos que vienen en el JSON.
- Mensaje del dominio: `recibirDanio(int dano)` y `recibirDanio(int dano, boolean conArmadura)`.

**Sobreescritura** (clase hija, misma firma, implementación propia):

- `moverse()` sigue siendo abstracto en `EntidadMinecraft` y lo sobreescriben `Creeper`, `Aldeano` y `Enderman`.
- `recibirExplosion(Explosivo)` está implementado en `EntidadMinecraft` (regla 1) y `Enderman` lo sobreescribe (regla 2). `Explosion.provocar` recorre una `List<EntidadMinecraft>` y el despacho dinámico decide cuál se ejecuta.
- `Creeper` y `BloqueTnt` implementan los métodos de `Explosivo` cada uno a su manera.

**Cómo se distinguen:** la sobrecarga se resuelve en tiempo de compilación según los argumentos y ocurre dentro de una misma clase. La sobreescritura se resuelve en ejecución según el tipo real del objeto y ocurre entre una clase padre y sus hijas (o entre una interface y sus implementaciones).

## Ocultamiento frente al JSON y frente a JPA

- No hay setters públicos de `vida`, `x`, `z` ni `nombre`. Cambian solo por mensajes: `recibirDanio`, `recibirExplosion`, `desaparecer`, `moverA`, `renombrar`. El único "setter", `Enderman.setBloquesTeletransporte`, valida igual que el constructor.
- **JSON → objeto:** Jackson nunca arma una entidad. Recibe un *record* DTO y ese DTO llama al constructor, que es quien valida. Un `POST` con `vida: -5` lanza la excepción antes de `repositorio.save`, así que la tabla no cambia.
- **Fila → objeto:** JPA necesita un constructor sin argumentos; está declarado `protected`, así que solo lo ven el paquete `domain` y las clases hijas; ni el controller ni los DTO pueden fabricar un objeto vacío. JPA lee y escribe los campos por reflexión (el `@Id` está en un campo), sin pasar por setters.

## Qué hay que pensar (respuestas)

**Recurso y verbo.** El sustantivo es la **entidad** del mundo: `/api/entidades` es la colección y `/api/entidades/{id}` un ítem. El verbo HTTP dice la operación (POST crear, GET leer, PATCH actualizar, DELETE borrar); la URL nunca lleva verbos como `/crear`. La simulación también es un sustantivo: `/api/explosiones`.

**Identidad.** La clave es `id` (`Long`). La asigna la base con `GenerationType.IDENTITY` al insertar. El JSON de creación **no** la lleva (y si la lleva, se ignora: el DTO no tiene ese campo); la respuesta 201 sí la devuelve, junto con la cabecera `Location`.

**Herencia y tablas.** Elegí `SINGLE_TABLE`: una sola tabla `ENTIDAD_MINECRAFT` con una columna `TIPO_ENTIDAD` (`CREEPER`, `ALDEANO`, `ENDERMAN`). En mi dominio las hijas comparten casi todo (nombre, vida, posición) y solo agregan una o dos columnas, y la simulación necesita traer **todas** las entidades vivas de una vez sin *joins*. El costo es que las columnas propias de una hija (`CARGADO`, `EXPLOTADO`, `BLOQUES_TELETRANSPORTE`) quedan en NULL para las demás, por eso en Java son `Boolean`/`Integer`. En el JSON el subtipo se distingue con la propiedad `"tipo"` (`@JsonTypeInfo` + `@JsonSubTypes`), tanto al crear (en el DTO) como al listar (en la entidad).

**Interfaz y repositorio.** `EntidadMinecraftRepository` es una interfaz y Spring genera la clase que la implementa. El controller solo conoce los mensajes (`save`, `findById`, `findByVidaGreaterThan`...) y no sabe si atrás hay H2, PostgreSQL o un mock de prueba. Es la misma idea que el protocolo de POO-07: quien usa un objeto habla con el tipo de la interfaz y la implementación concreta se puede cambiar sin tocar a quien la usa.

**Protocolo del dominio.** La interface es `Explosivo`. La implementan `Creeper` (un mob, de la familia `EntidadMinecraft`) y `BloqueTnt` (un bloque, otra familia). `GET /api/explosivos` junta ambas en una `List<Explosivo>` y arma el JSON solo con mensajes de la interfaz; `Explosion.provocar` también recibe un `Explosivo`, por eso la misma simulación sirve para Creepers y para TNT.

**Ocultamiento.** Las reglas numéricas las sostienen las clases del dominio: el constructor rechaza vida ≤ 0 y nombre vacío, `recibirDanio` rechaza daño negativo y no deja la vida bajo 0, `Enderman` rechaza bloques negativos, `detonar` rechaza un explosivo ya usado. La prueba de Insomnia es `02 · Pedidos ilegales → 01 POST ilegal` seguido de `02 GET lista (no creció)`: devuelve 400 y la lista tiene la misma cantidad de entidades que antes.

**PUT y PATCH.** Usé **PATCH**: actualiza un subconjunto de campos (`nombre`, `x`, `z`, `bloquesTeletransporte`). Un campo omitido (o `null`) queda como estaba. No hay PUT porque reemplazar el recurso entero implicaría poder escribir la `vida`, que solo cambia por las reglas del juego; si un PATCH trae `vida`, se responde 400.

**DELETE.** Borrar es el *despawn* de Minecraft: la entidad sale del mundo. El controller le manda `desaparecer()` y luego `repositorio.delete`; responde 204 y un `GET` posterior da 404. Las explosiones pasadas no se pierden porque `Impacto` guarda una copia del id y del nombre, no una clave foránea.

**Dos reglas.** Una explosión cambia a la vez el estado del explosivo (se consume), de cada entidad alcanzada (pierde vida) y del Enderman (pierde bloques y cambia de posición). El recurso HTTP es `/api/explosiones`: un `POST` crea una explosión (201) y queda guardada; `GET /api/entidades` y `GET /api/explosiones` muestran después el estado ya persistido.

**Excepciones.** `MinecraftException` (base abstracta) y sus hijas: `EntidadInvalidaException` (un valor rompe un invariante → 400), `EntidadNoEncontradaException` (el id no existe → 404) y `ExplosionImposibleException` (el pedido es correcto pero el estado del mundo no lo permite → 409). Un recurso inexistente y una regla incumplida **sí** son tipos distintos: uno es un problema de la URL y el otro del estado del juego. El JSON de error lleva `error` (código de la excepción), `excepcion`, `estado`, `mensaje`, `ruta` y `momento`. Las excepciones son **no comprobadas** (`RuntimeException`): las lanzan constructores y mensajes del dominio, que no deberían llenar sus firmas de `throws`, y Spring revierte la transacción de la simulación ante una `RuntimeException` sin configuración extra. Cada una tiene su propio `@ExceptionHandler` en `MinecraftExceptionHandler` (`@RestControllerAdvice`); Spring elige el handler del tipo más específico.

**Preguntas de anclaje.**
- *Si mañana cambiamos H2 por otra base, ¿qué archivos se tocan?* Solo `pom.xml` (el driver) y `application.properties` (URL, usuario, dialecto). Dominio, repositorios, controllers y la colección de Insomnia quedan quietos.
- *¿Quién lanza la excepción, el controller o el objeto del juego?* El objeto del juego (constructores, `detonar`, `moverA`...). El controller solo lanza `EntidadNoEncontradaException` cuando el repositorio no encuentra el id; el handler traduce todo a HTTP.

## Colección de Insomnia

Archivo: [`insomnia/ja-minecraft-poo08.insomnia.json`](insomnia/ja-minecraft-poo08.insomnia.json) (formato de exportación v4).

**Importar:** en Insomnia, *Import* (o *Create → Import*) → *File* → elegir el archivo. Se crea la colección **JA Minecraft POO-08** con un entorno base que define `base_url` (`http://localhost:8080`) y los ids de prueba (`creeper_id`, `aldeano_id`, `enderman_id`, `borrar_id`, `tnt_id`, `explosion_id`, `id_inexistente`).

**Recorrido** (con la base vacía: detener la app, borrar `data/` y arrancar de nuevo):

1. `00 · Salud` → `GET /`.
2. `01 · CRUD` en orden: crear Creeper, Aldeano, Enderman y Aldeano lejano (reciben los ids 1 a 4, que son los del entorno; si la base no estaba vacía, copiar el `id` de cada respuesta al entorno), listar, buscar por id, PATCH, GET después del PATCH, DELETE y GET del borrado (404).
3. `02 · Pedidos ilegales y errores`: POST ilegal (400) y lista que no creció, id inexistente (404), PATCH de vida (400), tipo desconocido (400).
4. `03 · Protocolo Explosivo`: colocar la TNT y `GET /api/explosivos`.
5. `04 · Simulación`: explota el Creeper, ver las entidades, explota la TNT, ver el historial.
6. `05 · Excepciones de la simulación`: Creeper ya explotado (409), TNT ya detonada (409), explosivo inexistente (404).

Hay un pedido por cada hija de la jerarquía: `ENTIDAD_INVALIDA` (400), `ENTIDAD_NO_ENCONTRADA` (404) y `EXPLOSION_IMPOSIBLE` (409).

## Entregas

- Tarea 1 (Taller Git): <https://github.com/javifacha123/ja-taller-git-2026/commit/98bfbf4c5b4a393fa755bf43d689f344405d4def> (etiqueta `tarea-1`)
- Tarea 2 (POO-06): ver el enlace al commit de la solución en la entrega de Classroom.
- Tarea 3 (POO-08, CRUD + H2 + Insomnia): <https://github.com/javifacha123/ja-taller-git-2026/commit/84d8224b0348d1c94effd562c4d3892d77f818c5>
