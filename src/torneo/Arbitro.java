package torneo;

import config.Logger;
import modelo.Jugador;
import modelo.Resultado;

import java.util.ArrayList;
import java.util.List;

public class Arbitro {

    private final List<Jugador> ganadores;
    private int combatesRegistrados;

    public Arbitro() {
        this.ganadores = new ArrayList<>();
        this.combatesRegistrados = 0;
    }

    public synchronized void registrar(Resultado resultado) {
        resultado.getGanador().sumarVictoria();
        resultado.getPerdedor().eliminar();
        ganadores.add(resultado.getGanador());
        combatesRegistrados++;
        Logger.sync("Arbitro registra combate " + combatesRegistrados
            + ": gana " + resultado.getGanador().getNombre());
    }

    public synchronized List<Jugador> getGanadores() {
        return new ArrayList<>(ganadores);
    }

    public synchronized int getCombatesRegistrados() {
        return combatesRegistrados;
    }

    public synchronized void reiniciarRonda() {
        ganadores.clear();
        combatesRegistrados = 0;
    }
}
