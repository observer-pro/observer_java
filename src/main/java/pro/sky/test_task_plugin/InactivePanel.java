package pro.sky.test_task_plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import io.socket.client.IO;
import io.socket.client.Socket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;

public class InactivePanel {
    private JTextField urlField;
    private JTextField roomIdField;
    private JTextField nameField;
    private JButton connectButton;
    private JLabel nameLabel;
    private JLabel roomLabel;
    private JLabel hostLabel;
    private JLabel titleLabel;
    private JPanel inactivePanel;
    Project openProject;


    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                openProject =   Resources.toolWindow.getProject();
                Resources.mSocket = IO.socket(SOCKET_URL, options);
                String id = "pro.sky";

                Notification balloonNotificationConnected =
                        new Notification(id, "Connected to socket!", NotificationType.IDE_UPDATE);
                balloonNotificationConnected.setTitle("Connection success");

                Notification balloonNotificationDisconnected =
                        new Notification(id, "Disconnected from socket!", NotificationType.WARNING);
                balloonNotificationDisconnected.setTitle("Disconnected!");

                Notification balloonNotificationError =
                        new Notification(id, "Disconnected from socket!", NotificationType.ERROR);
                balloonNotificationError.setTitle("Error connecting!");


                Resources.mSocket.on(Socket.EVENT_CONNECT, args -> {
                    balloonNotificationConnected.notify(openProject);

                    inactivePanel.setVisible(false);
                    Resources.roomId = roomIdField.getText();
                    Resources.userName = nameField.getText();
                    Resources.connectedPanel.getConnectedPanel().setVisible(true);
                    Resources.connectedPanel.setConnectionStatusLabelText(String.format("Connected to %s as %s", Resources.roomId , Resources.userName));

                });
                Resources.mSocket.on(Socket.EVENT_DISCONNECT, args -> balloonNotificationDisconnected.notify(openProject));
                Resources.mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> balloonNotificationDisconnected.notify(openProject));
                Resources.mSocket.connect();
            }
        });
        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
               urlField.setText("");
            }
        });

        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
               if(urlField.getText().equals("")){
                   urlField.setText("Enter url to connect to");
               }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                roomIdField.setText("");
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if(roomIdField.getText().equals("")){
                    roomIdField.setText("Enter room id");
                }
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                nameField.setText("");
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if(nameField.getText().equals("")){
                    nameField.setText("Enter room id");
                }
            }
        });
    }



    public JPanel getInactivePanel() {
        return inactivePanel;
    }


}
