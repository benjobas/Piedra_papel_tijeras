package config;

public class Logger {
    public static final boolean LOG_COMBATES = true;
    public static final boolean LOG_RONDAS = true;
    public static final boolean LOG_HILOS = true;
    public static final boolean LOG_SINCRONIZACION = true;

    public static void combate(String msg) {
        if (LOG_COMBATES) System.err.println("[COMBATE] " + msg);
    }
    public static void ronda(String msg) {
        if (LOG_RONDAS) System.err.println("[RONDA] " + msg);
    }
    public static void hilo(String msg) {
        if (LOG_HILOS) System.err.println("[HILO] " + msg);
    }
    public static void sync(String msg) {
        if (LOG_SINCRONIZACION) System.err.println("[SYNC] " + msg);
    }
}
