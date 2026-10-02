# Bitácora - Taller Git 2026

## Repositorio

https://github.com/javifacha123/ja-taller-git-2026

## Objetivo

Desarrollar y publicar un servicio REST con Spring Boot que modele entidades de Minecraft utilizando herencia, sobreescritura, polimorfismo y ocultamiento de información.

## 1. Creación del proyecto

Se creó un proyecto Spring Boot utilizando:

- Java 21
- Maven
- Spring Web

Se verificó el funcionamiento de la aplicación mediante:

./mvnw spring-boot:run

## 2. Modelo de dominio

Se creó la clase abstracta `EntidadMinecraft`, que contiene los atributos comunes `nombre` y `vida`.

Los atributos son privados, aplicando ocultamiento de información.

La clase define los siguientes comportamientos:

- `moverse()`
- `recibirDanio()`
- `estaVivo()`
- `desaparecer()`

El método `moverse()` es abstracto y debe ser implementado por las clases hijas.

## 3. Herencia y sobreescritura

Se crearon dos clases que heredan de `EntidadMinecraft`:

- `Creeper`
- `Aldeano`

Cada clase sobrescribe el método `moverse()` para proporcionar un comportamiento diferente.

## 4. Polimorfismo

El controlador utiliza una referencia del tipo padre:

`EntidadMinecraft entidad;`

La referencia puede contener una instancia de `Creeper` o `Aldeano`.

Al ejecutar `entidad.moverse()`, se ejecuta automáticamente el comportamiento correspondiente de la clase concreta.

## 5. API REST

Se creó un `IndexController` con el endpoint:

GET /

También se creó `MinecraftController` con:

GET /minecraft/entidad

Ejemplo para un Creeper:

/minecraft/entidad?tipo=creeper&nombre=Creeper1&vida=20

Ejemplo para un Aldeano:

/minecraft/entidad?tipo=aldeano&nombre=Villager1&vida=20

La respuesta se devuelve en formato JSON mostrando los datos y el comportamiento de cada entidad.

## 6. Pruebas

Se verificó el funcionamiento de la API mediante `curl`.

Para un Creeper se obtuvo:

- vida: 20
- vivo: true
- tipo: Creeper
- movimiento: El Creeper se acerca silenciosamente al jugador.

Para un Aldeano se obtuvo:

- vida: 20
- vivo: true
- tipo: Aldeano
- movimiento: El Aldeano camina hacia la aldea.

Estas pruebas permiten comprobar que ambas clases comparten la misma jerarquía y presentan comportamientos diferentes mediante sobreescritura.

## 7. Diagrama de clases

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
