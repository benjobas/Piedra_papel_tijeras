# Evidencias de ejecución

En esta carpeta va la evidencia de funcionamiento que pide la pauta: la **salida por consola**
del programa (en `salida-ejemplo.txt`) y/o capturas de pantalla.

## Cómo capturar la salida a un archivo

Desde `ProyectoConcurrente/`, luego de compilar. Los logs salen por `stderr` y el campeón por
`stdout`, así que para juntar todo en un archivo se redirigen ambos:

```powershell
echo 8 | java -cp out Main > evidencias/salida-ejemplo.txt 2>&1
```

(El `echo 8` responde automáticamente la cantidad de jugadores.)

## Qué demuestra la salida

- Las trazas `[SYNC] Arena ocupada/liberada` muestran el **pool acotado**: cuando una ronda tiene
  más combates que arenas, un combate espera a que se libere una antes de empezar.
- Las trazas `[SYNC] Arbitro registra combate N` muestran el **recurso compartido sincronizado**:
  combates de distintos hilos registran su resultado sin pisarse.
- Los `[HILO] Inicia Combate-...` muestran los **hilos concurrentes** de cada ronda.

Los flags de `config/Logger.java` permiten activar o silenciar cada categoría de log.
