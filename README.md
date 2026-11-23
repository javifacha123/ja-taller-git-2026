# ja-taller-git-2026

## Taller Git 2026 - Minecraft

Servicio HTTP con Spring Boot (Java 21, Maven) que modela entidades de Minecraft con herencia, sobreescritura, sobrecarga, polimorfismo y ocultamiento de información. Desde el ejercicio **POO-08** el servicio guarda su estado en una base **H2**, expone un **CRUD REST** de la familia `EntidadMinecraft`, simula **explosiones** con dos reglas del juego y traduce una **jerarquía de excepciones** del dominio a respuestas HTTP. Desde **POO-09** publica su **OpenAPI**, expone sus dos reglas para que las consulte otro servicio y suma un **escenario** que solo se persiste si las APIs de dos compañeros lo aceptan.

**Licencia:** Apache License 2.0 (ver archivo `LICENSE`).

---

## Situación de POO-08 (observaciones cerradas)

POO-08 se corrigió con **19/24 → 7.9/10**. El documento de corrección dejó tres observaciones; las tres están cerradas en este repositorio, en el commit `POO-08: cierra observaciones de la corrección`.

| # | Observación del docente | Cómo se cerró | Dónde verlo |
|---|---|---|---|
| 1 | **Faltaba la capa de servicios en el CRUD.** `EntidadController`, `TntController`, `ExplosivoController` y `ExplosionController` inyectaban repositorios directamente; solo la simulación pasaba por un service. | Se crearon los contratos `EntidadService`, `TntService` y `ExplosivoService` (y se amplió `SimulacionService`) con sus implementaciones en `service.impl`. Los **cinco** controllers quedaron sin una sola referencia a un repositorio: solo llaman al service, y las transacciones viven en la capa de servicios. | `service/`, `service/impl/`, `rest/controller/` |
| 2 | **El dominio lanzaba excepciones y tenía lógica de negocio.** Había siete `throw` en `domain/` (cuatro en `EntidadMinecraft`, uno en `Enderman`, uno en `Creeper`, uno en `BloqueTnt`) y las reglas del juego vivían en las entities. | Las entities quedaron **solo con estado**: cero `throw` en `domain/` y cero en `rest/dto/`. Las validaciones pasaron a `EntidadServiceImpl`, el control de «un explosivo detona una sola vez» a `SimulacionServiceImpl`, y las dos reglas del juego a una jerarquía propia de la capa de servicios (`ReglaExplosion` abstracta, con `ReglaOndaExpansiva` y `ReglaEsquiveEnderman`). | `domain/`, `service/ReglaExplosion.java`, `service/impl/` |
| 3 | **Documentación.** El README decía que los constructores simples delegaban con `this(...)`, pero `Creeper` y `Aldeano` llamaban a `super(...)` directo; además había errores de ortografía en el Javadoc (`polimorfica`, `mas`, `comun`, `ademas`). | Se corrigió el **código** para que el README sea exacto: ahora las cuatro clases de la familia delegan con `this(...)` y un solo constructor por clase llama a `super(...)`. Se barrieron las tildes de todo el repositorio (39 correcciones en Javadoc, comentarios y `application.properties`) y se reescribió el Javadoc que la refactorización dejó desactualizado. | `domain/`, este README |

**Verificación.** La refactorización **no cambió el comportamiento observable**: las fórmulas de las dos reglas y los mensajes de error son idénticos. Se comprobó contra una base en memoria reproduciendo el escenario que verificó la corrección: el Creeper le hace 8 de daño a Testificate (vida 20 → 12), Slender esquiva gastando 2 bloques y salta a (4,0) con 6 bloques, la TNT le hace 9 de daño a Testificate (vida 3) y Slender vuelve a esquivar gastando 4 bloques hasta (8,0); los tres errores siguen respondiendo `EXPLOSION_IMPOSIBLE` (409), `ENTIDAD_INVALIDA` (400) y `ENTIDAD_NO_ENCONTRADA` (404).

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
├── config          OpenApiConfig (metadatos del documento OpenAPI)
├── exceptions       MinecraftException + EntidadInvalidaException,
│                    EntidadNoEncontradaException, ExplosionImposibleException,
│                    ReglaRechazadaException
├── repository       EntidadMinecraftRepository, CreeperRepository,
│                    BloqueTntRepository, ExplosionRepository   (solo interfaces)
├── service          EntidadService, TntService, ExplosivoService, SimulacionService,
│   │                ReglaService            (contratos)
│   │                ReglaExplosion          (contrato abstracto de una regla del juego)
│   └── impl         EntidadServiceImpl, TntServiceImpl, ExplosivoServiceImpl,
│                    SimulacionServiceImpl, ReglaServiceImpl    (lógica y transacciones)
│                    ReglaOndaExpansiva, ReglaEsquiveEnderman   (las dos reglas)
└── rest
    ├── controller   IndexController, EntidadController, ExplosivoController,
    │                TntController, ExplosionController, ReglaController,
    │                MinecraftController (POO-06)
    ├── dto          EntidadRequest (+ CreeperRequest, AldeanoRequest, EndermanRequest),
    │                EntidadPatch, TntRequest, SimulacionRequest, ExplosivoResumen,
    │                OndaExpansivaRequest, EsquiveEndermanRequest, VeredictoRegla,
    │                ErrorRespuesta
    └── handler      MinecraftExceptionHandler (un @ExceptionHandler por tipo)
