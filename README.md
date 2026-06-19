# Torneo de Piedra, Papel o Tijera (Concurrente)

Simulación de un torneo eliminatorio de Piedra, Papel o Tijera escrita en **Java puro**
(sin frameworks ni librerías externas), aplicando los conceptos del paradigma concurrente:
creación de hilos, coordinación de tareas simultáneas, recurso compartido, sincronización
y prevención de condiciones de carrera.

## Descripción del sistema

Al iniciar, el programa pregunta cuántos jugadores participan (una **potencia de 2** entre
`MIN_JUGADORES` y `MAX_JUGADORES`). Se generan jugadores con nombres aleatorios y se arma un
bracket eliminatorio. En cada ronda los combates ocurren en paralelo, pero **acotados por un
pool de arenas**: hay una cantidad limitada de arenas (`ARENAS_DISPONIBLES`) y cada combate
debe **tomar una arena libre** para pelear; si no hay, **espera**. Así corren *hasta X combates
en simultáneo*. Cada jugador es un hilo que decide su jugada y se enfrenta a su rival. El
ganador de cada combate avanza a la siguiente ronda hasta que queda un único campeón.

Mientras tanto, un hilo de render dibuja el tablero en consola (ronda actual, estado de cada
combate, jugadas, ganadores que avanzan y la pantalla final del campeón), usando colores ANSI
y emojis.

## Clases principales y su responsabilidad

| Clase | Paquete | Responsabilidad |
|-------|---------|-----------------|
| `Main` | (raíz) | Punto de entrada. Pide la cantidad de jugadores, los crea, lanza el hilo de render y ejecuta el torneo. |
| `Config` | `config` | Todas las constantes del juego (tiempos, mínimos/máximos, lista de nombres). |
| `Logger` | `config` | Flags para activar/desactivar logs por categoría (combates, rondas, hilos, sincronización). |
| `Jugador` | `modelo` | Entidad concurrente (`Runnable`). Decide su jugada con lógica propia y disputa su combate. |
| `Combate` | `modelo` | Enfrenta a dos jugadores, sincroniza sus tiradas y determina el resultado. |
| `Resultado` | `modelo` | Objeto inmutable con ganador, perdedor y jugadas de un combate. |
| `Torneo` | `torneo` | Orquesta las rondas, mantiene el estado global y notifica cambios al render. |
| `Arbitro` | `torneo` | **Recurso compartido (registro)**: registra de forma sincronizada los resultados de los combates. |
| `Arenas` | `torneo` | **Recurso compartido (pool acotado)**: presta una cantidad limitada de arenas; los combates compiten por ellas con `wait()`/`notify()`. |
| `Ronda` | `torneo` | Arma los combates de una ronda, lanza los hilos de los jugadores y espera a que terminen. |
| `Renderer` | `visual` | Hilo de render: limpia la pantalla y redibuja el tablero. |
| `Tablero` | `visual` | Construye el string del tablero a partir del estado del torneo. |
| `Colores` | `visual` | Constantes de color ANSI. |
| `Simbolos` | `visual` | Emojis y caracteres Unicode del juego. |

Relaciones: `Torneo` **compone** un `Arbitro`, un `Arenas` y las `Ronda`; cada `Ronda`
**compone** sus `Combate`; cada `Combate` **asocia** dos `Jugador` y **usa** los recursos
compartidos. La dependencia hacia el árbitro, el render y las arenas se hace mediante las
interfaces `Combate.Registrador`, `Combate.Observador` y `Combate.Recinto` (inversión de
dependencias), por eso `modelo` no depende de `torneo`.

## Hilos utilizados y su función

1. **Hilo principal (`main`)**: pide datos, crea las entidades y conduce el torneo ronda a ronda.
2. **Un hilo por cada `Jugador` activo en la ronda** (`new Thread(jugador)`): decide su jugada y
   participa en su combate. Con 4 jugadores hay 4 hilos de jugador simultáneos en la primera
   ronda (2 combates en paralelo); con 8, hay 8 hilos (4 combates), etc.
3. **Hilo de render (`Renderer`)**: dibuja el tablero. Espera con `wait()` y sólo redibuja cuando
   hay un cambio de estado (o tras un timeout corto).

Esto cumple el mínimo de **4 hilos concurrentes activos** ya con la cantidad mínima de jugadores.

## Recursos compartidos y por qué necesitan sincronización

Hay **dos recursos compartidos con roles distintos y complementarios**:

### 1. `Arbitro` — registro global (exclusión mutua)

Es el registro único de quién avanza de ronda. Varios combates terminan aproximadamente al
mismo tiempo y, desde distintos hilos, llaman a `arbitro.registrar(resultado)`, que modifica
estructuras compartidas:

- agrega el ganador a la lista `ganadores`,
- agrega el resultado al `historial`,
- incrementa el contador `combatesRegistrados`,
- actualiza las estadísticas de los jugadores.

