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
import pro.sky.observer_java.model.CustomSocketEvents;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.MessageTemplates;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.scheduler.UpdateProjectScheduledSending;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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
    String groupId = "pro.sky";
    Notification balloonNotificationConnected;
    Notification balloonNotificationDisconnected;
    Notification balloonNotificationError;
    Notification balloonNotificationSharingStarted;
    Notification balloonNotificationSharingStopped;

    private Project openProject;

    private MessageBusConnection connection;

    private final ResourceManager resourceManager;

    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;

        connectButton.addActionListener(e ->
                createSocketWithListenersAndConnect(/*SOCKET_URL*/URI.create(urlField.getText()))
        );

        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals(MessageTemplates.URL_FIELD_DEFAULT_TEXT)) {
                    urlField.setText("");
                }
            }
        });
        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (urlField.getText().isEmpty()) {
                    urlField.setText(MessageTemplates.URL_FIELD_DEFAULT_TEXT);
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (roomIdField.getText().equals(MessageTemplates.ROOM_ID_FIELD_DEFAULT_TEXT)) {
                    roomIdField.setText("");
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (roomIdField.getText().isEmpty()) {
                    roomIdField.setText(MessageTemplates.ROOM_ID_FIELD_DEFAULT_TEXT);
                }
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(MessageTemplates.NAME_FIELD_DEFAULT_TEXT)) {
                    nameField.setText("");
                }
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText(MessageTemplates.NAME_FIELD_DEFAULT_TEXT);
                }
            }
        });
    }
    private void createSocketWithListenersAndConnect(URI uri) {
        if (resourceManager.getmSocket() != null) {
            resourceManager.getmSocket().disconnect();
            resourceManager.setMessageList(new ArrayList<>());
        }
        resourceManager.setmSocket(IO.socket(uri, options));

        configureBubbles();

        socketConnectionEvents();

        socketMessageEvents();

        socketProjectRequestEvents();

        resourceManager.getmSocket().connect();
    }
    private void socketMessageEvents() {
        resourceManager.getmSocket().on("message/to_client", args -> {
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
                        "Teacher",
                        LocalDateTime.now(),
                        jsonMessage.getString("content")
                );
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            resourceManager.getConnectedPanel().appendChat(String.format(MessageTemplates.MESSAGE_STRING_FORMAT, "SOCKET", message.getMessageText()));
            resourceManager.getMessageList().add(message);
        });
    }
    private void socketConnectionEvents() {
        openProject = resourceManager.getToolWindow().getProject();

        resourceManager.getmSocket()
                .on(Socket.EVENT_CONNECT, this::eventConnect)
                .on(Socket.EVENT_DISCONNECT, this::eventDisconnect)
                .on(Socket.EVENT_CONNECT_ERROR, this::eventError)
                .on(CustomSocketEvents.ROOM_JOIN, this::eventRoomJoin)
                .on(CustomSocketEvents.SHARING_END, this::eventSharingEnd);
    }
    private void eventRoomJoin(Object... args){
        JSONObject message;
        try {
            message = new JSONObject(args[0].toString());
            resourceManager.setUserId(message.getInt("user_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        balloonNotificationConnected.notify(openProject);
        resourceManager.getInactivePanel().setVisible(false);
        resourceManager.getConnectedPanel().setVisible(true);
    }
    private void eventError(Object... args){
        balloonNotificationError.notify(openProject);
    }
    private void eventDisconnect(Object... args){
        if (resourceManager.isWatching()) {
            balloonNotificationSharingStopped.notify(openProject);
        }
        balloonNotificationDisconnected.notify(openProject);

        resourceManager.getConnectedPanel().setVisible(false);
        resourceManager.getInactivePanel().setVisible(true);
        resourceManager.setWatching(false);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();

        if (connection != null) {
            connection.disconnect();
        }

        JSONObject data = new JSONObject();
        try {
            data.put("room_id", Long.parseLong(roomIdField.getText()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        resourceManager.getmSocket().emit("room/leave", data);
    }
    private void eventConnect(Object... args) {

        resourceManager.setRoomId(Integer.valueOf(roomIdField.getText()));
        resourceManager.setUserName(nameField.getText());

        resourceManager.getConnectedPanel().setConnectionStatusLabelText(
                String.format(
                        MessageTemplates.CONNECTED_STATUS_TEXT_FORMAT,
                        resourceManager.getRoomId(),
                        resourceManager.getUserName()
                )
        );

        JSONObject data = new JSONObject();
        try {
            data.put("room_id", Long.parseLong(roomIdField.getText()));
            data.put("name", nameField.getText());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        resourceManager.getmSocket().emit("room/join", data);
    }
    private void eventSharingEnd(Object... args) {
        resourceManager.setWatching(false);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();
        if (connection != null) {
            connection.disconnect();
        }

        balloonNotificationSharingStopped.notify(openProject);
    }
    private void socketProjectRequestEvents() {
        resourceManager.getmSocket().on(CustomSocketEvents.SHARING_START, this::codeSharingAndEventCatcher);
    }
    private void codeSharingAndEventCatcher(Object... args) {
        resourceManager.setWatching(true);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();
        FileStructureStringer fileStructureStringer = new FileStructureStringer(resourceManager);

        resourceManager.getmSocket()
                .emit("sharing/code_send",
                        fileStructureStringer
                                .getJsonObjectFromString(fileStructureStringer.getProjectFilesList(openProject)));

        activateEditorEventListenerAndScheduler();
        balloonNotificationSharingStarted.notify(openProject);
    }
    private void activateEditorEventListenerAndScheduler() {

        connection = openProject.getMessageBus().connect();
        connection.deliverImmediately();

        resourceManager.setEditorUpdateEvents(new ArrayList<>());
        resourceManager.setSes(Executors.newSingleThreadScheduledExecutor());

        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                EventManager eventManager = new EventManager(resourceManager);

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

                    } else if (event instanceof VFileMoveEvent) {
                        System.out.println("MoveEvent" + event);
                        eventManager.addMoveEventToEditorEventList((VFileMoveEvent) event);

                    }
                });
            }
        });


        resourceManager.getSes()
                .scheduleAtFixedRate(new UpdateProjectScheduledSending(resourceManager), 5, 5, TimeUnit.SECONDS);
    }
    private void configureBubbles() {
        balloonNotificationConnected =
                new Notification(groupId, "Connected to socket!", NotificationType.IDE_UPDATE);
        balloonNotificationConnected.setTitle("Connection success");

        balloonNotificationDisconnected =
                new Notification(groupId, "Disconnected from socket!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Disconnected!");

        balloonNotificationError =
                new Notification(groupId, "Error connecting to socket!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationSharingStarted =
                new Notification(groupId, "Your sharing session started. Mentor is now watching", NotificationType.INFORMATION);
        balloonNotificationError.setTitle("Sharing started");

        balloonNotificationSharingStopped =
                new Notification(groupId, "Your sharing session stopped. Mentor is not watching", NotificationType.WARNING);
        balloonNotificationError.setTitle("Sharing stopped");
    }
    public void setVisible(boolean toggle) {
        inactivePanel.setVisible(toggle);
    }
    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }
}
