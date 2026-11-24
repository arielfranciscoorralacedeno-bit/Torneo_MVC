import javax.swing.SwingUtilities;
import controlador.GestorTorneo;
import vista.VentanaTorneo;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorTorneo gestor = new GestorTorneo();
            VentanaTorneo ventana = new VentanaTorneo(gestor);
            ventana.setVisible(true);
        });
    }
}
