package controlador;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import excepciones.NumeroEquiposInvalidoException;
import excepciones.SorteoInvalidoException;
import modelo.Equipo;
import modelo.Partido;
import modelo.Torneo;

public class GestorTorneo {
    private Torneo torneo;
    private static final String ARCHIVO_SER = "torneo.ser";

    public GestorTorneo() {
        this.torneo = new Torneo("Liga Profesional");
        this.torneo.cargar16EquiposPorDefecto();
    }

    public void restaurar16Equipos() {
        torneo.cargar16EquiposPorDefecto();
    }

    public void establecerEquipos(List<Equipo> equipos) throws NumeroEquiposInvalidoException {
        torneo.setEquipos(equipos);
    }

    public List<Equipo> obtenerEquipos() {
        return torneo.getEquipos();
    }

    public void ejecutarSorteoCompleto() throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        torneo.sortearYResolverTorneo();
    }

    public Map<String, List<Partido>> obtenerEtapas() {
        return torneo.getEtapas();
    }

    public Equipo obtenerCampeon() {
        return torneo.getCampeon();
    }

    public void guardarTorneo() throws IOException {
        torneo.guardarSerializado(ARCHIVO_SER);
    }

    public void cargarTorneo() throws IOException, ClassNotFoundException {
        this.torneo = Torneo.cargarSerializado(ARCHIVO_SER);
    }
}