~~~

## Diagrama de clases

~~~mermaid
classDiagram
    direction TB

    %% ---------- Familia persistida: SOLO ESTADO ----------
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
        +aplicarDanio(int danio)
        +aplicarDanio(int danio, boolean conArmadura)
        +estaVivo() boolean
        +distanciaA(int x, int z) double
        +desaparecer()
        +renombrar(String nombre)
        +ubicarEn(int x, int z)
    }
    class Creeper {
        -Boolean cargado
        -Boolean explotado
        +Creeper(String nombre)
        +Creeper(String nombre, int vida)
        +Creeper(String nombre, int vida, int x, int z)
        +Creeper(String nombre, int vida, int x, int z, boolean cargado)
        +moverse() String
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
        +gastarBloques(int bloques)
    }
    EntidadMinecraft <|-- Creeper
    EntidadMinecraft <|-- Aldeano
    EntidadMinecraft <|-- Enderman

    %% ---------- Protocolo de dominio ----------
    class Explosivo {
        <<interface>>
        +potenciaExplosion() int
        +radioExplosion() int
        +puedeExplotar() boolean
        +consumir()
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
        +Explosion(Explosivo fuente)
        +registrar(Impacto impacto)
    }
    class Impacto {
        <<embeddable>>
    }
    Explosion *-- Impacto : impactos

    %% ---------- Las DOS REGLAS: herencia y sobreescritura en el service ----------
    class ReglaExplosion {
        <<abstract>>
        +corresponde(EntidadMinecraft e) boolean*
        +aplicar(EntidadMinecraft e, Explosivo f) Impacto*
        #alcanzada(EntidadMinecraft e, Explosivo f) boolean
        +danioPorDistancia(int p, int r, double d) int$
        +bloquesNecesarios(int r, double d) int$
    }
    class ReglaOndaExpansiva {
        +corresponde(EntidadMinecraft e) boolean
        +aplicar(EntidadMinecraft e, Explosivo f) Impacto
    }
    class ReglaEsquiveEnderman {
        +corresponde(EntidadMinecraft e) boolean
        +aplicar(EntidadMinecraft e, Explosivo f) Impacto
    }
    ReglaExplosion <|-- ReglaOndaExpansiva
    ReglaOndaExpansiva <|-- ReglaEsquiveEnderman
    ReglaExplosion ..> EntidadMinecraft : cambia su estado

    %% ---------- Capa de servicios ----------
    class EntidadService {
        <<interface>>
        +crear(EntidadRequest p) EntidadMinecraft
        +listar(String nombre, boolean vivas) List
        +buscar(Long id) EntidadMinecraft
        +actualizar(Long id, EntidadPatch c) EntidadMinecraft
        +borrar(Long id)
    }
    class SimulacionService {
        <<interface>>
        +explotar(String tipo, Long id) Explosion
        +historial() List
    }
    class ReglaService {
        <<interface>>
        +evaluarOndaExpansiva(OndaExpansivaRequest p) VeredictoRegla
        +evaluarEsquiveEnderman(EsquiveEndermanRequest p) VeredictoRegla
    }
    class EntidadServiceImpl {
        <<Service>>
    }
    class SimulacionServiceImpl {
        <<Service>>
    }
    class ReglaServiceImpl {
        <<Service>>
    }
    EntidadService <|.. EntidadServiceImpl
    SimulacionService <|.. SimulacionServiceImpl
    ReglaService <|.. ReglaServiceImpl
    SimulacionServiceImpl --> ReglaExplosion : elige la regla
    ReglaServiceImpl ..> ReglaExplosion : usa las formulas

    %% ---------- Repositorios ----------
    class EntidadMinecraftRepository {
        <<interface>>
        +findByVidaGreaterThan(int vida) List
        +findByNombreContainingIgnoreCase(String nombre) List
    }
    class JpaRepository~T, ID~ {
        <<interface>>
    }
    JpaRepository <|-- EntidadMinecraftRepository
    EntidadServiceImpl --> EntidadMinecraftRepository : usa
    SimulacionServiceImpl --> EntidadMinecraftRepository : usa

    %% ---------- Controllers: solo traducen HTTP ----------
    class EntidadController
    class ExplosionController
    class ReglaController
    EntidadController --> EntidadService : usa
    ExplosionController --> SimulacionService : usa
    ReglaController --> ReglaService : usa

    %% ---------- Jerarquia de excepciones ----------
    class MinecraftException {
        <<abstract>>
        +getCodigo() String*
    }
    class EntidadInvalidaException
    class EntidadNoEncontradaException
    class ExplosionImposibleException
    class ReglaRechazadaException
    MinecraftException <|-- EntidadInvalidaException
    MinecraftException <|-- EntidadNoEncontradaException
    MinecraftException <|-- ExplosionImposibleException
    MinecraftException <|-- ReglaRechazadaException
    class MinecraftExceptionHandler {
        <<RestControllerAdvice>>
    }
    MinecraftExceptionHandler ..> MinecraftException : un handler por tipo
