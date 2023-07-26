package pro.sky.observer_java;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import io.socket.client.IO;
import io.socket.client.Socket;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    private final String URL_FIELD_DEFAULT_TEXT = "Enter url to connect to";
    private final String ROOM_ID_FIELD_DEFAULT_TEXT = "Enter room id";
    private final String NAME_FIELD_DEFAULT_TEXT = "Enter name to display in chat";
    private final String CONNECTED_STATUS_TEXT_FORMAT = "Connected to %s as %s";

    private final String MESSAGE_STRING_FORMAT = "%s: %s\n";
    Project openProject;


    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSocketWithListenersAndConnect(SOCKET_URL);
            }
        });


        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals(URL_FIELD_DEFAULT_TEXT)) {
                    urlField.setText("");
                }
            }
        });

        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (urlField.getText().equals("")) {
                    urlField.setText(URL_FIELD_DEFAULT_TEXT);
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (roomIdField.getText().equals(ROOM_ID_FIELD_DEFAULT_TEXT)) {
                    roomIdField.setText("");
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (roomIdField.getText().equals("")) {
                    roomIdField.setText(ROOM_ID_FIELD_DEFAULT_TEXT);
                }
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(NAME_FIELD_DEFAULT_TEXT)) {
                    nameField.setText("");
                }
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().equals("")) {
                    nameField.setText(NAME_FIELD_DEFAULT_TEXT);
                }
            }
        });
    }

    private void createSocketWithListenersAndConnect(URI uri) {
        if(ResourceManager.getmSocket() != null){
            ResourceManager.getmSocket().disconnect();
            ResourceManager.setMessageList(new ArrayList<>());
        }
        ResourceManager.setmSocket(IO.socket(uri, options));
        socketConnectionEventsWithBubbles();
        socketMessageEvents();
        socketProjectRequestEvents();

        ResourceManager.getmSocket().connect();
    }

    private void socketMessageEvents() {
        ResourceManager.getmSocket().on(io.socket.engineio.client.Socket.EVENT_MESSAGE, args -> {
            Message message = new Message(
                    1L,
                    "SOCKET",
                    LocalDateTime.now(),
                    args[0].toString()
            );
            ResourceManager.getConnectedPanel().appendChat(String.format(MESSAGE_STRING_FORMAT, "SOCKET", message.getMessageText()));
            ResourceManager.getMessageList().add(message);
        });
    }


    private void socketConnectionEventsWithBubbles() {
        openProject = ResourceManager.getToolWindow().getProject();
        String id = "pro.sky.observer";

        Notification balloonNotificationConnected =
                new Notification(id, "Connected to socket!", NotificationType.IDE_UPDATE);
        balloonNotificationConnected.setTitle("Connection success");

        Notification balloonNotificationDisconnected =
                new Notification(id, "Disconnected from socket!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Disconnected!");

        Notification balloonNotificationError =
                new Notification(id, "Error connecting to socket!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        ResourceManager.getmSocket().on(Socket.EVENT_CONNECT, args -> {
            balloonNotificationConnected.notify(openProject);

            ResourceManager.getInactivePanel().setVisible(false);
            ResourceManager.setRoomId(roomIdField.getText());
            ResourceManager.setUserName(nameField.getText());

            ResourceManager.getConnectedPanel().setConnectionStatusLabelText(
                    String.format(CONNECTED_STATUS_TEXT_FORMAT, ResourceManager.getRoomId(), ResourceManager.getUserName())
            );

            ResourceManager.getConnectedPanel().setMentorStatusLabelText();
            ResourceManager.getConnectedPanel().setVisible(true);


            socketProjectRequestEvents(); //TODO TMP!!!

        });
        ResourceManager.getmSocket().on(Socket.EVENT_DISCONNECT, args -> {
            balloonNotificationDisconnected.notify(openProject);
            ResourceManager.getConnectedPanel().setVisible(false);
            ResourceManager.getInactivePanel().setVisible(true);
        });
        ResourceManager.getmSocket().on(Socket.EVENT_CONNECT_ERROR, args ->
                balloonNotificationError.notify(openProject));
    }

    private void socketProjectRequestEvents() {
        ResourceManager.getmSocket().on("initial_file_transfer", args -> {
            ResourceManager.setWatching(true);
            ResourceManager.getConnectedPanel().setMentorStatusLabelText();
            FileStructureStringer fileStructureStringer = new FileStructureStringer();
            ResourceManager.getmSocket().emit("project_json", fileStructureStringer.getProjectFilesList(openProject));
        });
    }

    public void setVisible(boolean toggle){
        inactivePanel.setVisible(toggle);
    }

    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }


}
