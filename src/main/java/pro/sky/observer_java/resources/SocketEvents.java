package pro.sky.observer_java.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.util.messages.MessageBusConnection;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.ConnectedPanel;
import pro.sky.observer_java.InactivePanel;
import pro.sky.observer_java.constants.*;
import pro.sky.observer_java.events.EventManager;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.mapper.MarkdownAndHtml;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.Step;
import pro.sky.observer_java.scheduler.UpdateProjectScheduledSending;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SocketEvents {
    private final ResourceManager resourceManager;
    private final InactivePanel inactivePanel;
    private final ConnectedPanel connectedPanel;
    private Project openProject;
    private MessageBusConnection connection;
    private final IO.Options options = IO.Options
            .builder()
            .setForceNew(true)
            .setUpgrade(true)
            .setTransports(new String[]{"websocket"})
            .build();

    private final BubbleNotifications bubbleNotifications;

    private final Logger logger = Logger.getLogger(SocketEvents.class.getName());

    public SocketEvents(ResourceManager resourceManager, InactivePanel inactivePanel, ConnectedPanel connectedPanel) {
        this.resourceManager = resourceManager;
        this.inactivePanel = inactivePanel;
        this.connectedPanel = connectedPanel;
        this.bubbleNotifications = new BubbleNotifications();
    }

    private void connect() {
        socketConnectionEvents();
        resourceManager.getmSocket().connect();
    }

    public void createSocketWithListenersAndConnect(String url) {
        url = StringUtils.remove(url, " ");

        if (resourceManager.getmSocket() != null) {
            resourceManager.getmSocket().disconnect();
            resourceManager.resetMessageList();
        }
        try {
            resourceManager.setmSocket(IO.socket(new URI(url), options));
        } catch (URISyntaxException e) {
            logger.warning(e.getMessage());
            inactivePanel.getUrlField().setText("Wrong url syntax");
        }

        if (inactivePanel.getNameField().getText().equals(MessageTemplates.NAME_FIELD_DEFAULT_TEXT)) {
            bubbleNotifications.wrongName(openProject);
            return;
        }

        if (inactivePanel.getRoomIdField().getText().equals(MessageTemplates.ROOM_ID_FIELD_DEFAULT_TEXT) ||
                StringUtils.isAlpha(inactivePanel.getRoomIdField().getText())) {
            bubbleNotifications.wrongRoom(openProject);
            return;
        }

        connect();
    }

    private void socketConnectionEvents() {
        openProject = resourceManager.getToolWindow().getProject();

        resourceManager.getmSocket()
                .on(Socket.EVENT_CONNECT, this::eventConnect)
                .on(Socket.EVENT_DISCONNECT, this::eventDisconnect)
                .on(Socket.EVENT_CONNECT_ERROR, this::eventError)
                .on(CustomSocketEvents.ROOM_JOIN, this::eventRoomJoin)
                .on(CustomSocketEvents.SHARING_END, this::eventSharingEnd)
                .on(CustomSocketEvents.MESSAGE_TO_CLIENT, this::socketMessageEvents)
                .on(CustomSocketEvents.SHARING_START, this::codeSharingAndEventCatcher)
                .on(CustomSocketEvents.STEPS_ALL, this::stepsEvent)
                .on(CustomSocketEvents.SOLUTION_AI, this::solutionAiEvent)
                .on(CustomSocketEvents.PING, this::pingEvent)
                .on(CustomSocketEvents.SETTINGS, this::eventSettings)
                .on(CustomSocketEvents.ALERT, this::alertEvent)
                .on(CustomSocketEvents.STEPS_STATUS_TO_CLIENT, this::stepStatusToClient);
    }

    private void stepStatusToClient(Object... args) {
        String jsonObjectString;

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Step> steps;
        try {
            jsonObjectString = args[0].toString();
            steps = objectMapper
                    .readValue(jsonObjectString,
                            new TypeReference<>() {
                            });
        } catch (JsonProcessingException e) {
            System.out.println("STEP STATUS ERROR");
            logger.warning("STEP STATUS ERROR");
            throw new RuntimeException(e);
        }

        resourceManager.updateStepStatus(steps);
    }

    private void alertEvent(Object... args) {
        JSONObject jsonObject;
        String message;
        Alert alert;
        try {
            jsonObject = new JSONObject(args[0].toString());
            message = jsonObject.getString(JsonFields.MESSAGE);
            alert = Alert.valueOf(jsonObject.getString(JsonFields.TYPE));
        } catch (JSONException e) {
            logger.warning("Alert WARNING");
            throw new RuntimeException(e);
        }
        bubbleNotifications.createNotificationAndEmmit(alert,message, openProject);
    }

    private void pingEvent(Object... args) {
        resourceManager.getmSocket().emit(CustomSocketEvents.PING, new JSONObject());
    }

    private void solutionAiEvent(Object... args) {
        JSONObject jsonObject;
        String aiAnswer;
        try {
            jsonObject = new JSONObject(args[0].toString());
            aiAnswer = jsonObject.getString(JsonFields.CONTENT);
        } catch (JSONException e) {
            logger.warning("AI HELP RECEIVE WARNING");
            throw new RuntimeException(e);
        }

        connectedPanel.changeAiHelpTabName();
        connectedPanel.setAiHelpFieldText(MarkdownAndHtml.mdToHtml(aiAnswer));
    }

    private void eventConnect(Object... args) {

        resourceManager.setRoomId(Integer.valueOf(inactivePanel.getRoomIdField().getText()));

        resourceManager.setUserName(inactivePanel.getNameField().getText());

        JSONObject data = new JSONObject();
        try {
            data.put(JsonFields.ROOM_ID, resourceManager.getRoomId());
            data.put(JsonFields.NAME, resourceManager.getUserName());
            data.put(JsonFields.VERSION, Properties.VERSION);

        } catch (JSONException e) {
            logger.warning("Connected panel connect json - " + e.getMessage());
        }
        resourceManager.getmSocket().emit(CustomSocketEvents.ROOM_JOIN, data);
    }


    private void eventDisconnect(Object... args) {
        bubbleNotifications.disconnected(openProject);

        connectedPanel.setVisible(false);
        inactivePanel.setVisible(true);
        resourceManager.setWatching(false);
        connectedPanel.toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();

        resourceManager.refreshObserverIgnore();
        resourceManager.clearSteps();

        if (connection != null) {
            connection.disconnect();
        }

        JSONObject data = new JSONObject();
        try {
            data.put(JsonFields.ROOM_ID, Long.parseLong(inactivePanel.getRoomIdField().getText()));
        } catch (JSONException e) {
            logger.warning("Connected panel disconnect json - " + e.getMessage());
        }
        resourceManager.getmSocket().emit(CustomSocketEvents.ROOM_LEAVE, data);
    }

    private void eventError(Object... args) {
        bubbleNotifications.error(openProject);
    }

    private void eventRoomJoin(Object... args) {
        JSONObject message;
        try {
            message = new JSONObject(args[0].toString());
            resourceManager.setUserId(message.getInt(JsonFields.USER_ID));
        } catch (JSONException e) {
            logger.warning("Connected panel room/join json - " + e.getMessage());
        }
        bubbleNotifications.connected(openProject);
        inactivePanel.setVisible(false);
        connectedPanel.setVisible(true);
    }

    private void eventSharingEnd(Object... args) {
        resourceManager.setWatching(false);
        connectedPanel.toggleMentorStatusLabelText();

        resourceManager.getSes().shutdownNow();
        if (connection != null) {
            connection.disconnect();
        }
        Message message = new Message(
                SenderNames.WATCH_STATUS,
                LocalDateTime.now(),
                MessageTemplates.SHARING_END
        );
        resourceManager.addMessageToChatAndToList(message);
    }

    private void socketMessageEvents(Object... args) {
        JSONObject jsonMessage;
        Message message;

        try {
            //TODO test
            jsonMessage = new JSONObject(args[0].toString());

            message = new Message(
                    SenderNames.HOST,
                    LocalDateTime.now(),
                    jsonMessage.getString(JsonFields.CONTENT)
            );
            resourceManager.getConnectedPanel().addCounterNonActive();
            resourceManager.addMessageToChatAndToList(message);
            connectedPanel.scrollChatToBottom();
        } catch (JSONException e) {
            logger.warning("Connected panel message/to_client json - " + e.getMessage());
        }

    }

    private void codeSharingAndEventCatcher(Object... args) {
        resourceManager.setWatching(true);
        connectedPanel.toggleMentorStatusLabelText();
        FileStructureStringer fileStructureStringer = new FileStructureStringer(resourceManager);

        resourceManager.getmSocket()
                .emit(CustomSocketEvents.CODE_SEND,
                        fileStructureStringer
                                .getCodeSendJsonObjectFromString(fileStructureStringer.getProjectFilesJson(openProject)));

        activateVfsEventListenerAndScheduler();
        Message message = new Message(
                SenderNames.WATCH_STATUS,
                LocalDateTime.now(),
                MessageTemplates.SHARING_START
        );
        resourceManager.addMessageToChatAndToList(message);
    }

    private void stepsEvent(Object... args) {

        String data = args[0].toString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Step> steps;
        try {
            steps = objectMapper
                    .readValue(data,
                            new TypeReference<>() {
                            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        connectedPanel.setAllSteps(steps);
    }

    private void eventSettings(Object... args) {
        JSONObject jsonObject;
        ObserverIgnore observerIgnore = resourceManager.getObserverIgnore();
        try {
            jsonObject = new JSONObject(args[0].toString());

            observerIgnore.addToDirectories(
                    jsonObject.getJSONArray(jsonObject.getString(JsonFields.DIRS))
            );
            observerIgnore.addToNames(
                    jsonObject.getJSONArray(jsonObject.getString(JsonFields.NAMES))
            );
            observerIgnore.addToExtensions(
                    jsonObject.getJSONArray(JsonFields.EXTENSIONS)
            );
        } catch (JSONException e) {
            logger.warning("Settings JSON error");
        }
    }

    private void activateVfsEventListenerAndScheduler() {

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
}
