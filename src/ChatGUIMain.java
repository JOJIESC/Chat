import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ChatGUIMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Solicitar el nombre de usuario si no se ha pasado como parámetro
                String username;
                if (args.length > 0) {
                    username = args[0]; // Si el nombre de usuario se pasa como parámetro
                } else {
                    username = JOptionPane.showInputDialog("Ingrese su nombre de usuario:");
                }

                if (username == null || username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nombre de usuario no válido. El programa se cerrará.");
                    System.exit(0);
                }

                // Establecer conexión con el servidor
                Socket socket = new Socket("localhost", 1234);

                // Crear cliente y pasarle el socket y el nombre de usuario
                Client client = new Client(socket, username);

                // Crear la interfaz gráfica y pasarle el cliente
                ChatGUI gui = new ChatGUI(client);

                // Iniciar el proceso para recibir mensajes y actualizar la interfaz
                client.listenForMessage();

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al conectarse al servidor. Intente nuevamente.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
