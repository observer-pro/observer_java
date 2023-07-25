package pro.sky.test_task_plugin;


import com.intellij.openapi.wm.ToolWindow;
import io.socket.client.Socket;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Resources {
    static volatile ConnectedPanel connectedPanel;

    static volatile InactivePanel inactivePanel;

    static volatile String roomId;

    static volatile String userName;

    static volatile ToolWindow toolWindow;

    static volatile SkyPanelToolWindowFactory skyPanelToolWindowFactory;

    static volatile SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent;

    static volatile JPanel contentPanel;

    static volatile Socket mSocket;

    static List<Message> messageList = new ArrayList<>();

    static volatile boolean watchingStatus = false;
}
