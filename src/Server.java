// MANAGE INPUTS AND OUTPUTS ERRORS
import java.io.IOException;
// CREATE A SERVER SOCKET
import java.net.ServerSocket;
// CONNECT SERVER - CLIENT
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            //WHILE SERVER IS OPEN, ACCEPT CONNECTIONS
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}

