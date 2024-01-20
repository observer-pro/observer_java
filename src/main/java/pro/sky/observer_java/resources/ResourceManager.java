package pro.sky.observer_java.resources;

import com.intellij.openapi.wm.ToolWindow;
import io.socket.client.Socket;
import pro.sky.observer_java.ConnectedPanel;
import pro.sky.observer_java.InactivePanel;
import pro.sky.observer_java.constants.*;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.model.Step;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ResourceManager {
    private static ResourceManager instance;
    private volatile ConnectedPanel connectedPanel;
    private volatile InactivePanel inactivePanel;

    private volatile Integer roomId;

    private volatile String userName;

    private volatile ToolWindow toolWindow;

    private String currentSelectedTask = "No Task";

    private int chatCounter = 0;

    private Socket mSocket;

    private volatile Integer userId;

    private List<ProjectFile> editorUpdateEvents;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    private Map<String, Step> stepMap = new HashMap<>();

    private ObserverIgnore observerIgnore = new ObserverIgnore();

    public static synchronized ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public ObserverIgnore getObserverIgnore() {
        return observerIgnore;
    }

    public void refreshObserverIgnore() {
        this.observerIgnore = new ObserverIgnore();
    }

    public ScheduledExecutorService getSes() {
        return ses;
    }

    public List<ProjectFile> getEditorUpdateEvents() {
        return editorUpdateEvents;
    }

    public void setEditorUpdateEvents(List<ProjectFile> editorUpdateEvents) {
        this.editorUpdateEvents = editorUpdateEvents;
    }

    public String getCurrentSelectedTask() {
        return currentSelectedTask;
    }

    public void setCurrentSelectedTask(String currentSelectedTask) {
        this.currentSelectedTask = currentSelectedTask;
    }

    public void clearEditorUpdateEvents() {
        this.editorUpdateEvents.clear();
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private List<Message> allMessageList = new ArrayList<>();

    private volatile boolean watching = false;

    public ConnectedPanel getConnectedPanel() {
        return connectedPanel;
    }

    public void setConnectedPanel(ConnectedPanel connectedPanel) {
        this.connectedPanel = connectedPanel;
    }

    public InactivePanel getInactivePanel() {
        return inactivePanel;
    }

    public void setInactivePanel(InactivePanel inactivePanel) {
        this.inactivePanel = inactivePanel;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }


    public Socket getmSocket() {
        return mSocket;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public List<Message> getAllMessageList() {
        return allMessageList;
    }

    public void resetMessageList() {
        this.allMessageList.clear();
    }

    public boolean isWatching() {
        return watching;
    }

    public void setWatching(boolean watching) {
        this.watching = watching;
    }

    public void setSes(ScheduledExecutorService scheduledExecutorService) {
        this.ses = scheduledExecutorService;
    }

    public void setSteps(Map<String, Step> stepMap) {
        for (Map.Entry<String, Step> stringStepEntry : this.stepMap.entrySet()) {
            String stepEntryKey = stringStepEntry.getKey();
            if(!stepMap.containsKey(stepEntryKey)){
                this.stepMap.remove(stepEntryKey);
            }
        }
        for (Map.Entry<String, Step> stepEntry : stepMap.entrySet()) {
            String stepEntryKey = stepEntry.getKey();
            if (this.stepMap.containsKey(stepEntryKey)) {
                this.stepMap.get(stepEntryKey).setContent(stepEntry.getValue().getContent());
            }else{
                this.stepMap.put(stepEntry.getKey(),stepEntry.getValue());
            }
        }
    }

    public List<Step> getStepsList() {
        return stepMap.values().stream().sorted(Comparator.comparing(Step::getName)).toList();
    }

    public Map<String, Step> getStepsMap() {
        return stepMap;
    }

    public int getChatCounter() {
        return chatCounter;
    }

    public void setChatCounter(int chatCounter) {
        this.chatCounter = chatCounter;
    }

    public void clearSteps() {
        this.stepMap.clear();
    }

    public void updateStepStatus(Map<String, StepStatus> steps) {
        for (Map.Entry<String, StepStatus> entry : steps.entrySet()) {
            if (stepMap.containsKey(String.format(StringFormats.TASK_FORMAT, entry.getKey()))) {
                stepMap.get(String.format(StringFormats.TASK_FORMAT, entry.getKey())).setStatus(entry.getValue());
            }
            switch (entry.getValue()) {
                case ACCEPTED -> {
                    connectedPanel.appendChat(
                            new Message(
                                    SenderNames.TASK_STATUS_CHANGES,
                                    LocalDateTime.now(),
                                    String.format(
                                            StringFormats.TASK_ACCEPTED,
                                            String.format(
                                                    StringFormats.TASK_FORMAT, entry.getKey()
                                            )
                                    )
                            )
                    );
                    connectedPanel.addCounterNonActive();
                    connectedPanel.setVisualToNone(entry.getKey());
                }
                case NONE -> {
                    connectedPanel.setVisualToNone(entry.getKey());
                }

            }
        }
        ResourceManager.getInstance().getConnectedPanel().redrawSquares();
    }

    public void addMessageToChatAndToList(Message message) {
        if (connectedPanel.getChatArea().getText().equals(FieldTexts.NO_MESSAGES)) {
            connectedPanel.getChatArea().setText("");
        }
        connectedPanel.appendChat(message);
        allMessageList.add(message);
    }
}
