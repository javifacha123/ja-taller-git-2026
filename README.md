# ja-taller-git-2026

## Taller Git 2026 - Minecraft

Servicio HTTP con Spring Boot (Java 21, Maven) que modela entidades de Minecraft con herencia, sobreescritura, sobrecarga, polimorfismo y ocultamiento de información.

**Licencia:** Apache License 2.0 (ver archivo `LICENSE`).

## Estructura de paquetes

~~~
py.edu.uc.lp3
├── Application
├── domain          (EntidadMinecraft, Creeper, Aldeano, Enderman)
└── rest.controller (IndexController, MinecraftController)
~~~

## Diagrama de clases

~~~mermaid
classDiagram
    EntidadMinecraft <|-- Creeper
    EntidadMinecraft <|-- Aldeano
    EntidadMinecraft <|-- Enderman

    class EntidadMinecraft {
        <<abstract>>
        -String nombre
        -int vida
        +EntidadMinecraft(String nombre)
        +EntidadMinecraft(String nombre, int vida)
        +getNombre() String
        +getVida() int
        +moverse() String*
        +recibirDanio(int dano)
        +recibirDanio(int dano, boolean conArmadura)
        +estaVivo() boolean
        +desaparecer()
    }

    class Creeper {
        +Creeper(String nombre)
        +Creeper(String nombre, int vida)
        +moverse() String
    }

    class Aldeano {
        +Aldeano(String nombre)
        +Aldeano(String nombre, int vida)
        +moverse() String
    }

    class Enderman {
        -int bloquesTeletransporte
        +Enderman(String nombre)
        +Enderman(String nombre, int vida)
        +Enderman(String nombre, int vida, int bloquesTeletransporte)
        +getBloquesTeletransporte() int
        +setBloquesTeletransporte(int bloques)
        +moverse() String
    }
~~~

## Qué cambió con la sobrecarga y con la sobreescritura

**Sobrecarga** (misma clase, mismo nombre, otra lista de argumentos):

- Constructores: `EntidadMinecraft`, `Creeper` y `Aldeano` tienen uno simple `(nombre)` y otro `(nombre, vida)`. `Enderman` tiene tres: `(nombre)`, `(nombre, vida)` y `(nombre, vida, bloquesTeletransporte)`. El simple delega al completo con `this(...)` y las hijas llaman a `super(...)`. Todos validan los valores, así que el objeto nunca queda en un estado ilegal.
- Mensaje del dominio: `recibirDanio(int dano)` y `recibirDanio(int dano, boolean conArmadura)`. La segunda reduce el daño a la mitad si hay armadura y delega en la primera.

**Sobreescritura** (clase hija, misma firma, implementación propia):

- `moverse()` es abstracto en `EntidadMinecraft`. `Creeper`, `Aldeano` y `Enderman` lo sobreescriben con su propio texto. El controller trabaja con una variable de tipo `EntidadMinecraft` y el texto sale del método de la hija.

**Cómo se distinguen:** la sobrecarga se resuelve en tiempo de compilación según los argumentos y ocurre dentro de una misma clase. La sobreescritura se resuelve en ejecución según el tipo real del objeto y ocurre entre una clase padre y sus hijas.

## Ocultamiento de la información

`nombre` y `vida` son `private`. La vida solo cambia por `recibirDanio()` y `desaparecer()`, y los constructores rechazan nombre vacío o vida menor o igual a 0. El controller no asigna la vida a mano: solo construye el objeto y, si los parámetros rompen una regla, la clase lanza `IllegalArgumentException` y el controller responde 400 con el mensaje.

## Cómo probarlo

~~~bash
./mvnw spring-boot:run
~~~

~~~bash
curl http://localhost:8080/
curl "http://localhost:8080/minecraft/entidad?tipo=creeper&nombre=Creeper1"
curl "http://localhost:8080/minecraft/entidad?tipo=enderman&nombre=Enderman1&vida=30&bloques=5"
curl "http://localhost:8080/minecraft/entidad?tipo=aldeano&nombre=Villager1&vida=15&danio=10&armadura=true"
curl "http://localhost:8080/minecraft/entidad?tipo=creeper&nombre=Malo&vida=-5"
~~~

Parámetros de `GET /minecraft/entidad`: `tipo` (creeper, aldeano o enderman) y `nombre` son obligatorios; `vida`, `bloques` (solo enderman), `danio` y `armadura` son opcionales.

## Entregas

- Tarea 1 (Taller Git): <https://github.com/javifacha123/ja-taller-git-2026/commit/98bfbf4c5b4a393fa755bf43d689f344405d4def> (etiqueta `tarea-1`)
- Tarea 2 (POO-06): ver el enlace al commit de la solución en la entrega de Classroom.
