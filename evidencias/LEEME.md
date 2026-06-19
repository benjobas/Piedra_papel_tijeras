# Evidencias de ejecución

En esta carpeta va la evidencia de funcionamiento que pide la pauta. Podés incluir:

- **Capturas de pantalla** del tablero durante una ronda (combates EN CURSO / FINALIZADO) y de la
  pantalla final del campeón.
- **Salida por consola** redirigida a un archivo de texto.

## Cómo capturar la salida a un archivo

Desde `ProyectoConcurrente/`, luego de compilar:

```powershell
echo 8 | java -cp out Main > evidencias/salida-ejemplo.txt
```

(El `echo 8` responde automáticamente la cantidad de jugadores.)

## Sugerencia para evidenciar la concurrencia

1. En `config/Logger.java` poner `LOG_HILOS = true` y `LOG_SINCRONIZACION = true`.
2. Ejecutar con 8 o 16 jugadores.
3. En la salida se verán los hilos `Jugador-...` arrancando en simultáneo y el `[SYNC]` del
   árbitro registrando combates de distintos hilos: esa es la evidencia del paralelismo y del
   uso del recurso compartido sincronizado.