~~~

**Cómo leer el diagrama**

- **Las entities solo guardan estado.** `EntidadMinecraft` y sus hijas no validan, no lanzan excepciones y no deciden reglas: exponen `moverse()` (que es cómo se describen a sí mismas, no una regla) y mensajes que cambian su estado. Quien decide es la capa de servicios.
- **Herencia** (`<|--`, flecha llena): `Creeper`, `Aldeano` y `Enderman` *son una* `EntidadMinecraft`. También `ReglaEsquiveEnderman` *es una* `ReglaOndaExpansiva`, y por eso puede delegar en `super.aplicar(...)` cuando el esquive no se puede pagar.
- **Implementación de interface** (`<|..`): `Creeper` y `BloqueTnt` cumplen el protocolo `Explosivo` aunque son familias distintas; cada `ServiceImpl` cumple su contrato de `service`.
- **Las tres capas** se leen de abajo hacia arriba en el bloque de controllers: `Controller → Service → Repository`. Ningún controller toca un repositorio.
- **Las dos reglas viven en el service.** `SimulacionServiceImpl` recorre las reglas y usa la primera que dice `corresponde(...)`: no pregunta de qué tipo es la entidad. Las fórmulas son métodos estáticos de `ReglaExplosion` (subrayados en el diagrama), así la simulación interna y el endpoint que consulta un compañero usan el mismo código.
- **Jerarquía de excepciones**: `MinecraftException` es la base abstracta y no comprobada; las cuatro hijas nombran la regla que falló y el handler atiende cada una por separado.

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
| GET | `/v3/api-docs` | **POO-09**: documento OpenAPI 3 de este servicio (JSON) | 200 |
| GET | `/swagger-ui.html` | **POO-09**: el mismo documento, navegable | 200 |
| POST | `/api/reglas/onda-expansiva` | **POO-09**: mi regla 1, para que la consulte otro servicio | 200 / 400 |
| POST | `/api/reglas/esquive-enderman` | **POO-09**: mi regla 2, para que la consulte otro servicio | 200 / 400 |

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

1. **Onda expansiva** (`service.impl.ReglaOndaExpansiva`): cuando un `Explosivo` detona, toda entidad viva a una distancia `d` menor o igual al radio recibe `ceil(potencia × (radio + 1 − d) / (radio + 1))` de daño y el explosivo se consume (el Creeper queda con vida 0 y `explotado = true`).
2. **Esquive del Enderman** (`service.impl.ReglaEsquiveEnderman`, que hereda de la regla 1 y **sobreescribe** `aplicar`): si la onda alcanza a un Enderman y le quedan al menos `ceil(radio + 1 − d)` bloques de teletransporte, los gasta, aparece justo afuera del radio y no recibe daño; si no le alcanzan, delega en `super.aplicar(...)` y recibe el daño normal.

Desde POO-08 las reglas **ya no viven en las entities**: son una jerarquía propia de la capa de servicios. `SimulacionServiceImpl` recorre las reglas y aplica la primera que dice `corresponde(...)`, así que agregar un mob con otra reacción es agregar una regla, no tocar un `if`. Las dos fórmulas son métodos estáticos de `ReglaExplosion` y las comparten la simulación interna y los endpoints de `/api/reglas`.

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

- Constructores: `EntidadMinecraft`, `Creeper` y `Aldeano` tienen `(nombre)`, `(nombre, vida)` y, desde POO-08, `(nombre, vida, x, z)`. `Creeper` suma `(nombre, vida, x, z, cargado)`. `Enderman` tiene cuatro: `(nombre)`, `(nombre, vida)`, `(nombre, vida, bloques)` y `(nombre, vida, bloques, x, z)`. En las cuatro clases **los constructores simples delegan con `this(...)`** y hay **uno solo por clase que llama a `super(...)`**: el completo. Así la inicialización queda en un único lugar.
- Mensaje de la entidad: `aplicarDanio(int danio)` y `aplicarDanio(int danio, boolean conArmadura)`; con armadura, el daño que llega a la vida es la mitad.
- Quién valida: desde que se cerró la observación 2 de POO-08, **los constructores no validan**. Valida `EntidadServiceImpl`, que tiene una fábrica privada por cada firma (`nuevoCreeper(...)`, `nuevoEnderman(...)`...) que comprueba los valores y recién entonces construye. Como ningún otro paquete construye entidades, no hay camino que saltee la validación.

