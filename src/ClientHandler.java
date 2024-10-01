//MANAGE INPUTS AND OUTPUTS
import java.io.*;
// CONNECT CLIENT AND SERVER
import java.net.Socket;
// LIST OF CLIENTS CONNECTED
import java.util.ArrayList;

/*THIS CLASS IMPLEMENTS RUNNABLE TO BE ABLE TO RUN
* THE CLASS ON A DIFFERENT THREAD*/
public class ClientHandler implements Runnable {

    // LIST TO TRACK ALL THE INSTANCES OF CLIENT HANDLER AND SEND MESSAGES TO ALL THE CLIENTS
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    // READ CLIENT MESSAGES
    private BufferedReader bufferedReader;
    // SEND MESSAGES TO CLIENT
    private BufferedWriter bufferedWriter;
    // SAVE THE CLIENT USERNAME
    private String clientUsername;

    // CONSTRUCTOR
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            // INITIALIZE THE BUFFERS TO READ AND WRITE THROUGH THE SOCKET
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // SAVE THE USERNAME BY INPUT CONSOLE
            this.clientUsername = bufferedReader.readLine();
            // ADD THE ACTUAL CLIENT TO THE LIST
            clientHandlers.add(this);
            // NOTIFY ALL CONNECTED CLIENTS
            broadcastMessage("Server: " + clientUsername + " has entered the chat");
        }catch (Exception e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // THIS METHOD HAVE THE LOGIC FOR EACH THREAD EXECUTED
    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket,bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // THIS METHOD SEND MESSAGES TO ALL CLIENTS ON THE "CLIENT HANDLERS" LIST
    // EXCEPT FOR THE SENDER
    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader,bufferedWriter);
            }
        }
    }

    // THIS METHOD DELETE THE ACTUAL CLIENT FROM THE "CLIENT HANDLER LIST"
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("Server: " + clientUsername + " has left the chat");
    }

    // THIS METHOD CLOSE EVERY RESOURCE (CLIENT SOCKET, BUFFERS AND THE CLIENT FROM THE LIST)
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
