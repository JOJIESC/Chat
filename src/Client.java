import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
            // Enviar el nombre de usuario al servidor
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            if (socket != null && !socket.isClosed()) {
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                SwingUtilities.invokeLater(() -> {
                    // Mostrar el mensaje enviado por el propio cliente en la interfaz
                    ChatGUI.getChatArea().append("Me: " + message + "\n");
                });
            } else {
                System.out.println("Socket no está conectado o está cerrado.");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el error en la consola para depuración
            closeEverything(socket, bufferedReader, bufferedWriter); // Cerrar recursos en caso de error
        }
    }

    // Método para recibir mensajes del servidor, incluyendo la lista de usuarios conectados
    public void listenForMessage() {
        new Thread(() -> {
            String msgFromServer;

            while (socket.isConnected()) {
                try {
                    msgFromServer = bufferedReader.readLine();

                    if (msgFromServer.startsWith("USERLIST")) {
                        // Mensaje especial para actualizar la lista de usuarios conectados
                        String[] userList = msgFromServer.replace("USERLIST ", "").split(",");
                        updateUserList(userList);
                    } else {
                        // Evitar mostrar el mensaje si es del mismo usuario (ya se mostró al enviarlo)
                        if (!msgFromServer.startsWith(username + ": ")) {
                            String finalMsgFromServer = msgFromServer;
                            SwingUtilities.invokeLater(() -> {
                                ChatGUI.getChatArea().append(finalMsgFromServer + "\n");
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    // Actualiza la lista de usuarios conectados en la GUI
    private void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            ChatGUI.getUserListModel().clear(); // Limpia la lista anterior
            for (String user : users) {
                if (!user.isEmpty()) {
                    ChatGUI.getUserListModel().addElement(user);
                }
            }
        });
    }

    // Cierra todos los recursos y muestra un mensaje si se pierde la conexión
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
}
