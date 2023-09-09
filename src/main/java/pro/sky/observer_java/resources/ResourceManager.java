package pro.sky.observer_java.resources;


import com.intellij.openapi.wm.ToolWindow;
import io.socket.client.Socket;
import pro.sky.observer_java.ConnectedPanel;
import pro.sky.observer_java.InactivePanel;
import pro.sky.observer_java.SkyPanelToolWindowFactory;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.ProjectFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ResourceManager {
    private volatile ConnectedPanel connectedPanel;

    private volatile InactivePanel inactivePanel;

    private volatile Integer roomId;

    private volatile String userName;

    private volatile ToolWindow toolWindow;

    private volatile SkyPanelToolWindowFactory skyPanelToolWindowFactory;

    private volatile SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent;

    private volatile JPanel contentPanel;

    private Socket mSocket;

    private volatile Integer userId;

    private List<ProjectFile> editorUpdateEvents;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();


    public ScheduledExecutorService getSes() {
        return ses;
    }

    public List<ProjectFile> getEditorUpdateEvents() {
        return editorUpdateEvents;
    }

    public void setEditorUpdateEvents(List<ProjectFile> editorUpdateEvents) {
        this.editorUpdateEvents = editorUpdateEvents;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private List<Message> messageList = new ArrayList<>();

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

    public SkyPanelToolWindowFactory getSkyPanelToolWindowFactory() {
        return skyPanelToolWindowFactory;
    }

    public void setSkyPanelToolWindowFactory(SkyPanelToolWindowFactory skyPanelToolWindowFactory) {
        this.skyPanelToolWindowFactory = skyPanelToolWindowFactory;
    }

    public SkyPanelToolWindowFactory.SkyPanelToolWindowContent getSkyPanelToolWindowContent() {
        return skyPanelToolWindowContent;
    }

    public void setSkyPanelToolWindowContent(SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent) {
        this.skyPanelToolWindowContent = skyPanelToolWindowContent;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    public Socket getmSocket() {
        return mSocket;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
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
}
