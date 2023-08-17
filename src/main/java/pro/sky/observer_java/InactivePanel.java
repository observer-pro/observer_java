package pro.sky.observer_java;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.util.messages.MessageBusConnection;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.events.EventManager;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.mapper.ProjectFileMapper;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.scheduler.UpdateProjectScheduledSending;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private Project openProject;


    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSocketWithListenersAndConnect(/*SOCKET_URL*/URI.create(urlField.getText()));
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
                if (urlField.getText().isEmpty()) {
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
                if (roomIdField.getText().isEmpty()) {
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
                if (nameField.getText().isEmpty()) {
                    nameField.setText(NAME_FIELD_DEFAULT_TEXT);
                }
            }
        });
    }

    private void createSocketWithListenersAndConnect(URI uri) {
        if (ResourceManager.getmSocket() != null) {
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
        ResourceManager.getmSocket().on("message/to_client", args -> {
            JSONObject jsonMessage;
            try {
                jsonMessage = new JSONObject(args[0].toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Message message;
            try {
                message = new Message(
                        1L,
                        "HOST",
                        LocalDateTime.now(),
                        jsonMessage.getString("content")
                );
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

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

        ResourceManager.getmSocket()
                .on(Socket.EVENT_CONNECT, this::sendEventConnect)
                .on(Socket.EVENT_DISCONNECT, args -> {

                    balloonNotificationDisconnected.notify(openProject);
                    ResourceManager.getConnectedPanel().setVisible(false);
                    ResourceManager.getInactivePanel().setVisible(true);

                    ResourceManager.setWatching(false);
                    ResourceManager.getConnectedPanel().setMentorStatusLabelText();

                    ses.shutdown();

                }).on(Socket.EVENT_CONNECT_ERROR, args -> {
                    balloonNotificationError.notify(openProject);


                }).on("room/join", args -> {

                    JSONObject message;
                    try {
                        message = new JSONObject(args[0].toString());
                        ResourceManager.setUserId(message.getInt("user_id"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    balloonNotificationConnected.notify(openProject);
                    ResourceManager.getInactivePanel().setVisible(false);
                    ResourceManager.getConnectedPanel().setVisible(true);
                });
    }

    private void socketProjectRequestEvents() {
        ResourceManager.getmSocket().on("sharing/start", this::codeSharingAndEventCatcher);
    }

    public void setVisible(boolean toggle) {
        inactivePanel.setVisible(toggle);
    }

    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }

    private void sendEventConnect(Object... args) {

        ResourceManager.setRoomId(Integer.valueOf(roomIdField.getText()));
        ResourceManager.setUserName(nameField.getText());

        ResourceManager.getConnectedPanel().setConnectionStatusLabelText(
                String.format(CONNECTED_STATUS_TEXT_FORMAT, ResourceManager.getRoomId(), ResourceManager.getUserName())
        );

        JSONObject data = new JSONObject();
        try {
            data.put("room_id", Long.parseLong(roomIdField.getText()));
            data.put("name", nameField.getText());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ResourceManager.getmSocket().emit("room/join", data);
    }

    private void codeSharingAndEventCatcher(Object... args) {
        ResourceManager.setWatching(true);
        ResourceManager.getConnectedPanel().setMentorStatusLabelText();
        FileStructureStringer fileStructureStringer = new FileStructureStringer();

        ResourceManager.getmSocket().on("sharing/end", this::codeSharingEnd)
                .emit("sharing/code_send",
                        fileStructureStringer
                                .getJsonObjectFromString(fileStructureStringer.getProjectFilesList(openProject)));

        activateEditorEventListenerAndScheduler();
    }

    private void codeSharingEnd(Object... args) {
        ResourceManager.setWatching(false);
        ResourceManager.getConnectedPanel().setMentorStatusLabelText();
    }

    private void activateEditorEventListenerAndScheduler() {
        MessageBusConnection connection = openProject.getMessageBus().connect();

        ResourceManager.setEditorUpdateEvents(new ArrayList<>());
        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                EventManager eventManager = new EventManager();

                events.forEach(event -> {
                    System.out.println(event.getClass());
                    if (event instanceof VFileContentChangeEvent) {
                        System.out.println("ContentChangeEvent" + event);
                        eventManager.addContentChangeEventToEditorEventList((VFileContentChangeEvent) event);

                    } else if (event instanceof VFileCreateEvent) {
                        System.out.println("CreateEvent" + event);
                        eventManager.addCreateEventToEditorEventList((VFileCreateEvent) event);

                    } else if (event instanceof VFileDeleteEvent) {
                        System.out.println("DeleteEvent" + event);
                        eventManager.addDeleteEventToEditorEventList((VFileDeleteEvent) event);

                    } else if (event instanceof VFilePropertyChangeEvent) {
                        System.out.println("PropertyChangeEvent" + event);
                        eventManager.addPropertyChangeEventToEditorEventList((VFilePropertyChangeEvent) event);
                    }
                });

//                for (Object event : events) {
//                    if(event.getClass().equals(VFilePropertyChangeEvent.class))
//                    {
//                        VFilePropertyChangeEvent changeEvent = (VFilePropertyChangeEvent) event;
//                        changeEvent.
//                    }
//                    mapper.addRenameEventToProjectFileEventList(event);
//                }
            }
        });

        ses.scheduleAtFixedRate(new UpdateProjectScheduledSending(), 5, 5, TimeUnit.SECONDS);
    }
}
