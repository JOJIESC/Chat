// MANAGE INPUTS AND OUTPUTS ERRORS
import java.io.IOException;
// CREATE A SERVER SOCKET
import java.net.ServerSocket;
// CONNECT SERVER - CLIENT
import java.net.Socket;

public class Server {
    // Variable to access serverSocket
    private ServerSocket serverSocket;

    // Constructor to start the server
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // This method is in charge to start the server and manage client connections
    public void startServer(){
        try{
            //WHILE SERVER IS OPEN, ACCEPT CONNECTIONS
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                // Once a connection is established, we create an instance of clientHandler
                // is in charge to manage the client communication
                ClientHandler clientHandler = new ClientHandler(socket);
                // Start a thread to manage each client independently
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error connecting server" + e.getMessage());
        }
    }

    // METHOD TO CLOSE SERVER SOCKET
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // CREATE A NEW INSTANCE OF SERVER SOCKET ON PORT 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        // CREATE A NEW INSTANCE OF SERVER CLASS AND START TO ACCEPT NEW CONNECTIONS
        Server server = new Server(serverSocket);
        server.startServer();
    }
}

