package modelo;

import java.io.Serializable;
import java.util.Random;

public class Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    private String etapa;
    private Equipo competidor;
    private Equipo oponente;
    private Equipo ganador;

    public Partido(String etapa, Equipo competidor, Equipo oponente) {
        this.etapa = etapa;
        this.competidor = competidor;
        this.oponente = oponente;
    }

    public Equipo simularGanador(Random random) {
        this.ganador = random.nextBoolean() ? competidor : oponente;
        return ganador;
    }

    public String getEtapa() {
        return etapa;
    }

    public Equipo getCompetidor() {
        return competidor;
    }

    public Equipo getOponente() {
        return oponente;
    }

    public Equipo getGanador() {
        return ganador;
    }

    @Override
    public String toString() {
        String g = (ganador != null) ? "  ->  Ganador: " + ganador.getNombre() : "";
        return String.format("%-22s vs %-22s%s", competidor.getNombre(), oponente.getNombre(), g);
    }
}
