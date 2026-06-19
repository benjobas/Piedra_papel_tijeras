# Torneo de Piedra, Papel o Tijera (Concurrente) — versión mínima

Simulación de un torneo eliminatorio de Piedra, Papel o Tijera en **Java puro** (sin frameworks
ni librerías externas), enfocada en los conceptos del paradigma concurrente: creación de hilos,
coordinación de tareas simultáneas, recursos compartidos, sincronización y prevención de
condiciones de carrera. Versión sin interfaz visual: la salida es por consola.

## Descripción del sistema

Al iniciar, el programa pregunta cuántos jugadores participan (una **potencia de 2** entre
`MIN_JUGADORES` y `MAX_JUGADORES`). Se generan jugadores con nombres aleatorios y se arma un
bracket eliminatorio. En cada ronda **cada combate corre en su propio hilo**, pero los combates
están **acotados por un pool de arenas**: hay una cantidad limitada de arenas y cada combate debe
tomar una libre para pelear; si no hay, espera. El ganador de cada combate avanza hasta que queda
un único campeón.

## Clases principales y su responsabilidad

| Clase | Paquete | Responsabilidad |
|-------|---------|-----------------|
| `Main` | (raíz) | Punto de entrada. Pide la cantidad de jugadores, los crea y ejecuta el torneo. |
| `Config` | `config` | Todas las constantes del juego (tiempos, mínimos/máximos, arenas, lista de nombres). |
| `Logger` | `config` | Flags para activar/desactivar logs por categoría (combates, rondas, hilos, sincronización). |
| `Jugador` | `modelo` | Entidad del juego. Decide su jugada y guarda sus estadísticas. |
| `Resultado` | `modelo` | Objeto inmutable con el ganador y el perdedor de un combate. |
| `Combate` | `torneo` | **Hilo** (`Runnable`). Toma una arena, juega hasta que hay un ganador y registra el resultado. |
| `Arbitro` | `torneo` | **Recurso compartido (registro)**: registra de forma `synchronized` los resultados. |
| `Arenas` | `torneo` | **Recurso compartido (pool acotado)**: presta arenas limitadas con `wait()` / `notify()`. |
| `Ronda` | `torneo` | Arma los combates de una ronda, lanza los hilos y espera a que terminen (`join`). |
| `Torneo` | `torneo` | Orquesta las rondas hasta que queda un campeón. |

Relaciones: `Torneo` **compone** un `Arbitro`, un `Arenas` y las `Ronda`; cada `Ronda` crea sus
`Combate`; cada `Combate` **asocia** dos `Jugador` y **usa** los recursos compartidos.

## Hilos utilizados y su función

1. **Hilo principal (`main`)**: pide datos, crea las entidades y conduce el torneo ronda a ronda.
2. **Un hilo por cada `Combate` de la ronda** (`new Thread(combate)`): juega el combate completo
   (pide la jugada a sus dos jugadores), registra el resultado y libera la arena.

Con la cantidad mínima de jugadores (8) la primera ronda tiene **4 combates en paralelo**, es
decir 4 hilos concurrentes activos, cumpliendo el mínimo de la pauta.

## Recursos compartidos y sincronización

Hay **dos recursos compartidos con roles complementarios**:

### 1. `Arbitro` — registro global (exclusión mutua con `synchronized`)

Varios combates terminan casi a la vez y, desde distintos hilos, llaman a
`arbitro.registrar(resultado)`, que agrega el ganador a la lista `ganadores`, incrementa el
contador `combatesRegistrados` y actualiza las estadísticas de los jugadores. Por eso el método
está marcado como `synchronized`: sólo un hilo a la vez modifica el árbitro.

### 2. `Arenas` — pool acotado (espera con `wait()` / `notify()`)

Antes de pelear, el combate llama a `arenas.adquirir()`; si no hay arena libre, el hilo **se
bloquea con `wait()`** hasta que otro combate llame a `arenas.liberar()` y haga `notify()`. Es el
patrón clásico de competencia por un recurso escaso.

## Condición de carrera sin sincronización

Si se quitara `synchronized` de `Arbitro.registrar(...)`, dos combates que terminan a la vez
ejecutarían en paralelo:

```
ganadores.add(resultado.getGanador());   // ArrayList NO es thread-safe
combatesRegistrados++;                    // leer-modificar-escribir NO atómico
```

Posibles fallas: **actualización perdida** del contador (dos hilos leen el mismo valor antes de
escribir) y **corrupción del ArrayList** (redimensionamiento simultáneo), lo que puede perder un
ganador y dejar la ronda siguiente con un bracket inconsistente. El código va sin comentarios
(los agrega el profesor), por eso la condición de carrera se explica sólo acá.

> Para demostrarla: quitar `synchronized` de `registrar(...)` y ejecutar con muchos jugadores;
> en varias corridas `getCombatesRegistrados()` deja de coincidir con la cantidad real de combates.

## Cómo iniciar y finalizar

- **Inicio**: se ejecuta `Main`, se ingresa la cantidad de jugadores y comienza el torneo.
- **Fin**: cuando queda un solo jugador, el torneo termina y se imprime el campeón por consola.

## Cómo compilar y ejecutar

Desde la carpeta `ProyectoConcurrente/`:

```bash
javac -d out src/Main.java src/config/*.java src/modelo/*.java src/torneo/*.java
java -cp out Main
```

En PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
java -cp out Main
```

### Requisitos y notas

- **Java 11 o superior.**
- Los logs salen por **error estándar** (`System.err`) y el resultado final por **salida estándar**
  (`System.out`); así se pueden separar redirigiendo (`java -cp out Main > resultado.txt`).
- La contención de arenas se ve cuando una ronda tiene más combates que arenas. Con el valor por
  defecto (`ARENAS_DISPONIBLES = 3`), ya con 8 jugadores (4 combates) un combate debe esperar.
