import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket,String username) {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            if (socket != null && !socket.isClosed()) {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                SwingUtilities.invokeLater(() -> {
                    ChatGUI.getChatArea().append("Me: " + message + "\n");
                });
            } else {
                System.out.println("Socket is not connected or is closed.");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el error en la consola para depuración
            closeEverything(socket, bufferedReader, bufferedWriter); // Cerrar recursos en caso de error
        }
    }


    public void listenForMessage(){
        new Thread(new Runnable() {
             @Override
            public void run() {
                String msgFromGroupchat;

                 while(socket.isConnected()){
                     try{
                         msgFromGroupchat = bufferedReader.readLine();
                         String finalMsgFromGroupchat = msgFromGroupchat;
                         SwingUtilities.invokeLater(() -> {
                             ChatGUI.getChatArea().append(username+": " + finalMsgFromGroupchat + "\n");
                         });
                     }catch (IOException e){
                         e.printStackTrace();
                         closeEverything(socket, bufferedReader, bufferedWriter);
                     }
                 }
             }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
