import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ChatGUI extends JFrame {

    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JTextArea chatArea;
    private JTextArea msgInputArea;
    private JButton sendButton;

    public ChatGUI() {
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
        leftPanel.setBorder(BorderFactory.createTitledBorder("Online Users"));
        leftPanel.add(userScrollPane, BorderLayout.CENTER);

        // Panel derecho: Chat grupal
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        // Panel inferior: Enviar mensaje
        msgInputArea = new JTextArea(3, 50);
        JScrollPane msgInputScrollPane = new JScrollPane(msgInputArea);
        msgInputArea.setBorder(BorderFactory.createTitledBorder("Message"));
        sendButton = new JButton("Send");

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

        // Añadir usuarios de prueba a la lista
        populateUserList();

        setVisible(true);
    }

    // Método para abrir una nueva ventana de chat privado
    private void openPrivateChatWindow(String username) {
        JFrame privateChatFrame = new JFrame("Private Chat with " + username);
        privateChatFrame.setSize(400, 400);
        privateChatFrame.setLayout(new BorderLayout());

        JTextArea privateChatArea = new JTextArea();
        privateChatArea.setEditable(false);
        JScrollPane privateChatScrollPane = new JScrollPane(privateChatArea);
        privateChatFrame.add(privateChatScrollPane, BorderLayout.CENTER);

        JTextArea privateMsgInputArea = new JTextArea(2, 30);
        JScrollPane privateMsgScrollPane = new JScrollPane(privateMsgInputArea);
        JButton privateSendButton = new JButton("Send");

        // Botón para enviar archivos
        JButton fileButton = new JButton("Send File");

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
                        // Aquí puedes agregar la lógica para enviar el archivo
                        privateChatArea.append("Sending file: " + selectedFile.getName() + "\n");
                    } else {
                        JOptionPane.showMessageDialog(privateChatFrame, "File size exceeds 50MB limit.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        privateChatFrame.setVisible(true);
    }

    // Método para agregar algunos usuarios de prueba a la lista
    private void populateUserList() {
        listModel.addElement("User 1");
        listModel.addElement("User 2");
        listModel.addElement("User 3");
        listModel.addElement("User 4");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}
