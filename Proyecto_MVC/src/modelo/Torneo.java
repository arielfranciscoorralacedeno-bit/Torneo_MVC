package modelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import excepciones.NumeroEquiposInvalidoException;
import excepciones.SorteoInvalidoException;

public class Torneo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombreLiga;
    private List<Equipo> equipos;
    private Map<String, List<Partido>> etapas;
    private Equipo campeon;
    private transient Random random;

    public Torneo(String nombreLiga) {
        this.nombreLiga = nombreLiga;
        this.equipos = new ArrayList<>();
        this.etapas = new LinkedHashMap<>();
        this.random = new Random();
    }

    private Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public void cargar16EquiposPorDefecto() {
        equipos.clear();
        String[] base = {
            "Barcelona SC", "Emelec", "Liga de Quito", "Independiente del Valle",
            "El Nacional", "Aucas", "Universidad Catolica", "Deportivo Cuenca",
            "Macara", "Orense", "Delfin", "Tecnico Universitario",
            "Mushuc Runa", "Cumbaya", "Libertad", "Imbabura"
        };
        for (String nombre : base) {
            equipos.add(new Equipo(nombre));
        }
    }

    public void setEquipos(List<Equipo> nuevosEquipos) throws NumeroEquiposInvalidoException {
        validarPotenciaDeDos(nuevosEquipos.size());
        this.equipos = new ArrayList<>(nuevosEquipos);
    }

    public List<Equipo> getEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public Map<String, List<Partido>> getEtapas() {
        return etapas;
    }

    public Equipo getCampeon() {
        return campeon;
    }

    public String getNombreLiga() {
        return nombreLiga;
    }

    private void validarPotenciaDeDos(int cantidad) throws NumeroEquiposInvalidoException {
        if (cantidad < 2 || (cantidad & (cantidad - 1)) != 0) {
            throw new NumeroEquiposInvalidoException(
                "El numero de equipos (" + cantidad + ") no es valido. Debe ser potencia de 2 (2, 4, 8, 16)."
            );
        }
    }

    private String nombreEtapa(int cantidadEquipos) {
        switch (cantidadEquipos) {
            case 16: return "Octavos de final";
            case 8:  return "Cuartos de final";
            case 4:  return "Semifinales";
            case 2:  return "Final";
            default: return "Etapa (" + cantidadEquipos + " equipos)";
        }
    }

    public void sortearYResolverTorneo() throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        validarPotenciaDeDos(equipos.size());
        etapas.clear();
        campeon = null;

        List<Equipo> listaInicial = new ArrayList<>(equipos);
        Collections.shuffle(listaInicial, getRandom());
        this.campeon = ejecutarEtapaRecursiva(listaInicial);
    }

    private Equipo ejecutarEtapaRecursiva(List<Equipo> participantes)
            throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        validarPotenciaDeDos(participantes.size());

        String etapaActual = nombreEtapa(participantes.size());
        List<Equipo> sorteados = new ArrayList<>(participantes);
        Collections.shuffle(sorteados, getRandom());

        List<Partido> partidosEtapa = new ArrayList<>();
        Set<Equipo> yaParticiparonEnEtapa = new HashSet<>();

        emparejarRecursivo(sorteados, 0, etapaActual, yaParticiparonEnEtapa, partidosEtapa);
        etapas.put(etapaActual, partidosEtapa);

        List<Equipo> clasificados = new ArrayList<>();
        for (Partido partido : partidosEtapa) {
            Equipo ganador = partido.simularGanador(getRandom());
            if (ganador == null) {
                throw new SorteoInvalidoException("Error al asignar ganador en " + etapaActual);
            }
            clasificados.add(ganador);
        }

        if (clasificados.size() == 1) {
            return clasificados.get(0);
        }

        return ejecutarEtapaRecursiva(clasificados);
    }

    private void emparejarRecursivo(
            List<Equipo> lista,
            int indice,
            String etapaActual,
            Set<Equipo> yaParticiparon,
            List<Partido> partidos) throws SorteoInvalidoException {

        if (indice >= lista.size()) {
            return;
        }
        if (indice + 1 >= lista.size()) {
            throw new SorteoInvalidoException("Cantidad impar de equipos en " + etapaActual);
        }

        Equipo competidor = lista.get(indice);
        Equipo oponente = lista.get(indice + 1);

        if (competidor == null || oponente == null) {
            throw new SorteoInvalidoException("Equipo nulo detectado en " + etapaActual);
        }
        if (competidor.equals(oponente)) {
            throw new SorteoInvalidoException("Un equipo no puede ser competidor y oponente a la vez: " + competidor.getNombre());
        }
        if (yaParticiparon.contains(competidor) || yaParticiparon.contains(oponente)) {
            throw new SorteoInvalidoException("Equipo repetido en la misma etapa: " + etapaActual);
        }

        yaParticiparon.add(competidor);
        yaParticiparon.add(oponente);
        partidos.add(new Partido(etapaActual, competidor, oponente));

        emparejarRecursivo(lista, indice + 2, etapaActual, yaParticiparon, partidos);
    }

    public void guardarSerializado(String rutaArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(this);
        }
    }

    public static Torneo cargarSerializado(String rutaArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            Torneo recuperado = (Torneo) ois.readObject();
            recuperado.random = new Random();
            return recuperado;
        }
    }
}
