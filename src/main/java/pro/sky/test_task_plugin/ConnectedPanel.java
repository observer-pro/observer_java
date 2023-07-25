package pro.sky.test_task_plugin;

import javax.swing.*;

public class ConnectedPanel {
    private JTextField messageField;
    private JButton sendButton;
    private JButton stopSharingButton;
    private JLabel logoLabel;
    private JLabel mentorStatusLabel;
    private JTextArea chatArea;
    private JPanel connectedPanel;

    public JPanel getConnectedPanel() {
       return connectedPanel;
    }

    public void setVisible(boolean toggle){
        connectedPanel.setVisible(toggle);
    }
}
