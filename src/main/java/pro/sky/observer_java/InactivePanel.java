package pro.sky.observer_java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.util.messages.MessageBusConnection;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.constants.FieldTexts;
import pro.sky.observer_java.constants.JsonFields;
import pro.sky.observer_java.constants.MessageTemplates;
import pro.sky.observer_java.events.EventManager;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.Step;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.scheduler.UpdateProjectScheduledSending;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
    private JTextPane instructionTextPane;
    private Notification balloonNotificationConnected;
    private Notification balloonNotificationDisconnected;
    private Notification balloonNotificationError;
    private Notification balloonNotificationWrongName;
    private Notification balloonNotificationWrongRoom;
//    private Notification balloonNotificationSharingStarted;
//    private Notification balloonNotificationSharingStopped;
    private Project openProject;
    private MessageBusConnection connection;
    private final ResourceManager resourceManager;

    private final Logger logger = Logger.getLogger(InactivePanel.class.getName());

    private final IO.Options options = IO.Options
            .builder()
            .setForceNew(true)
            .setUpgrade(true)
            .setTransports(new String[]{"websocket"})
            .build();

    public InactivePanel(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;

        connectButton.addActionListener(e ->
                 createSocketWithListenersAndConnect(urlField.getText())
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
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isAlphabetic(c)) {
                    e.consume();
                }
                if (e.isActionKey()) {
                    e.consume();
                }
            }
        });
    }


    private void createSocketWithListenersAndConnect(String url) {

        configureBubbles();

        url = StringUtils.remove(url, " ");

        if (resourceManager.getmSocket() != null) {
            resourceManager.getmSocket().disconnect();
            resourceManager.setMessageList(new ArrayList<>());
        }
        try {
            resourceManager.setmSocket(IO.socket(new URI(url), options));
        } catch (URISyntaxException e) {
            logger.warning(e.getMessage());
            urlField.setText("Wrong url syntax");
        }

        if(nameField.getText().equals(MessageTemplates.NAME_FIELD_DEFAULT_TEXT)){
            balloonNotificationWrongName.notify(openProject);
            return;
        }

        if(roomIdField.getText().equals(MessageTemplates.ROOM_ID_FIELD_DEFAULT_TEXT) ||
                StringUtils.isAlpha(roomIdField.getText())){
            balloonNotificationWrongRoom.notify(openProject);
            return;
        }

        socketConnectionEvents();

        socketMessageEvents();

        socketProjectRequestEvents();

        excessiveEvent();

        stepsEvent();

        resourceManager.getmSocket().connect();
    }
    private void stepsEvent() {
        resourceManager.getmSocket().on(CustomSocketEvents.STEPS_ALL, args -> {
            String data = args[0].toString();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Step> steps;
            try {
                steps = objectMapper
                        .readValue(data,
                                new TypeReference<>(){});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            resourceManager.getConnectedPanel().setAllSteps(steps);
        });
    }

    private void excessiveEvent() {
        resourceManager.getmSocket().on(CustomSocketEvents.EXERCISE, args -> {
            JSONObject jsonObject;
            String taskCode;
            String parseLanguage;
            try {
                jsonObject = new JSONObject(args[0].toString());
                taskCode = jsonObject.getString(JsonFields.CONTENT);
                parseLanguage = jsonObject.getString(JsonFields.PARSE_LANGUAGE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            resourceManager.getConnectedPanel().setExerciseText(taskCode, parseLanguage);
        });
    }

    private void socketMessageEvents() {
        resourceManager.getmSocket().on(CustomSocketEvents.MESSAGE_TO_CLIENT, args -> {

            if (resourceManager.getConnectedPanel().getChatArea().getText().equals(FieldTexts.NO_MESSAGES)) {
                resourceManager.getConnectedPanel().getChatArea().setText("");
            }

            JSONObject jsonMessage;
            Message message;

            try {
                //TODO test
                jsonMessage = new JSONObject(args[0].toString());

                message = new Message(
                        "HOST",
                        LocalDateTime.now(),
                        jsonMessage.getString(JsonFields.CONTENT)
                );
                resourceManager.getConnectedPanel().appendChat(
                        String.format(MessageTemplates.MESSAGE_STRING_FORMAT, "HOST", message.getMessageText())
                );
                resourceManager.getMessageList().add(message);
            } catch (JSONException e) {
                logger.warning("Connected panel message/to_client json - " + e.getMessage());
            }
        });
    }

    private void socketConnectionEvents() {
        openProject = resourceManager.getToolWindow().getProject();

        resourceManager.getmSocket()
                .on(Socket.EVENT_CONNECT, this::eventConnect)
                .on(Socket.EVENT_DISCONNECT, this::eventDisconnect)
                .on(Socket.EVENT_CONNECT_ERROR, this::eventError)
                .on(CustomSocketEvents.ROOM_JOIN, this::eventRoomJoin)
                .on(CustomSocketEvents.SHARING_END, this::eventSharingEnd)
                .on(CustomSocketEvents.ROOM_CLOSED, this::eventDisconnect)
                .on(CustomSocketEvents.SETTINGS, this::eventSettings);
    }

    private void eventSettings(Object... args) {
        //TODO Decide how to settings

    }

    private void eventRoomJoin(Object... args) {
        JSONObject message;
        try {
            //TODO Test
            message = new JSONObject(args[0].toString());
            resourceManager.setUserId(message.getInt(JsonFields.USER_ID));
        } catch (JSONException e) {
            logger.warning("Connected panel room/join json - " + e.getMessage());
        }
        balloonNotificationConnected.notify(openProject);
        resourceManager.getInactivePanel().setVisible(false);
        resourceManager.getConnectedPanel().setVisible(true);
    }

    private void eventError(Object... args) {
        balloonNotificationError.notify(openProject);
    }

    private void eventDisconnect(Object... args) {
//        if (resourceManager.isWatching()) {
//            balloonNotificationSharingStopped.notify(openProject);
//        }
        balloonNotificationDisconnected.notify(openProject);

        resourceManager.getConnectedPanel().setVisible(false);
        resourceManager.getInactivePanel().setVisible(true);
        resourceManager.setWatching(false);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();

        resourceManager.refreshObserverIgnore();

        if (connection != null) {
            connection.disconnect();
        }

        JSONObject data = new JSONObject();
        try {
            data.put(JsonFields.ROOM_ID, Long.parseLong(roomIdField.getText()));
        } catch (JSONException e) {
            logger.warning("Connected panel disconnect json - " + e.getMessage());
        }
        resourceManager.getmSocket().emit(CustomSocketEvents.ROOM_LEAVE, data);
    }

    private void eventConnect(Object... args) {

        resourceManager.setRoomId(Integer.valueOf(roomIdField.getText()));

        resourceManager.setUserName(nameField.getText());

//        resourceManager.getConnectedPanel().setConnectionStatusLabelText(
//                String.format(
//                        MessageTemplates.CONNECTED_STATUS_TEXT_FORMAT,
//                        resourceManager.getRoomId(),
//                        resourceManager.getUserName()
//                )
//        );

        JSONObject data = new JSONObject();
        try {
            data.put(JsonFields.ROOM_ID, resourceManager.getRoomId());
            data.put(JsonFields.NAME, resourceManager.getUserName());

        } catch (JSONException e) {
            logger.warning("Connected panel connect json - " + e.getMessage());
        }
        resourceManager.getmSocket().emit(CustomSocketEvents.ROOM_JOIN, data);
    }

    private void eventSharingEnd(Object... args) {
        resourceManager.setWatching(false);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();
        if (connection != null) {
            connection.disconnect();
        }
        //TODO NOTIFY TO CHAT
        Message message = new Message(
                "HOST",
                LocalDateTime.now(),
                "Your sharing session started. Mentor is now watching"
        );
        resourceManager.getConnectedPanel().appendChat(
                String.format(MessageTemplates.MESSAGE_STRING_FORMAT, "HOST", message.getMessageText())
        );
       // balloonNotificationSharingStopped.notify(openProject);
    }

    private void socketProjectRequestEvents() {
        resourceManager.getmSocket()
                .on(CustomSocketEvents.SHARING_START, this::codeSharingAndEventCatcher);
                // .on(CustomSocketEvents.EXERCISE_RESET, this::exerciseReset);
    }

//    private void exerciseReset(Object... objects) {
//        resourceManager.getConnectedPanel().setAllNoneAndSend();
//    }


    private void codeSharingAndEventCatcher(Object... args) {
        resourceManager.setWatching(true);
        resourceManager.getConnectedPanel().toggleMentorStatusLabelText();
        FileStructureStringer fileStructureStringer = new FileStructureStringer(resourceManager);

        resourceManager.getmSocket()
                .emit(CustomSocketEvents.CODE_SEND,
                        fileStructureStringer
                                .getCodeSendJsonObjectFromString(fileStructureStringer.getProjectFilesJson(openProject)));

        activateEditorEventListenerAndScheduler();
        //TODO NOTIFY TO CHAT
        Message message = new Message(
                "HOST",
                LocalDateTime.now(),
                "Your sharing session STOPPED. Mentor is now NOT watching"
        );
        resourceManager.getConnectedPanel().appendChat(
                String.format(MessageTemplates.MESSAGE_STRING_FORMAT, "HOST", message.getMessageText())
        );
       // balloonNotificationSharingStarted.notify(openProject);
      //  resourceManager.getConnectedPanel().setAllNoneButDoneAndSend();
    }

    private void activateEditorEventListenerAndScheduler() {

        connection = openProject.getMessageBus().connect();
        connection.deliverImmediately();

        resourceManager.setEditorUpdateEvents(new ArrayList<>());
        resourceManager.setSes(Executors.newSingleThreadScheduledExecutor());

        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            final EventManager eventManager = new EventManager(resourceManager);

            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                events.forEach(event -> {
                    if (event instanceof VFileDeleteEvent) {
                        System.out.println("DeleteEvent" + event);
                        logger.info("DeleteEvent - " + event);
                        eventManager.addDeleteEventToEditorEventList((VFileDeleteEvent) event);
                    }
                });
            }

            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                events.forEach(event -> {
                    if (!ProjectFileIndex.getInstance(openProject).isInContent(Objects.requireNonNull(event.getFile()))) {
                        return;
                    }

                    logger.info("Event Caught - " + event);
                    System.out.println("AFTER Event Caught - " + event);
                    if (event instanceof VFileContentChangeEvent) {
                        System.out.println("ContentChangeEvent" + event);
                        logger.info("ContentChangeEvent - " + event);
                        eventManager.addContentChangeEventToEditorEventList((VFileContentChangeEvent) event);

                    } else if (event instanceof VFileCreateEvent) {
                        System.out.println("CreateEvent" + event);
                        logger.info("CreateEvent - " + event);
                        eventManager.addCreateEventToEditorEventList((VFileCreateEvent) event);
                    } else if (event instanceof VFilePropertyChangeEvent) {
                        System.out.println("PropertyChangeEvent " + event);
                        logger.info("PropertyChangeEvent - " + event);
                        eventManager.addPropertyChangeEventToEditorEventList((VFilePropertyChangeEvent) event);

                    } else if (event instanceof VFileMoveEvent) {
                        System.out.println("MoveEvent" + event);
                        logger.info("MoveEvent - " + event);
                        eventManager.addMoveEventToEditorEventList((VFileMoveEvent) event);
                    }
                });
            }
        });


        resourceManager.getSes()
                .scheduleAtFixedRate(
                        new UpdateProjectScheduledSending(resourceManager, openProject),
                        5,
                        1,
                        TimeUnit.SECONDS);
    }

    private void configureBubbles() {
        String groupId = "pro.sky";

        balloonNotificationConnected =
                new Notification(groupId, "Connected to socket!", NotificationType.IDE_UPDATE);
        balloonNotificationConnected.setTitle("Connection success");

        balloonNotificationDisconnected =
                new Notification(groupId, "Disconnected from socket!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Disconnected!");

        balloonNotificationError =
                new Notification(groupId, "Error connecting to socket!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationWrongName =
                new Notification(groupId, "Name not filled!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationWrongRoom =
                new Notification(groupId, "Room not filled!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

//        balloonNotificationSharingStarted =
//                new Notification(groupId, "Your sharing session started. Mentor is now watching", NotificationType.INFORMATION);
//        balloonNotificationError.setTitle("Sharing started");

//        balloonNotificationSharingStopped =
//                new Notification(groupId, "Your sharing session stopped. Mentor is not watching", NotificationType.WARNING);
//        balloonNotificationError.setTitle("Sharing stopped");
    }

    public void setVisible(boolean toggle) {
        inactivePanel.setVisible(toggle);
    }

    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }
}
