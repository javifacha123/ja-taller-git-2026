# ja-taller-git-2026

## Taller Git 2026 - Minecraft

Proyecto Spring Boot que modela entidades de Minecraft utilizando herencia, sobreescritura, polimorfismo y ocultamiento de información.

## Modelo

La clase abstracta `EntidadMinecraft` concentra los comportamientos comunes de las entidades:

- moverse
- recibir daño
- desaparecer

Las clases `Creeper` y `Aldeano` especializan el comportamiento de movimiento mediante sobreescritura.

```mermaid
classDiagram
    EntidadMinecraft <|-- Creeper
    EntidadMinecraft <|-- Aldeano

    class EntidadMinecraft {
        <<abstract>>
        -String nombre
        -int vida
        +getNombre()
        +getVida()
        +moverse()*
        +recibirDanio(int dano)
        +estaVivo()
        +desaparecer()
    }

    class Creeper {
        +moverse()
    }

    class Aldeano {
        +moverse()
    }
