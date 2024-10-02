import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ChatGUIMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Pedir al usuario que ingrese su nombre de usuario
                String username = JOptionPane.showInputDialog("Enter your username for the chat:");

                // Establecer conexión con el servidor
                Socket socket = new Socket("localhost", 1234);
                Client client = new Client(socket, username);  // Crear cliente con socket y username

                // Crear la interfaz gráfica y pasarle el cliente
                ChatGUI gui = new ChatGUI(client);

                // Iniciar el proceso para recibir mensajes y actualizar la interfaz
                client.listenForMessage();  // Pasar el área de chat para recibir mensajes

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error connecting to the server. Please try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