**Sobreescritura** (clase hija, misma firma, implementación propia):

- `moverse()` sigue siendo abstracto en `EntidadMinecraft` y lo sobreescriben `Creeper`, `Aldeano` y `Enderman`. Se quedó en el dominio porque es cómo la entidad se describe a sí misma, no una regla del juego: no valida, no depende de otra entidad y no decide nada.
- `aplicar(EntidadMinecraft, Explosivo)` está implementado en `ReglaOndaExpansiva` (regla 1) y `ReglaEsquiveEnderman` lo **sobreescribe** (regla 2). Cuando al Enderman no le alcanzan los bloques, la regla 2 llama a `super.aplicar(...)` en vez de repetir la cuenta del daño.
- `Creeper` y `BloqueTnt` implementan los métodos de `Explosivo` cada uno a su manera.

**Cómo se distinguen:** la sobrecarga se resuelve en tiempo de compilación según los argumentos y ocurre dentro de una misma clase. La sobreescritura se resuelve en ejecución según el tipo real del objeto y ocurre entre una clase padre y sus hijas (o entre una interface y sus implementaciones).

## Ocultamiento frente al JSON y frente a JPA

- No hay setters públicos de `vida`, `x`, `z` ni `nombre`. Cambian solo por mensajes: `aplicarDanio`, `desaparecer`, `ubicarEn`, `renombrar`, `gastarBloques`. Esos mensajes no validan a propósito: validar es tarea del service, que es el único que los llama.
- **JSON → objeto:** Jackson nunca arma una entidad. Recibe un *record* DTO, que también es solo datos, y el **service** decide qué constructor usar después de validar. Un `POST` con `vida: -5` lanza la excepción antes de `repositorio.save`, así que la tabla no cambia.
- **Fila → objeto:** JPA necesita un constructor sin argumentos; está declarado `protected`, así que solo lo ven el paquete `domain` y las clases hijas; ni el controller ni los DTO pueden fabricar un objeto vacío. JPA lee y escribe los campos por reflexión (el `@Id` está en un campo), sin pasar por setters.

## Qué hay que pensar (respuestas)

**Recurso y verbo.** El sustantivo es la **entidad** del mundo: `/api/entidades` es la colección y `/api/entidades/{id}` un ítem. El verbo HTTP dice la operación (POST crear, GET leer, PATCH actualizar, DELETE borrar); la URL nunca lleva verbos como `/crear`. La simulación también es un sustantivo: `/api/explosiones`.

**Identidad.** La clave es `id` (`Long`). La asigna la base con `GenerationType.IDENTITY` al insertar. El JSON de creación **no** la lleva (y si la lleva, se ignora: el DTO no tiene ese campo); la respuesta 201 sí la devuelve, junto con la cabecera `Location`.

**Herencia y tablas.** Elegí `SINGLE_TABLE`: una sola tabla `ENTIDAD_MINECRAFT` con una columna `TIPO_ENTIDAD` (`CREEPER`, `ALDEANO`, `ENDERMAN`). En mi dominio las hijas comparten casi todo (nombre, vida, posición) y solo agregan una o dos columnas, y la simulación necesita traer **todas** las entidades vivas de una vez sin *joins*. El costo es que las columnas propias de una hija (`CARGADO`, `EXPLOTADO`, `BLOQUES_TELETRANSPORTE`) quedan en NULL para las demás, por eso en Java son `Boolean`/`Integer`. En el JSON el subtipo se distingue con la propiedad `"tipo"` (`@JsonTypeInfo` + `@JsonSubTypes`), tanto al crear (en el DTO) como al listar (en la entidad).

**Interfaz y repositorio.** `EntidadMinecraftRepository` es una interfaz y Spring genera la clase que la implementa. El controller solo conoce los mensajes (`save`, `findById`, `findByVidaGreaterThan`...) y no sabe si atrás hay H2, PostgreSQL o un mock de prueba. Es la misma idea que el protocolo de POO-07: quien usa un objeto habla con el tipo de la interfaz y la implementación concreta se puede cambiar sin tocar a quien la usa.

**Protocolo del dominio.** La interface es `Explosivo`. La implementan `Creeper` (un mob, de la familia `EntidadMinecraft`) y `BloqueTnt` (un bloque, otra familia). `GET /api/explosivos` junta ambas en una `List<Explosivo>` y arma el JSON solo con mensajes de la interfaz; `Explosion.provocar` también recibe un `Explosivo`, por eso la misma simulación sirve para Creepers y para TNT.

