package pro.sky.test_task_plugin;

import io.socket.engineio.client.Socket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

public class ConnectedPanel {
    private JTextField messageField;
    private JButton sendButton;
    private JButton stopSharingButton;
    private JLabel logoLabel;
    private JLabel mentorStatusLabel;
    private JTextArea chatArea;
    private JPanel connectedPanel;
    private JLabel connectionStatusLabel;
    private JScrollPane scroll;
    private JSeparator separator;

    private final String MESSAGE_STRING_FORMAT = "%s: %s\n";

    public ConnectedPanel() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messageField.addKeyListener(new KeyAdapter() {
        });
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    sendMessage();
                }
            }
        });
    }

    private void sendMessage(){
        String senderName = Resources.userName;
        String messageText = messageField.getText();
        chatArea.setText("");
        Resources.mSocket.emit(Socket.EVENT_MESSAGE, messageText);
        Message message = new Message(1L, senderName, LocalDateTime.now(), messageText);
        Resources.messageList.add(message);
        messageField.setText("");
        fillChatFieldWithMessages();
    }
    public JPanel getConnectedPanel() {
       return connectedPanel;
    }

    public void setVisible(boolean toggle){
        connectedPanel.setVisible(toggle);
    }

    public void setConnectionStatusLabelText(String text) {
        this.connectionStatusLabel.setText(text);
    }
    public void fillChatFieldWithMessages(){
        StringBuilder sb = new StringBuilder();
        for (Message m : Resources.messageList) {
            sb.append(String.format(MESSAGE_STRING_FORMAT, m.getSender(), m.getMessageText()));
        }
        chatArea.setText(sb.toString());
    }
}
