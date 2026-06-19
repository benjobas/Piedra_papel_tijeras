import config.Config;
import modelo.Jugador;
import torneo.Torneo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cantidad = pedirCantidad(scanner);
        List<Jugador> jugadores = crearJugadores(cantidad);

        Torneo torneo = new Torneo(jugadores);
        Jugador campeon = torneo.ejecutar();

        System.out.println();
        System.out.println("CAMPEON DEL TORNEO: " + campeon.getNombre()
            + " (" + campeon.getVictorias() + " combates ganados)");
        scanner.close();
    }

    private static int pedirCantidad(Scanner scanner) {
        while (true) {
            System.out.println("Cantidad de jugadores (potencia de 2, entre "
                + Config.MIN_JUGADORES + " y " + Config.MAX_JUGADORES + "):");
            String linea = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(linea);
                if (n < Config.MIN_JUGADORES || n > Config.MAX_JUGADORES) {
                    System.out.println("Fuera de rango.");
                } else if ((n & (n - 1)) != 0) {
                    System.out.println("Debe ser potencia de 2.");
                } else {
                    return n;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida, ingrese un numero.");
            }
        }
    }

    private static List<Jugador> crearJugadores(int cantidad) {
        List<String> nombres = new ArrayList<>();
        Collections.addAll(nombres, Config.NOMBRES);
        Collections.shuffle(nombres);
        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            jugadores.add(new Jugador(nombres.get(i)));
        }
        return jugadores;
    }
}
