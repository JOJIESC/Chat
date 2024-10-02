import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    // Lista para rastrear todas las instancias de ClientHandler y enviar mensajes a todos los clientes
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Server server;

    // Constructor
    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            // Inicializar los buffers para leer y escribir a través del socket
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Leer el nombre de usuario enviado por el cliente
            this.clientUsername = bufferedReader.readLine();
            // Agregar al cliente actual a la lista
            clientHandlers.add(this);
            // Notificar a todos los clientes que un nuevo usuario ha entrado al chat
            server.broadcastMessage("Server: " + clientUsername + " ha entrado al chat");
            // Enviar la lista de usuarios conectados
            server.sendUserList();
        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Lógica del hilo que ejecuta este ClientHandler
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    server.broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Método para enviar un mensaje a este cliente
    public void sendMessage(String messageToSend) throws IOException {
        bufferedWriter.write(messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    // Método para eliminar el cliente de la lista y notificar a los demás
    public void removeClientHandler() {
        clientHandlers.remove(this);
        server.broadcastMessage("Server: " + clientUsername + " ha salido del chat");
        server.sendUserList(); // Actualizar la lista de usuarios conectados
    }

    // Método para cerrar todos los recursos y eliminar al cliente de la lista
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();  // Eliminar al cliente de la lista
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el nombre de usuario del cliente
    public String getClientUsername() {
        return clientUsername;
    }
}
