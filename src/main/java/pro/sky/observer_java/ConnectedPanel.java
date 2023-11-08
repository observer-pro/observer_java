package pro.sky.observer_java;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.constants.*;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.resources.ResourceManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import com.github.rjeschke.txtmark.Processor;

public class ConnectedPanel {
    private JTextField messageField;
    private JButton sendButton;
    private JButton disconnectButton;
    private JLabel mentorStatusLabel;
    private JTextArea chatArea;
    private JPanel connectedPanel;
    private JLabel connectionStatusLabel;
    private JSeparator separator;
    private JButton inProgressButton;
    private JButton doneButton;
    private JButton helpButton;
    private JTabbedPane tabPanel;
    private JTextPane taskCodeField;
    private JPanel chatTab;
    private JPanel taskTab;
    private JComboBox comboBox1;
    private final ResourceManager resourceManager;

    private final Logger logger = Logger.getLogger(ConnectedPanel.class.getName());

    public ConnectedPanel(ResourceManager resourceManager) {

        this.resourceManager = resourceManager;
        sendButton.addActionListener(e -> sendMessage());

        disconnectButton.addActionListener(e -> {
            resourceManager.getmSocket().disconnect();

            JSONObject sendMessage = new JSONObject();
            try {
                sendMessage.put(JsonFields.ROOM_ID, resourceManager.getRoomId());
            } catch (JSONException exception) {
                logger.warning("Connected panel JSON - " + exception.getMessage());
            }
            resourceManager.getSes().shutdown();
            resourceManager.getmSocket().emit(CustomSocketEvents.ROOM_LEAVE, sendMessage);
        });

        inProgressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resourceManager.getStudentStatus() == StudentSignal.IN_PROGRESS) {
                    setAllNoneButDoneAndSend();
                    return;
                }

                resourceManager.setStudentStatus(StudentSignal.IN_PROGRESS);
                helpButton.setForeground(Gray._60);
                doneButton.setForeground(Gray._60);
                inProgressButton.setForeground(JBColor.GREEN);

                sendSignal(StudentSignal.IN_PROGRESS);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resourceManager.getStudentStatus() == StudentSignal.HELP) {
                    setAllNoneButDoneAndSend();
                    return;
                }

                resourceManager.setStudentStatus(StudentSignal.HELP);
                doneButton.setForeground(Gray._60);
                inProgressButton.setForeground(Gray._60);
                helpButton.setForeground(JBColor.ORANGE);

                sendSignal(StudentSignal.HELP);
            }
        });
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resourceManager.getStudentStatus() == StudentSignal.DONE) {
                    setAllNoneButDoneAndSend();
                    return;
                }

                resourceManager.setStudentStatus(StudentSignal.DONE);
                helpButton.setForeground(Gray._60);
                inProgressButton.setForeground(Gray._60);
                doneButton.setForeground(JBColor.GREEN);

                sendSignal(StudentSignal.DONE);
            }
        });
    }

    public void setAllNoneButDoneAndSend() {
        if(resourceManager.getStudentStatus() == StudentSignal.DONE) {
            return;
        }
        setAllNoneAndSend();
    }

    public void setAllNoneAndSend(){
        resourceManager.setStudentStatus(StudentSignal.NONE);
        inProgressButton.setForeground(Gray._187);
        helpButton.setForeground(Gray._187);
        doneButton.setForeground(Gray._187);

        sendSignal(StudentSignal.NONE);
    }

    private void sendSignal(StudentSignal signal) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonFields.USER_ID, resourceManager.getUserId());
            jsonObject.put(JsonFields.VALUE, signal);
        } catch (JSONException e) {
            logger.warning(e.getMessage());
        }

        resourceManager.getmSocket().emit(CustomSocketEvents.SIGNAL, jsonObject);
    }

    private void sendMessage() {
        if (messageField.getText().isEmpty()) {
            return;
        }
        if (chatArea.getText().equals(FieldTexts.NO_MESSAGES)) {
            chatArea.setText("");
        }
        String senderName = resourceManager.getUserName();
        String messageText = messageField.getText();

        chatArea.append(String.format(MessageTemplates.MESSAGE_STRING_FORMAT, senderName, messageText));

        JSONObject sendMessage = new JSONObject();
        try {
            sendMessage.put(JsonFields.ROOM_ID, resourceManager.getRoomId());
            sendMessage.put(JsonFields.CONTENT, messageText);
        } catch (JSONException exception) {
            logger.warning("Connected panel JSON - " + exception.getMessage());
        }

        resourceManager.getmSocket().emit(CustomSocketEvents.MESSAGE_TO_MENTOR, sendMessage);

        Message message = new Message(senderName, LocalDateTime.now(), messageText);
        resourceManager.getMessageList().add(message);
        messageField.setText("");
    }

    public JPanel getConnectedJPanel() {
        return connectedPanel;
    }

    public void setVisible(boolean toggle) {
        connectedPanel.setVisible(toggle);
    }

    public void setConnectionStatusLabelText(String text) {
        this.connectionStatusLabel.setText(text);
    }

    public void toggleMentorStatusLabelText() {
        if (resourceManager.isWatching()) {
            mentorStatusLabel.setText(FieldTexts.MENTOR_IS_WATCHING);
            mentorStatusLabel.setForeground(JBColor.GREEN);
            return;
        }

        mentorStatusLabel.setText(FieldTexts.MENTOR_IS_NOT_WATCHING);
        mentorStatusLabel.setForeground(Gray._187);

    }

    public void setExerciseText(String taskCode, String parseLanguage){
        switch(parseLanguage){
            case ParseTags.MD: {
                this.setMdTask(taskCode);
                break;
            }
            case ParseTags.HTML: {
                this.setHtmlTask(taskCode);
            }
        }
    }
    private void setHtmlTask(String html){
        taskCodeField.setText(html);
    }

    private void setMdTask(String md){
        String html = Processor.process(md);
        taskCodeField.setText(html.replace("`",""));
    }

    public void appendChat(String string) {
        chatArea.append(string);
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

}
