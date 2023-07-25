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
    private JLabel connectionStatusLabel;
    private JScrollPane scroll;
    private JSeparator separator;

    public JPanel getConnectedPanel() {
       return connectedPanel;
    }

    public void setVisible(boolean toggle){
        connectedPanel.setVisible(toggle);
    }

    public void setConnectionStatusLabelText(String text) {
        this.connectionStatusLabel.setText(text);
    }
}
