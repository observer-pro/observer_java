package pro.sky.observer_java;

import pro.sky.observer_java.constants.MessageTemplates;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.resources.SocketEvents;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
    private final SocketEvents socketEvents;

    public InactivePanel() {

        this.socketEvents = new SocketEvents( this, ResourceManager.getInstance().getConnectedPanel());

        connectButton.addActionListener(e ->
                socketEvents.createSocketWithListenersAndConnect(urlField.getText())
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
    public void setVisible(boolean toggle) {
        inactivePanel.setVisible(toggle);
    }

    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }

    public JTextField getUrlField() {
        return urlField;
    }

    public JTextField getRoomIdField() {
        return roomIdField;
    }

    public JTextField getNameField() {
        return nameField;
    }
}
