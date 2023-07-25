package pro.sky.test_task_plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import io.socket.client.IO;
import io.socket.client.Socket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JPanel connectedPanel;

    private JPanel contentPanel;
    SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent;
    SkyPanelToolWindowFactory skyPanelToolWindowFactory;

    ToolWindow toolWindow;

    Project openProject;

    private Socket mSocket;
    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                openProject =  ProjectManager.getInstance().getOpenProjects()[0];// toolWindow.getProject();
                mSocket = IO.socket(SOCKET_URL, options);
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


                // baloonNotification.notify(p);
//                    (connectedString,
//                    AllIcons.Debugger.ThreadStates.Idle,
//                    NotificationType.INFORMATION);

                mSocket.on(Socket.EVENT_CONNECT, args -> {
                    balloonNotificationConnected.notify(openProject);
                   // skyPanelToolWindowContent.switchToConnected();
                    inactivePanel.setVisible(false);
                    connectedPanel.setVisible(true);
                    // skyPanelToolWindowFactory.recreateToolWindowContent(toolWindow);
                    // ConnectedPanel connectedPanel = ;
                    //inactivePanel = new ConnectedPanel().getConnectedPanel();
                });
                mSocket.on(Socket.EVENT_DISCONNECT, args -> balloonNotificationDisconnected.notify(openProject));
                mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> balloonNotificationDisconnected.notify(openProject));
                mSocket.connect();
            }
        });
    }

    public void setSkyPanelToolWindowContent(SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent) {
        this.skyPanelToolWindowContent = skyPanelToolWindowContent;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public void setSkyPanelToolWindowFactory(SkyPanelToolWindowFactory skyPanelToolWindowFactory) {
        this.skyPanelToolWindowFactory = skyPanelToolWindowFactory;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }


    public JPanel getInactivePanel() {
        return inactivePanel;
    }

    public void setConnectedPanel(JPanel panel){
        this.connectedPanel = panel;
    }

    public void setConnectedPanelVisible(boolean b) {
        this.connectedPanel.setVisible(b);
    }
}
