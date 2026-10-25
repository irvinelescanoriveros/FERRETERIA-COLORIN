
package pe.ferreteria.principal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import pe.ferreteria.vista.LoginForm;

public class Principal {

    public static void main(String[] args) {

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
