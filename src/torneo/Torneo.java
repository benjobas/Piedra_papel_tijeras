package torneo;

import config.Config;
import config.Logger;
import modelo.Combate;
import modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Torneo implements Combate.Observador {

    public enum Estado {
        PREPARADO, EN_CURSO, TERMINADO
    }

    private final List<Jugador> jugadores;
    private final Arbitro arbitro;
    private final Arenas arenas;

    private final Object monitorRender;
    private boolean hayCambio;

    private volatile Estado estado;
    private volatile Ronda rondaActual;
    private volatile Jugador campeon;
    private volatile int numeroRonda;
    private final int totalRondas;

    public Torneo(List<Jugador> jugadores) {
        this.jugadores = new ArrayList<>(jugadores);
        this.arbitro = new Arbitro();
        this.arenas = new Arenas(Config.ARENAS_DISPONIBLES);
        this.monitorRender = new Object();
        this.hayCambio = false;
        this.estado = Estado.PREPARADO;
        this.numeroRonda = 0;
        this.totalRondas = calcularRondas(jugadores.size());
    }

    private int calcularRondas(int n) {
        int rondas = 0;
        while (n > 1) {
            n = n / 2;
            rondas++;
        }
        return rondas;
    }

    public void ejecutar() {
        estado = Estado.EN_CURSO;
        alCambiar();
        List<Jugador> vivos = new ArrayList<>(jugadores);
        int numero = 1;
        while (vivos.size() > 1) {
            arbitro.reiniciarRonda();
            numeroRonda = numero;
            Ronda ronda = new Ronda(numero, vivos, arbitro, this);
            rondaActual = ronda;
            alCambiar();
            Logger.ronda("Inicia ronda " + numero + " con " + vivos.size() + " jugadores");
            ronda.ejecutar();
            vivos = arbitro.getGanadores();
            Logger.ronda("Fin de ronda " + numero + ", avanzan " + vivos.size());
            numero++;
            dormir(Config.PAUSA_ENTRE_RONDAS_MS);
        }
        campeon = vivos.get(0);
        estado = Estado.TERMINADO;
        Logger.ronda("Campeon del torneo: " + campeon.getNombre());
        alCambiar();
    }

    public void alCambiar() {
        synchronized (monitorRender) {
            hayCambio = true;
            monitorRender.notifyAll();
        }
    }

    public void esperarCambio(long timeoutMs) {
        synchronized (monitorRender) {
            if (!hayCambio) {
                try {
                    monitorRender.wait(timeoutMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            hayCambio = false;
        }
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public Arbitro getArbitro() {
        return arbitro;
    }

    public Arenas getArenas() {
        return arenas;
    }

    public Estado getEstado() {
        return estado;
    }

    public Ronda getRondaActual() {
        return rondaActual;
    }

    public Jugador getCampeon() {
        return campeon;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }

    public int getTotalRondas() {
        return totalRondas;
    }

    public boolean estaTerminado() {
        return estado == Estado.TERMINADO;
    }
}
