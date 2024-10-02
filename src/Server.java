import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    // Variable para manejar el ServerSocket
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clientHandlers;

    // Constructor para iniciar el servidor
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientHandlers = new ArrayList<>();
    }

    // Método para iniciar el servidor y gestionar conexiones
    public void startServer() {
        try {
            // Mientras el servidor esté abierto, acepta conexiones
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");

                // Crear una instancia de ClientHandler para manejar la comunicación con el cliente
                ClientHandler clientHandler = new ClientHandler(socket, this);
                // Agregar el cliente a la lista
                clientHandlers.add(clientHandler);
                // Enviar la lista de usuarios conectados a todos los clientes
                sendUserList();

                // Iniciar un hilo para gestionar el cliente de manera independiente
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error conectando el servidor: " + e.getMessage());
        }
    }

    // Método para cerrar el ServerSocket
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error cerrando el servidor: " + e.getMessage());
        }
    }

    // Método para enviar la lista de usuarios conectados a todos los clientes
    public void sendUserList() {
        StringBuilder userList = new StringBuilder("USERLIST ");
        for (ClientHandler clientHandler : clientHandlers) {
            userList.append(clientHandler.getClientUsername()).append(",");
        }

        // Enviar la lista a todos los clientes
        broadcastMessage(userList.toString());
    }

    // Método para enviar mensajes a todos los clientes conectados
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.sendMessage(messageToSend);
            } catch (IOException e) {
                System.out.println("Error enviando mensaje a " + clientHandler.getClientUsername() + ": " + e.getMessage());
            }
        }
    }

    // Método para eliminar un cliente de la lista y notificar a los demás
    public void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println(clientHandler.getClientUsername() + " ha salido del chat.");
        sendUserList(); // Actualiza la lista de usuarios conectados
    }

    public static void main(String[] args) throws IOException {
        // Crear una instancia de ServerSocket en el puerto 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        // Crear una instancia de la clase Server y comenzar a aceptar conexiones
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
