package torneo;

import config.Logger;
import modelo.Combate;
import modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Ronda {

    private final int numero;
    private final List<Jugador> participantes;
    private final List<Combate> combates;
    private final Arbitro arbitro;
    private final Torneo torneo;

    public Ronda(int numero, List<Jugador> participantes, Arbitro arbitro, Torneo torneo) {
        this.numero = numero;
        this.participantes = new ArrayList<>(participantes);
        this.arbitro = arbitro;
        this.torneo = torneo;
        this.combates = new ArrayList<>();
        armarCombates();
    }

    private void armarCombates() {
        for (int i = 0; i + 1 < participantes.size(); i += 2) {
            Jugador a = participantes.get(i);
            Jugador b = participantes.get(i + 1);
            combates.add(new Combate(a, b, arbitro, torneo, torneo.getArenas()));
        }
    }

    public void ejecutar() {
        List<Thread> hilos = new ArrayList<>();
        for (Combate combate : combates) {
            Jugador a = combate.getJugadorA();
            Jugador b = combate.getJugadorB();
            a.prepararCombate(combate);
            b.prepararCombate(combate);
            hilos.add(new Thread(a, "Jugador-" + a.getNombre()));
            hilos.add(new Thread(b, "Jugador-" + b.getNombre()));
        }
        for (Thread hilo : hilos) {
            hilo.start();
            Logger.hilo("Inicia hilo " + hilo.getName());
        }
        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getNumero() {
        return numero;
    }

    public List<Combate> getCombates() {
        return combates;
    }

    public List<Jugador> getParticipantes() {
        return participantes;
    }
}
