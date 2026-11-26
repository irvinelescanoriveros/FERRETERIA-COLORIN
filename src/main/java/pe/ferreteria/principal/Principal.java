
package pe.ferreteria.principal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import pe.ferreteria.infraestructura.Registro;
import pe.ferreteria.vista.LoginForm;

public class Principal {

    public static void main(String[] args) {

        Registro.iniciar();
        Logger log = Registro.de(Principal.class);
        log.info("Aplicacion iniciada");

        Thread.setDefaultUncaughtExceptionHandler((hilo, error) ->
                log.log(Level.SEVERE, "Error no controlado en " + hilo.getName(), error));

        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "No se pudo configurar el estilo visual: "
                    + e.getMessage()
            );
        }

        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