Por eso `registrar(...)` (y los getters que leen ese estado) están marcados como `synchronized`:
así sólo un hilo a la vez modifica el árbitro y no se pierden ni se corrompen registros. Es un
recurso **único a propósito**: si hubiera varios árbitros, habría que reunir sus listas al final
de cada ronda y reaparecería el mismo punto compartido (además de perderse la condición de
carrera demostrable).

### 2. `Arenas` — pool acotado (competencia por recurso escaso)

Modela una cantidad limitada de arenas donde ocurren los combates. Antes de pelear, el combate
llama a `arenas.adquirir()`; si no hay arena libre, **el hilo se bloquea con `wait()`** hasta que
otro combate llame a `arenas.liberar(arena)` y haga `notify()`. Esto demuestra el patrón clásico
de **competencia por un recurso escaso** (como los ejemplos de la pauta: "robots que compiten por
recursos", "herramientas limitadas"). El estado `EN ESPERA` del tablero representa justamente un
combate esperando una arena libre.

Con `ARENAS_DISPONIBLES` arenas y una ronda con más combates que arenas, sólo X combates corren a
la vez y el resto espera su turno. La contención se ve cuando **la cantidad de combates de la
ronda supera la de arenas** (por defecto 4 arenas: con 16 jugadores la ronda 1 tiene 8 combates,
así que la mitad espera).

## Dónde ocurre la condición de carrera sin sincronización

Si se quitara `synchronized` de `Arbitro.registrar(...)`, dos combates que terminan a la vez
ejecutarían en paralelo, por ejemplo:

```
ganadores.add(resultado.getGanador());   // ArrayList NO es thread-safe
combatesRegistrados++;                    // leer-modificar-escribir NO atómico
```

Posibles fallas observables:

- **Actualización perdida**: `combatesRegistrados++` no es atómico. Si dos hilos leen el mismo
  valor antes de escribir, una de las dos incrementaciones se pierde y el contador queda por
  debajo del real.
- **Corrupción de la lista**: `ArrayList.add` desde dos hilos a la vez puede dejar la lista en
  estado inconsistente (redimensionamiento simultáneo), perder un ganador o lanzar
  `ArrayIndexOutOfBoundsException`.

Consecuencia para el torneo: una ronda podría "perder" un ganador y la siguiente ronda
arrancaría con un número impar de jugadores o con un bracket inconsistente.

En este proyecto el problema está **resuelto** manteniendo `synchronized` en el árbitro. La
versión insegura no se deja en el código (el código va sin comentarios), pero el fragmento de
arriba muestra exactamente qué línea fallaría sin sincronización.

> Para *demostrar* la condición de carrera de forma controlada, basta con quitar la palabra
> `synchronized` del método `registrar(...)` y subir la cantidad de jugadores a 16: con varias
> ejecuciones aparecen rondas donde `getCombatesRegistrados()` no coincide con la cantidad real
> de combates.

## Mecanismos de sincronización usados

- **`synchronized`** en `Arbitro` (exclusión mutua sobre el registro compartido).
- **`synchronized` + `wait()` / `notifyAll()`** en `Arenas`, como pool acotado: un combate
  espera con `wait()` si no hay arena libre y es despertado con `notify()` al liberarse una.
- **`synchronized` + `wait()` / `notifyAll()`** en `Combate`, como barrera para que las dos
  tiradas de los jugadores se resuelvan en simultáneo (ninguno avanza hasta que el rival jugó).
- **`wait()` / `notifyAll()`** entre `Torneo` y `Renderer`: el render **espera** cuando no hay
  cambios de estado y se despierta sólo cuando algo cambia (o tras `INTERVALO_RENDER_MS`).
- **`Thread.join()`** en `Ronda`: la ronda no avanza hasta que **todos** los combates terminaron.

## Cómo iniciar y finalizar

- **Inicio**: se ejecuta `Main`, se ingresa la cantidad de jugadores y comienza el torneo.
- **Fin**: cuando queda un solo jugador, el torneo pasa a estado `TERMINADO`, el render muestra
  la pantalla de campeón y el hilo de render se detiene de forma ordenada.

## Cómo compilar y ejecutar

Desde la carpeta `ProyectoConcurrente/`:

```bash
javac -encoding UTF-8 -d out ^
  src/Main.java ^
  src/config/*.java ^
  src/modelo/*.java ^
  src/torneo/*.java ^
  src/visual/*.java

java -cp out Main
```

En PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
java -cp out Main
```

### Requisitos

- **Java 11 o superior.**
- `-encoding UTF-8` es necesario porque las clases visuales usan emojis.
- Los **colores ANSI y los emojis** se ven correctamente en **Windows Terminal**, en la terminal
  integrada de VS Code y en cualquier consola Linux/macOS. La consola clásica `cmd.exe` antigua
  puede no interpretar los códigos ANSI.

## Notas de configuración

- `Config.java` concentra los tiempos y límites del juego, además de la lista `NOMBRES`.
- `Logger.java` permite activar trazas por categoría. Importante: el `Renderer` limpia la
  pantalla en cada frame, por lo que para **leer los logs con tranquilidad** conviene capturar la
  salida a un archivo o reducir temporalmente la actividad del render. El tablero visual y los
  logs comparten la salida estándar.
