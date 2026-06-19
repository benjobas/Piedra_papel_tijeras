package modelo;

import config.Config;
import config.Logger;

public class Combate {

    public enum Estado {
        EN_ESPERA, EN_CURSO, FINALIZADO
    }

    public interface Registrador {
        void registrar(Resultado resultado);
    }

    public interface Observador {
        void alCambiar();
    }

    public interface Recinto {
        int adquirir();
        void liberar(int arena);
    }

    private final Jugador jugadorA;
    private final Jugador jugadorB;
    private final Registrador registrador;
    private final Observador observador;
    private final Recinto recinto;

    private volatile Estado estado;
    private volatile Resultado resultado;
    private volatile int arena;

    private Jugador.Jugada jugadaA;
    private Jugador.Jugada jugadaB;
    private int presentes;
    private int generacion;

    public Combate(Jugador jugadorA, Jugador jugadorB, Registrador registrador,
            Observador observador, Recinto recinto) {
        this.jugadorA = jugadorA;
        this.jugadorB = jugadorB;
        this.registrador = registrador;
        this.observador = observador;
        this.recinto = recinto;
        this.estado = Estado.EN_ESPERA;
        this.arena = 0;
        this.presentes = 0;
        this.generacion = 0;
    }

    public void disputar(Jugador yo) {
        if (yo == jugadorA) {
            arena = recinto.adquirir();
            estado = Estado.EN_CURSO;
            Logger.combate(jugadorA.getNombre() + " vs " + jugadorB.getNombre()
                + " comienza en la arena " + arena);
            avisar();
        }
        barrera();
        while (resultado == null) {
            dormir(Config.DURACION_COMBATE_MS);
            Jugador.Jugada mi = yo.decidirJugada();
            if (yo == jugadorA) {
                jugadaA = mi;
            } else {
                jugadaB = mi;
            }
            avisar();
            barrera();
            if (yo == jugadorA) {
                resolverTirada();
            }
            barrera();
        }
        if (yo == jugadorA) {
            recinto.liberar(arena);
        }
        Logger.hilo(yo.getNombre() + " termina su combate");
        avisar();
    }

    private void resolverTirada() {
        Jugador.Jugada ja = jugadaA;
        Jugador.Jugada jb = jugadaB;
        if (ja == jb) {
            Logger.combate(jugadorA.getNombre() + " y " + jugadorB.getNombre() + " empatan con " + ja);
            return;
        }
        Jugador ganador;
        Jugador perdedor;
        Jugador.Jugada jugadaGanadora;
        Jugador.Jugada jugadaPerdedora;
        if (ja.vence(jb)) {
            ganador = jugadorA;
            perdedor = jugadorB;
            jugadaGanadora = ja;
            jugadaPerdedora = jb;
        } else {
            ganador = jugadorB;
            perdedor = jugadorA;
            jugadaGanadora = jb;
            jugadaPerdedora = ja;
        }
        Resultado r = new Resultado(ganador, perdedor, jugadaGanadora, jugadaPerdedora);
        registrador.registrar(r);
        resultado = r;
        estado = Estado.FINALIZADO;
        Logger.combate(ganador.getNombre() + " vence a " + perdedor.getNombre()
            + " (" + jugadaGanadora + " vs " + jugadaPerdedora + ")");
    }

    private void barrera() {
        synchronized (this) {
            int g = generacion;
            presentes++;
            if (presentes == 2) {
                presentes = 0;
                generacion++;
                notifyAll();
            } else {
                while (g == generacion) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    private void avisar() {
        if (observador != null) {
            observador.alCambiar();
        }
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Jugador getJugadorA() {
        return jugadorA;
    }

    public Jugador getJugadorB() {
        return jugadorB;
    }

    public Estado getEstado() {
        return estado;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public int getArena() {
        return arena;
    }
}
