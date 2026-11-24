package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import controlador.GestorTorneo;
import excepciones.NumeroEquiposInvalidoException;
import excepciones.SorteoInvalidoException;
import modelo.Equipo;
import modelo.Partido;

public class VentanaTorneo extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color BG_MAIN = new Color(15, 18, 24);
    private static final Color BG_PANEL = new Color(24, 29, 38);
    private static final Color BG_TEXT = new Color(18, 22, 29);
    private static final Color BORDER_GRAY = new Color(55, 65, 81);
    private static final Color FG_TEXT = new Color(241, 245, 249);
    private static final Color FG_GOLD = new Color(250, 204, 21);

    private static final Color BTN_GREEN = new Color(22, 163, 74);
    private static final Color BTN_GREEN_DARK = new Color(21, 128, 61);
    private static final Color BTN_RED = new Color(220, 38, 38);
    private static final Color BTN_RED_DARK = new Color(185, 28, 28);

    private final GestorTorneo gestor;
    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaEquipos;
    private final JTextField campoEquipo;
    private final JTabbedPane pestanas;
    private final JTextArea areaResumen;
    private final JLabel etiquetaCampeon;

    public VentanaTorneo(GestorTorneo gestor) {
        this.gestor = gestor;
        setTitle("Liga Profesional - Sorteo Recursivo MVC");
        setSize(980, 640);
        setMinimumSize(new Dimension(840, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout(10, 10));

        JPanel superior = new JPanel(new BorderLayout());
        superior.setBackground(BG_PANEL);
        superior.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel titulo = new JLabel("LIGA PROFESIONAL - SORTEO RECURSIVO (OCTAVOS -> FINAL)");
        titulo.setForeground(FG_TEXT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));

        etiquetaCampeon = new JLabel("Campeon: Pendiente", SwingConstants.RIGHT);
        etiquetaCampeon.setForeground(FG_GOLD);
        etiquetaCampeon.setFont(new Font("SansSerif", Font.BOLD, 14));

        superior.add(titulo, BorderLayout.WEST);
        superior.add(etiquetaCampeon, BorderLayout.EAST);
        add(superior, BorderLayout.NORTH);

        JPanel izquierdo = new JPanel(new BorderLayout(8, 8));
        izquierdo.setPreferredSize(new Dimension(290, 0));
        izquierdo.setBackground(BG_PANEL);
        TitledBorder bordeLista = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            "Equipos (16 inicial / Potencia de 2)"
        );
        bordeLista.setTitleColor(FG_TEXT);
        izquierdo.setBorder(BorderFactory.createCompoundBorder(
            bordeLista,
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        modeloLista = new DefaultListModel<>();
        listaEquipos = new JList<>(modeloLista);
        listaEquipos.setBackground(BG_TEXT);
        listaEquipos.setForeground(FG_TEXT);
        listaEquipos.setSelectionBackground(BTN_GREEN_DARK);
        listaEquipos.setSelectionForeground(Color.WHITE);
        listaEquipos.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scrollLista = new JScrollPane(listaEquipos);
        scrollLista.getViewport().setBackground(BG_TEXT);
        scrollLista.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        izquierdo.add(scrollLista, BorderLayout.CENTER);

        JPanel controlesLista = new JPanel(new GridLayout(3, 1, 6, 6));
        controlesLista.setBackground(BG_PANEL);

        campoEquipo = new JTextField();
        campoEquipo.setBackground(BG_TEXT);
        campoEquipo.setForeground(FG_TEXT);
        campoEquipo.setCaretColor(Color.WHITE);
        campoEquipo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JPanel filaBotonesLista = new JPanel(new GridLayout(1, 2, 6, 6));
        filaBotonesLista.setBackground(BG_PANEL);

        JButton btnAgregar = crearBoton("Agregar", BTN_GREEN);
        JButton btnEliminar = crearBoton("Eliminar", BTN_RED);
        filaBotonesLista.add(btnAgregar);
        filaBotonesLista.add(btnEliminar);

        JButton btnRestaurar = crearBoton("Restaurar 16 Equipos", BTN_GREEN_DARK);

        controlesLista.add(campoEquipo);
        controlesLista.add(filaBotonesLista);
        controlesLista.add(btnRestaurar);
        izquierdo.add(controlesLista, BorderLayout.SOUTH);

        add(izquierdo, BorderLayout.WEST);

        pestanas = new JTabbedPane();
        pestanas.setBackground(BG_PANEL);
        pestanas.setForeground(FG_TEXT);

        areaResumen = crearAreaTexto("Presiona 'Ejecutar Sorteo Completo' para ver Octavos, Cuartos, Semifinales y Final.");
        JScrollPane scrollResumen = new JScrollPane(areaResumen);
        scrollResumen.getViewport().setBackground(BG_TEXT);
        scrollResumen.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        pestanas.addTab("Flujo Completo", scrollResumen);
        add(pestanas, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        inferior.setBackground(BG_PANEL);
        inferior.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_GRAY));

        JButton btnSortear = crearBoton("Ejecutar Sorteo Completo", BTN_GREEN);
        JButton btnGuardar = crearBoton("Guardar (torneo.ser)", BTN_GREEN_DARK);
        JButton btnCargar = crearBoton("Cargar (torneo.ser)", BTN_RED_DARK);
        JButton btnSalir = crearBoton("Salir", BTN_RED);

        inferior.add(btnSortear);
        inferior.add(btnGuardar);
        inferior.add(btnCargar);
        inferior.add(btnSalir);
        add(inferior, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarEquipo());
        btnEliminar.addActionListener(e -> eliminarEquipo());
        btnRestaurar.addActionListener(e -> {
            gestor.restaurar16Equipos();
            refrescarListaEquipos();
            JOptionPane.showMessageDialog(this, "Se cargaron los 16 equipos por defecto.");
        });
        btnSortear.addActionListener(e -> ejecutarSorteo());
        btnGuardar.addActionListener(e -> guardarEstado());
        btnCargar.addActionListener(e -> cargarEstado());
        btnSalir.addActionListener(e -> dispose());

        refrescarListaEquipos();
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fondo.darker(), 1),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return btn;
    }

    private JTextArea crearAreaTexto(String contenido) {
        JTextArea area = new JTextArea(contenido);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(BG_TEXT);
        area.setForeground(FG_TEXT);
        area.setCaretColor(Color.WHITE);
        area.setSelectionColor(BTN_GREEN_DARK);
        area.setSelectedTextColor(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return area;
    }

    private void refrescarListaEquipos() {
        modeloLista.clear();
        for (Equipo eq : gestor.obtenerEquipos()) {
            modeloLista.addElement(eq.getNombre());
        }
    }

    private void agregarEquipo() {
        String nombre = campoEquipo.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe el nombre de un equipo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (int i = 0; i < modeloLista.size(); i++) {
            if (modeloLista.get(i).equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(this, "El equipo ya esta registrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        modeloLista.addElement(nombre);
        campoEquipo.setText("");
    }

    private void eliminarEquipo() {
        int index = listaEquipos.getSelectedIndex();
        if (index != -1) {
            modeloLista.remove(index);
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void ejecutarSorteo() {
        try {
            List<Equipo> seleccionados = new ArrayList<>();
            for (int i = 0; i < modeloLista.size(); i++) {
                seleccionados.add(new Equipo(modeloLista.get(i)));
            }
            gestor.establecerEquipos(seleccionados);
            gestor.ejecutarSorteoCompleto();
            mostrarEtapasEnVista();
        } catch (NumeroEquiposInvalidoException | SorteoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Sorteo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEstado() {
        try {
            gestor.guardarTorneo();
            JOptionPane.showMessageDialog(this, "Torneo guardado en torneo.ser.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEstado() {
        try {
            gestor.cargarTorneo();
            refrescarListaEquipos();
            mostrarEtapasEnVista();
            JOptionPane.showMessageDialog(this, "Torneo cargado desde torneo.ser.");
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarEtapasEnVista() {
        Map<String, List<Partido>> etapas = gestor.obtenerEtapas();

        while (pestanas.getTabCount() > 1) {
            pestanas.removeTabAt(1);
        }

        if (etapas == null || etapas.isEmpty()) {
            areaResumen.setText("Sin resultados todavia.");
            etiquetaCampeon.setText("Campeon: Pendiente");
            return;
        }

        StringBuilder sbTodo = new StringBuilder();
        for (Map.Entry<String, List<Partido>> entry : etapas.entrySet()) {
            String nombreEtapa = entry.getKey();
            List<Partido> partidos = entry.getValue();

            StringBuilder sbEtapa = new StringBuilder();
            sbEtapa.append("========================================================\n");
            sbEtapa.append("  ").append(nombreEtapa.toUpperCase()).append("\n");
            sbEtapa.append("========================================================\n\n");

            sbTodo.append("=== ").append(nombreEtapa.toUpperCase()).append(" ===\n");
            for (Partido p : partidos) {
                sbEtapa.append("  ").append(p.toString()).append("\n");
                sbTodo.append("  ").append(p.toString()).append("\n");
            }
            sbTodo.append("\n");

            JTextArea areaEtapa = crearAreaTexto(sbEtapa.toString());
            JScrollPane scrollEtapa = new JScrollPane(areaEtapa);
            scrollEtapa.getViewport().setBackground(BG_TEXT);
            scrollEtapa.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
            pestanas.addTab(nombreEtapa, scrollEtapa);
        }

        Equipo campeon = gestor.obtenerCampeon();
        if (campeon != null) {
            sbTodo.append("********************************************************\n");
            sbTodo.append("  CAMPEON: ").append(campeon.getNombre()).append("\n");
            sbTodo.append("********************************************************\n");
            etiquetaCampeon.setText("Campeon: " + campeon.getNombre());
        } else {
            etiquetaCampeon.setText("Campeon: Pendiente");
        }

        areaResumen.setText(sbTodo.toString());
        areaResumen.setCaretPosition(0);
    }
}