**Ocultamiento.** Los campos siguen siendo privados y sin setters, así que desde afuera de la jerarquía nadie escribe `vida` ni `x` directamente. Lo que cambió al cerrar la observación 2 de POO-08 es **quién sostiene las reglas numéricas**: ya no el constructor, sino la capa de servicios. `EntidadServiceImpl` rechaza vida ≤ 0, nombre vacío, bloques negativos y daño negativo; `SimulacionServiceImpl` rechaza un explosivo ya usado. El efecto para quien consume la API es el mismo, porque el service es el único camino hacia las entities. La prueba de Insomnia es `02 · Pedidos ilegales → 01 POST ilegal` seguido de `02 GET lista (no creció)`: devuelve 400 y la lista tiene la misma cantidad de entidades que antes.

**PUT y PATCH.** Usé **PATCH**: actualiza un subconjunto de campos (`nombre`, `x`, `z`, `bloquesTeletransporte`). Un campo omitido (o `null`) queda como estaba. No hay PUT porque reemplazar el recurso entero implicaría poder escribir la `vida`, que solo cambia por las reglas del juego; si un PATCH trae `vida`, se responde 400.

**DELETE.** Borrar es el *despawn* de Minecraft: la entidad sale del mundo. El controller le manda `desaparecer()` y luego `repositorio.delete`; responde 204 y un `GET` posterior da 404. Las explosiones pasadas no se pierden porque `Impacto` guarda una copia del id y del nombre, no una clave foránea.

**Dos reglas.** Una explosión cambia a la vez el estado del explosivo (se consume), de cada entidad alcanzada (pierde vida) y del Enderman (pierde bloques y cambia de posición). El recurso HTTP es `/api/explosiones`: un `POST` crea una explosión (201) y queda guardada; `GET /api/entidades` y `GET /api/explosiones` muestran después el estado ya persistido.

**Excepciones.** `MinecraftException` (base abstracta) y sus hijas: `EntidadInvalidaException` (un valor rompe un invariante → 400), `EntidadNoEncontradaException` (el id no existe → 404) y `ExplosionImposibleException` (el pedido es correcto pero el estado del mundo no lo permite → 409). Un recurso inexistente y una regla incumplida **sí** son tipos distintos: uno es un problema de la URL y el otro del estado del juego. El JSON de error lleva `error` (código de la excepción), `excepcion`, `estado`, `mensaje`, `ruta` y `momento`. Desde POO-09 hay una cuarta hija, `ReglaRechazadaException` (una de mis dos reglas rechaza la jugada que me proponen → 400). Las excepciones son **no comprobadas** (`RuntimeException`): las lanza la capa de servicios al validar un pedido o al aplicar una regla, y obligar a declarar `throws` en cada firma de servicio ensuciaría el contrato; además Spring revierte la transacción de la simulación ante una `RuntimeException` sin configuración extra. Cada una tiene su propio `@ExceptionHandler` en `MinecraftExceptionHandler` (`@RestControllerAdvice`); Spring elige el handler del tipo más específico.

**Preguntas de anclaje.**
- *Si mañana cambiamos H2 por otra base, ¿qué archivos se tocan?* Solo `pom.xml` (el driver) y `application.properties` (URL, usuario, dialecto). Dominio, repositorios, controllers y la colección de Insomnia quedan quietos.
- *¿Quién lanza la excepción, el controller o el objeto del juego?* **Ninguno de los dos: la capa de servicios.** Ésta fue la observación 2 de la corrección de POO-08. Las entities quedaron solo con estado y los controllers solo traducen HTTP; `EntidadServiceImpl`, `SimulacionServiceImpl` y `ReglaServiceImpl` son los únicos que validan y lanzan, y el handler traduce cada tipo a su código HTTP.
- *Si cambia una de las dos reglas del juego, ¿dónde se toca?* En un solo lugar: las fórmulas son métodos estáticos de `ReglaExplosion`. Las usan tanto la simulación interna como los endpoints de `/api/reglas` que consulta un compañero, así que las dos puertas quedan de acuerdo solas.

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

## POO-09: APIs cruzadas

### Mapa A1 / A2 / A3

Según el documento de grupos, los que quedan de a dos forman un **dúo**: cada uno consulta a su compañero y además pide el segundo servicio a alguien de un trío del mismo juego, que les deja usar su OpenAPI.

| Rol | Quién | Usuario de GitHub | Puerto | OpenAPI |
|---|---|---|---|---|
| **A1** (yo) | Javier Atlasovich | `javifacha123` | 8080 | <http://localhost:8080/v3/api-docs> |
| **A2** | _(pendiente de confirmar)_ | — | 8081 | <http://localhost:8081/v3/api-docs> |
| **A3** | _(pendiente de confirmar)_ | — | 8082 | <http://localhost:8082/v3/api-docs> |

### Las reglas citadas

**Mis dos reglas (las que publico para que me consulten):**

