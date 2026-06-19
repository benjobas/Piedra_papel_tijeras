package visual;

import modelo.Jugador;

public class Simbolos {
    public static final String PIEDRA = "✊";
    public static final String PAPEL = "🖐";
    public static final String TIJERA = "✂️";
    public static final String TROFEO = "🏆";
    public static final String ESPADAS = "⚔️";
    public static final String RELOJ = "⏳";
    public static final String CHECK = "✅";
    public static final String ESPERA = "⏸️";
    public static final String CALAVERA = "💀";
    public static final String CORONA = "👑";
    public static final String INCOGNITA = "❔";

    public static String deJugada(Jugador.Jugada jugada) {
        if (jugada == null) {
            return INCOGNITA;
        }
        switch (jugada) {
            case PIEDRA:
                return PIEDRA;
            case PAPEL:
                return PAPEL;
            case TIJERA:
                return TIJERA;
            default:
                return INCOGNITA;
        }
    }

    private Simbolos() {
    }
}
