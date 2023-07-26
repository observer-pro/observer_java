package pro.sky.observer_java;

import com.intellij.openapi.project.Project;
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
                if(sendButton.getText().equals("")){
                    return;
                }
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
        stopSharingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResourceManager.getmSocket().disconnect();
            }
        });
    }

    private void sendMessage(){
        String senderName = ResourceManager.getUserName();
        String messageText = messageField.getText();
        if(chatArea.getText().equals("No messages")){
            chatArea.setText("");
        }
        chatArea.append(String.format(MESSAGE_STRING_FORMAT,senderName,messageText));
        ResourceManager.getmSocket().emit(Socket.EVENT_MESSAGE, messageText);
        Message message = new Message(1L, senderName, LocalDateTime.now(), messageText);
        ResourceManager.getMessageList().add(message);
        messageField.setText("");
       // fillChatFieldWithMessages();

        //TODO REMOVE THIS IS FOR TESTING
        Project openProject = ResourceManager.getToolWindow().getProject();
        FileStructureStringer fileStructureStringer = new FileStructureStringer();
        ResourceManager.getmSocket().emit(Socket.EVENT_MESSAGE/*"project_json"*/, fileStructureStringer.getProjectFilesList(openProject));
    }
    public JPanel getConnectedJPanel() {
       return connectedPanel;
    }

    public void setVisible(boolean toggle){
        connectedPanel.setVisible(toggle);
    }

    public void setConnectionStatusLabelText(String text) {
        this.connectionStatusLabel.setText(text);
    }

    public void setMentorStatusLabelText() {
        if(ResourceManager.isWatching()){
            mentorStatusLabel.setText("Mentor is watching");
            return;
        }
        mentorStatusLabel.setText("Mentor in not watching");
    }

    public void appendChat(String string){
        chatArea.append(string);
    }
}