1. **Onda expansiva** — `POST /api/reglas/onda-expansiva`. A una distancia `d` de un explosivo de potencia `p` y radio `r`, el daño es `ceil(p × (r + 1 − d) / (r + 1))`. Rechaza si la entidad está fuera del radio o si el daño declarado no coincide.
2. **Esquive del Enderman** — `POST /api/reglas/esquive-enderman`. El Enderman necesita `ceil(r + 1 − d)` bloques de teletransporte para salir del radio. Rechaza si no le alcanzan.

Las dos responden **200** cuando aceptan y **400 `REGLA_RECHAZADA`** cuando rechazan, igual que el ejemplo del enunciado.

**Las reglas que consulto:**

| Rol | Regla | Endpoint |
|---|---|---|
| A2 | _(pendiente)_ | _(pendiente)_ |
| A3 | _(pendiente)_ | _(pendiente)_ |

### El escenario, en una frase

Un Creeper detona y la onda alcanza a un Aldeano y a un Enderman: **la jugada se guarda solo si A2 acepta el daño que recibe el Aldeano y A3 acepta que el Enderman puede esquivar.**

~~~http
POST /api/escenarios
{ "nombre": "Emboscada en la aldea", "creeperId": 1, "aldeanoId": 2, "endermanId": 3 }
~~~

Las tres entidades tienen que existir ya en la base: el escenario no las crea, propone una jugada sobre el mundo que hay.

**El escenario no detona el Creeper.** Es a propósito: el escenario somete una jugada *propuesta* a las reglas de los dos compañeros y deja constancia de que es legal; la detonación real es otra operación, `POST /api/explosiones`, que sí consume el explosivo y aplica el daño. Separarlos tiene dos ventajas: el escenario queda **idempotente** (se puede repetir en una demostración sin que el mundo cambie entre intento e intento) y las consultas a A2 y a A3 no tienen efectos secundarios, así que el orden en que se hacen no altera el resultado. Lo único que el escenario escribe es la fila de la jugada.

| Situación | Respuesta | Qué pasa con la base |
|---|---|---|
| A2 y A3 aceptan | 201 con la jugada guardada | se inserta una fila en `JUGADA` |
| A2 o A3 rechaza | 409 `COMPANERO_RECHAZO`, el mensaje dice **cuál** | **queda igual** |
| A2 o A3 no responde | 502 `COMPANERO_INACCESIBLE`, el mensaje dice **cuál** | **queda igual** |
| Falta un id o el tipo no corresponde | 400 `ENTIDAD_INVALIDA` | queda igual |
| Alguna de las tres entidades no existe | 404 `ENTIDAD_NO_ENCONTRADA` | queda igual |

**Por qué no queda nada a medias.** `EscenarioServiceImpl.jugar` es `@Transactional`. Si A3 rechaza después de que A2 aceptó, la excepción corta el método antes del `save` y Spring revierte la transacción: la atomicidad la da la transacción, no un borrado manual. Por eso el historial de `/api/escenarios` solo contiene jugadas que los dos aprobaron.

**Cómo se distingue de quién fue el fallo.** Hay dos excepciones distintas, cada una con su handler, y las dos guardan el rol:

- `CompaneroRechazoException` → 409. El compañero contestó que **no**.
- `CompaneroInaccesibleException` → 502. **No hubo nadie** del otro lado (servicio apagado, puerto cambiado, sin configurar).

Son tipos separados a propósito: «me dijeron que no» y «no me contestaron» son problemas distintos y se resuelven distinto.

### Dónde se configuran los compañeros

En `application.properties`, bajo el prefijo `companeros`. **No hay ninguna URL escrita a mano en el código**: todo entra por `CompanerosProperties`, y se puede pisar al arrancar sin recompilar.

~~~properties
companeros.a2.nombre=...
companeros.a2.base-url=http://localhost:8081
companeros.a2.ruta=/api/reglas/...
companeros.a3.nombre=...
companeros.a3.base-url=http://localhost:8082
companeros.a3.ruta=/api/reglas/...
~~~

Todo el HTTP hacia afuera vive en una sola clase, `CompaneroClientHttp`, que interpreta la respuesta por el **código de estado** (2xx acepta, 4xx rechaza). Eso la hace servir con cualquier compañero sin importar cómo haya armado su JSON. Los tiempos de espera son cortos (3 s para conectar, 5 s para responder) para que un servicio apagado falle rápido y con un mensaje claro en vez de quedarse colgado.

### Qué hay que pensar (respuestas de POO-09)

