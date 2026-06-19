package modelo;

public class Resultado {

    private final Jugador ganador;
    private final Jugador perdedor;

    public Resultado(Jugador ganador, Jugador perdedor) {
        this.ganador = ganador;
        this.perdedor = perdedor;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public Jugador getPerdedor() {
        return perdedor;
    }
}
