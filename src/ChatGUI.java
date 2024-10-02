import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ChatGUI extends JFrame {

    private JList<String> userList;
    private static DefaultListModel<String> listModel;
    private static JTextArea chatArea;
    private JTextArea msgInputArea;
    private JButton sendButton;
    private Client client;

    public ChatGUI(Client client) {
        this.client = client;
        // Configuración del JFrame principal
        setTitle("Chat Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo: Usuarios en línea
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL);
        userList.setVisibleRowCount(-1);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(200, 0));
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Usuarios en línea"));
        leftPanel.add(userScrollPane, BorderLayout.CENTER);

        // Panel derecho: Chat grupal
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        // Panel inferior: Enviar mensaje
        msgInputArea = new JTextArea(3, 50);
        JScrollPane msgInputScrollPane = new JScrollPane(msgInputArea);
        msgInputArea.setBorder(BorderFactory.createTitledBorder("Mensaje"));
        sendButton = new JButton("Enviar");

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(msgInputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Añadir componentes al JFrame principal
        add(leftPanel, BorderLayout.WEST);
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event listener para doble clic en un usuario
        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = userList.locationToIndex(e.getPoint());
                    String selectedUser = userList.getModel().getElementAt(index);
                    openPrivateChatWindow(selectedUser);
                }
            }
        });

        // Evento de botón de enviar mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = msgInputArea.getText().trim();
                if (!message.isEmpty()) {
                    client.sendMessage(message);
                    msgInputArea.setText(""); // Limpiar el área de texto después de enviar
                }
            }
        });

        setVisible(true);
    }

    // Método para abrir una nueva ventana de chat privado
    private void openPrivateChatWindow(String username) {
        JFrame privateChatFrame = new JFrame("Chat privado con " + username);
        privateChatFrame.setSize(400, 400);
        privateChatFrame.setLayout(new BorderLayout());

        JTextArea privateChatArea = new JTextArea();
        privateChatArea.setEditable(false);
        JScrollPane privateChatScrollPane = new JScrollPane(privateChatArea);
        privateChatFrame.add(privateChatScrollPane, BorderLayout.CENTER);

        JTextArea privateMsgInputArea = new JTextArea(2, 30);
        JScrollPane privateMsgScrollPane = new JScrollPane(privateMsgInputArea);
        JButton privateSendButton = new JButton("Enviar");

        // Botón para enviar archivos
        JButton fileButton = new JButton("Enviar archivo");

        // Crear panel con GridBagLayout para tener los botones organizados
        JPanel privateBottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Añadir campo de entrada de mensajes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        privateBottomPanel.add(privateMsgScrollPane, gbc);

        // Añadir botón de enviar archivo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        privateBottomPanel.add(fileButton, gbc);

        // Añadir botón de enviar mensaje
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        privateBottomPanel.add(privateSendButton, gbc);

        privateChatFrame.add(privateBottomPanel, BorderLayout.SOUTH);

        // Listener para enviar archivos
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(privateChatFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    long fileSizeInMB = selectedFile.length() / (1024 * 1024);

                    if (fileSizeInMB <= 50) {
                        privateChatArea.append("Enviando archivo: " + selectedFile.getName() + "\n");
                    } else {
                        JOptionPane.showMessageDialog(privateChatFrame, "El archivo excede el límite de 50 MB.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        privateChatFrame.setVisible(true);
    }

    // Método para obtener el área de chat
    public static JTextArea getChatArea() {
        return chatArea;
    }

    // Método para obtener el modelo de lista de usuarios
    public static DefaultListModel<String> getUserListModel() {
        return listModel;
    }
}