**OpenAPI. ¿Dónde vive su documento? ¿Qué operaciones aparecen, con descripción suficiente para que un extraño las invoque?**
El documento vive en `/v3/api-docs` (JSON) y navegable en `/swagger-ui.html`. Lo genera `springdoc-openapi` 3.2.1, y sus metadatos están en `config/OpenApiConfig`. Aparecen todas las operaciones agrupadas por `@Tag`: el CRUD de entidades, los bloques de TNT, el endpoint de la interface `Explosivo`, la simulación de explosiones, **mis dos reglas** y el escenario cruzado. La descripción general del documento incluye las **dos fórmulas escritas** y la tabla de códigos de error, y cada operación declara sus respuestas con `@ApiResponse`. Un extraño puede invocar `POST /api/reglas/onda-expansiva` leyendo solo eso: sabe qué campos mandar, qué devuelve al aceptar y con qué código rechaza.

**Escenario. ¿Qué hecho del juego necesita las dos validaciones ajenas para ser legal?**
Una misma explosión produce **dos hechos distintos** que caen bajo reglas distintas: el Aldeano **recibe daño** (regla de la onda expansiva) y el Enderman **esquiva o no** (regla del teletransporte). La jugada es legal solo si los dos hechos lo son, y cada uno lo valida un compañero diferente. No alcanza con una sola validación porque son dos efectos independientes de la misma detonación.

**¿Qué se ve en la base después de un escenario exitoso?**
Una fila nueva en la tabla `JUGADA`, que un `GET /api/escenarios` muestra. Guarda el nombre de la jugada, los ids y nombres de las tres entidades, el daño y los bloques calculados, y **el veredicto textual de cada compañero**, para que la jugada siga siendo legible aunque después cambien o se apaguen sus servicios.

**¿Qué queda igual si A2 acepta y A3 rechaza?**
Todo. `EscenarioServiceImpl.jugar` es `@Transactional`: la excepción de A3 corta el método antes del `save` y Spring revierte la transacción. No hay borrado manual ni compensación; la atomicidad la da la transacción. Verificado: después de provocar el rechazo de A2 y el de A3, el historial seguía mostrando una sola jugada.

**¿En qué orden consulta a A2 y a A3, y por qué?**
Primero A2 (el daño), después A3 (el esquive). Es el orden del hecho en el juego: la onda llega y produce daño, y recién entonces el Enderman decide si se teletransporta. Además deja ver el caso interesante: cuando A3 rechaza, A2 **ya había aceptado**, y aun así no queda nada escrito. Si el orden se invirtiera el resultado sería el mismo, porque ninguna de las dos consultas modifica estado: son consultas puras.

**Cliente. ¿Cómo configura las URLs de A2 y A3?**
Con propiedades, bajo el prefijo `companeros`, ligadas por `config/CompanerosProperties`. Se pueden pisar con argumentos al arrancar o con variables de entorno, sin recompilar:

~~~bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--companeros.a2.base-url=http://localhost:8085 --companeros.a2.ruta=/api/reglas/otra"
~~~

**Errores. ¿Propaga el código y el cuerpo del compañero, o los traduce a su jerarquía?**
Los **traduce**, pero sin perder el original. Un 4xx ajeno se convierte en `CompaneroRechazoException` → **409 `COMPANERO_RECHAZO`**, y un fallo de conexión en `CompaneroInaccesibleException` → **502 `COMPANERO_INACCESIBLE`**. El cuerpo que mandó el compañero viaja **dentro del campo `mensaje`** de mi JSON de error, así no se pierde la trazabilidad.

Lo traduzco por dos razones. Primero, porque quien consume **mi** API tiene que ver **mis** códigos: no sería coherente que `/api/escenarios` devolviera de golpe un código de error con el vocabulario de otro proyecto. Segundo, porque el significado cambia: para el compañero el pedido fue inválido (400), pero para mi escenario el problema no es el formato sino que el estado del mundo no permite la jugada, y eso es un conflicto (409).

**¿El handler propio distingue «falló la API de A2» de «falló la de A3»?**
Sí. Las dos excepciones guardan el **rol** (`A2` o `A3`) y el nombre del compañero, y cada una tiene su propio `@ExceptionHandler` en `MinecraftExceptionHandler`. El mensaje empieza con `Rechazo de A2 (...)` o `Rechazo de A3 (...)` y aclara que la jugada no se guardó.

**Pregunta de anclaje: si el compañero cambia el nombre de una ruta, ¿alcanza con actualizar lo que dice el OpenAPI, o había código copiado de su dominio?**
Alcanza con cambiar **una propiedad** (`companeros.a2.ruta`), y ni siquiera hace falta recompilar: se puede pasar como argumento al arrancar. No hay nada copiado del dominio ajeno en este repositorio: no tengo sus clases, ni sus fórmulas, ni sus DTO. Todo el contacto con sus servicios pasa por una sola clase, `CompaneroClientHttp`, que interpreta la respuesta por el **código de estado HTTP**, no por la forma de su JSON. Por eso un cambio de ruta, de puerto o de formato de respuesta no toca ni una línea de código de dominio.

### Cuando un compañero no responde

El enunciado pide dejar registrado qué se hizo cuando A2 o A3 no estaban disponibles.

- **Contacto:** se pidió por el chat del curso el OpenAPI, las dos reglas y la ruta de validación de cada compañero, indicando los puertos propuestos (yo 8080, A2 8081, A3 8082).
- **Reintento:** el cliente espera 3 segundos para conectar y 5 para la respuesta. Si no hay nadie, responde **502 `COMPANERO_INACCESIBLE`** diciendo cuál falló, en vez de quedarse colgado.
- **Respuestas guardadas:** cada pedido de la colección de Insomnia lleva en su descripción la **respuesta verificada**, con el texto exacto del escenario feliz y de los dos rechazos.
- **Verificación independiente:** el circuito completo se probó apuntando A2 y A3 a este mismo servicio (ver más abajo), así el cliente, el escenario, la persistencia y la atribución del rechazo quedan demostrados aunque un compañero no arranque el día de la corrección.

### Cómo clonar y arrancar los tres

~~~bash
# A1 (este repositorio)
git clone https://github.com/javifacha123/ja-taller-git-2026.git
cd ja-taller-git-2026 && ./mvnw spring-boot:run          # puerto 8080

# A2
git clone <repositorio de A2> && cd <carpeta de A2>
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# A3
git clone <repositorio de A3> && cd <carpeta de A3>
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
~~~

Si los puertos o las rutas de los compañeros cambian, no hace falta tocar el código:

~~~bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--companeros.a2.base-url=http://localhost:8085 --companeros.a2.ruta=/api/reglas/otra"
~~~

**Orden para recorrer la colección de Insomnia:** primero tienen que estar arriba **A2 y A3**, y recién después A1. Si A1 arranca solo, el escenario responde 502 en vez de 201 — que es el comportamiento correcto, pero no el que se quiere demostrar.

### Probar el escenario sin los compañeros

El circuito completo se puede verificar apuntando A2 y A3 a este mismo servicio, porque mis dos endpoints de reglas aceptan los campos que el escenario envía:

~~~bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8099 --spring.datasource.url=jdbc:h2:mem:prueba --companeros.a2.base-url=http://localhost:8099 --companeros.a2.ruta=/api/reglas/onda-expansiva --companeros.a3.base-url=http://localhost:8099 --companeros.a3.ruta=/api/reglas/esquive-enderman"
~~~

Con eso se comprobó el escenario feliz (jugada guardada con 8 de daño y 2 bloques), el rechazo de A2 (Aldeano fuera del radio), el rechazo de A3 (Enderman sin bloques suficientes) y que **tras los dos rechazos el historial seguía teniendo una sola jugada**.

### Colección de Insomnia de POO-09

Archivo: [`insomnia/ja-minecraft-poo09.insomnia.json`](insomnia/ja-minecraft-poo09.insomnia.json) (formato de exportación v4). La colección de POO-08 se conserva aparte, sin cambios.

**Importar:** en Insomnia, *Import* → *File* → elegir el archivo. Se crea la colección **JA Minecraft POO-09 (APIs cruzadas)** con un entorno que define `base_url` (8080), `a2_url` (8081), `a3_url` (8082) y los ids de prueba.

**Recorrido** (con la base vacía: detener la app, borrar `data/` y arrancar de nuevo; y con los servicios de A2 y A3 arriba):

1. `00 · Preparación y OpenAPI` — salud, mi OpenAPI, el de A2, el de A3, y las cinco entidades del escenario (reciben los ids 1 a 5, que son los del entorno).
2. `01 · Mis dos reglas publicadas` — las dos reglas aceptando y las dos rechazando. Es lo que consulta un compañero cuando yo soy su A2 o su A3.
3. `02 · Escenario cruzado` — el escenario feliz (201), el historial, **un rechazo por compañero** (A2 y A3, los dos 409) y el historial de nuevo, que tiene que seguir mostrando una sola jugada.
4. `03 · Errores del escenario` — id inexistente (404), tipo equivocado (400) y compañero caído (502).

Cada pedido lleva en su **descripción la respuesta que se verificó**, con el texto exacto. El enunciado avisa que guardar las respuestas de ejemplo evita perder el puntaje si el servicio de un compañero no arranca el día de la corrección.

## Entregas

- Tarea 1 (Taller Git): <https://github.com/javifacha123/ja-taller-git-2026/commit/98bfbf4c5b4a393fa755bf43d689f344405d4def> (etiqueta `tarea-1`)
- Tarea 2 (POO-06): ver el enlace al commit de la solución en la entrega de Classroom.
- Tarea 3 (POO-08, CRUD + H2 + Insomnia): <https://github.com/javifacha123/ja-taller-git-2026/commit/84d8224b0348d1c94effd562c4d3892d77f818c5>
- Tarea 4 (POO-09, APIs cruzadas): _(pendiente: pegar acá el enlace al commit de la solución)_
