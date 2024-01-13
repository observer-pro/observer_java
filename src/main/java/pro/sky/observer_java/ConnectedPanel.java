package pro.sky.observer_java;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import pro.sky.observer_java.constants.*;
import pro.sky.observer_java.mapper.JsonMapper;
import pro.sky.observer_java.mapper.MarkdownAndHtml;
import pro.sky.observer_java.model.Message;
import pro.sky.observer_java.model.Step;
import pro.sky.observer_java.resources.EditorEvents;
import pro.sky.observer_java.resources.ResourceManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConnectedPanel {

    private JTextField messageField;
    private JButton sendButton;
    private JButton disconnectButton;
    private JLabel mentorStatusLabel;
    private JTextArea chatArea;
    private JPanel connectedPanel;
    private JSeparator separator;
    private JButton doneButton;
    private JButton helpButton;
    private JTabbedPane tabPanel;
    private JTextPane taskCodeField;
    private JPanel chatTab;
    private JPanel taskTab;
    private JComboBox comboBoxTasks;
    private JScrollPane chatScroll;
    private JButton AIHELPButton;
    private JTextPane aiHelpField;
    private JPanel aiHelpTab;
    private JTextPane SqaresTextPlane;
    private JComboBox chatTypeComboBox;

    private final Logger logger = Logger.getLogger(ConnectedPanel.class.getName());

    public ConnectedPanel() {


        sendButton.addActionListener(e -> sendMessage());

        chatTypeComboBox.addItem(ChatTypes.SHOW_ALL);
        chatTypeComboBox.addItem(ChatTypes.MESSAGES);
        chatTypeComboBox.addItem(ChatTypes.STATUSES);
        chatTypeComboBox.addItem(ChatTypes.MENTOR_STATUS);

        disconnectButton.addActionListener(e -> {
            ResourceManager.getInstance().getmSocket().disconnect();

            JSONObject sendMessage = new JSONObject();
            try {
                sendMessage.put(JsonFields.ROOM_ID, ResourceManager.getInstance().getRoomId());
            } catch (JSONException exception) {
                logger.warning("Connected panel JSON - " + exception.getMessage());
            }
            ResourceManager.getInstance().getSes().shutdown();
            ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.ROOM_LEAVE, sendMessage);
        });


        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Step currentSelectedStep = ResourceManager.getInstance().getStepsMap()
                        .get(Objects.requireNonNull(comboBoxTasks.getSelectedItem()).toString());

                if (currentSelectedStep.getStatus() == StepStatus.HELP) {
                    setAllButtonVisualsToNone();
                    setStepStatusAndSend(currentSelectedStep, StepStatus.NONE);
                    return;
                }
                buttonPressChatMessage(StringFormats.HELP_MESSAGE_FORMAT);

                setAllButtonVisualsToHelp();
                setStepStatusAndSend(currentSelectedStep, StepStatus.HELP);
            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBoxTasks.getSelectedItem() == null){
                    return;
                }
                Step currentSelectedStep = ResourceManager.getInstance().getStepsMap()
                        .get(Objects.requireNonNull(comboBoxTasks.getSelectedItem()).toString());
                if (currentSelectedStep.getStatus() == StepStatus.DONE) {
                    setAllButtonVisualsToNone();
                    setStepStatusAndSend(currentSelectedStep, StepStatus.NONE);
                    return;
                }

                buttonPressChatMessage(StringFormats.DONE_MESSAGE_FORMAT);

                setAllButtonVisualsToDone();

                setStepStatusAndSend(currentSelectedStep, StepStatus.DONE);
            }
        });
        comboBoxTasks.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Map<String, Step> steps = ResourceManager.getInstance().getStepsMap();
                    String selectedStep = e.getItem().toString();
                    String taskText = "No task";
                    if (steps.containsKey(selectedStep)) {
                        Step currentSelectedStep = steps.get(selectedStep);
                        taskText = currentSelectedStep.getContent();
                        switch (currentSelectedStep.getStatus()) {
                            case DONE -> setAllButtonVisualsToDone();
                            case HELP -> setAllButtonVisualsToHelp();
                            default -> setAllButtonVisualsToNone();
                        }
                    }
                    taskCodeField.setText(taskText);
                }
            }
        });
        AIHELPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditorEvents editorEvents = new EditorEvents(ResourceManager.getInstance().getToolWindow().getProject());
                String aiRequestCode;
                String aiRequestContent;
                JSONObject sendJson = new JSONObject();
                try {
                    aiRequestContent = Jsoup.parse(taskCodeField.getText()).text();
                    aiRequestCode = editorEvents.getOpenEditorText();

                    sendJson.put(JsonFields.CONTENT, aiRequestContent);
                    sendJson.put(JsonFields.CODE, aiRequestCode);

                } catch (IOException | JSONException ex) {
                    logger.warning("AI HELP PROJECT WARNING");
                    throw new RuntimeException(ex);
                }
                setAiHelpFieldText(MessageTemplates.AI_WAITING_FOR_SERVER);
                ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.SOLUTION_AI, sendJson);
            }
        });
        tabPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (tabPanel.getSelectedIndex()) {
                    case 1: {
                        tabPanel.setTitleAt(1, StringFormats.CHAT_TAB_READ);
                        ResourceManager.getInstance().setChatCounter(0);
                        break;
                    }
                    case 2: {
                        tabPanel.setTitleAt(2, StringFormats.AI_HELP_READ);
                        break;
                    }
                }
            }
        });
        chatTypeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateChatField(e.getItem().toString());
                }
            }
        });
    }


    public void setVisualToNone(String key) {
        if(comboBoxTasks.getSelectedItem() == null){
            return;
        }
        String selectedStep = Objects.requireNonNull(comboBoxTasks.getSelectedItem()).toString();
        if(selectedStep.equals(String.format(StringFormats.TASK_FORMAT, key))){
            setAllButtonVisualsToNone();
        }
    }
    private void buttonPressChatMessage(String format) {
        ResourceManager.getInstance().addMessageToChatAndToList(
                new Message(
                        SenderNames.TASK_STATUS_CHANGES,
                        LocalDateTime.now(),
                        String.format(format, comboBoxTasks.getSelectedItem().toString())
                )
        );
    }

    private void updateChatField(String chatFilter) {
        switch (chatFilter) {
            case ChatTypes.SHOW_ALL -> chatArea.setText(createChatUpdateString(ResourceManager.getInstance().getAllMessageList()));
            case ChatTypes.MENTOR_STATUS ->
                chatArea.setText(
                        createChatUpdateString(
                                ResourceManager.getInstance()
                                        .getAllMessageList()
                                        .stream()
                                        .filter(m -> m.getSender().equals(SenderNames.WATCH_STATUS))
                                        .toList()
                        )
                );
            case ChatTypes.STATUSES -> chatArea.setText(
                    createChatUpdateString(
                            ResourceManager.getInstance()
                                    .getAllMessageList()
                                    .stream()
                                    .filter(m -> m.getSender().equals(SenderNames.TASK_STATUS_CHANGES))
                                    .toList()
                    )
            );
            default -> chatArea.setText(
                    createChatUpdateString(
                            ResourceManager.getInstance()
                                    .getAllMessageList()
                                    .stream()
                                    .filter(m -> !(m.getSender().equals(SenderNames.TASK_STATUS_CHANGES)||
                                            m.getSender().equals(SenderNames.WATCH_STATUS)))
                                    .toList()
                    )
            );
        }
    }

    private String createChatUpdateString(List<Message> messageList) {
        StringBuilder sb = new StringBuilder();
        for (Message message : messageList) {
            sb.append(String.format(StringFormats.CHAT_FORMAT, message.getSender(), message.getMessageText()));
        }
        return sb.toString();
    }

    private void setStepStatusAndSend(Step step, StepStatus status) {
        step.setStatus(status);
        sendStatuses();
    }

    public void setAllButtonVisualsToDone() {
        helpButton.setForeground(Gray._60);
        doneButton.setForeground(JBColor.GREEN);
    }

    public void setAllButtonVisualsToHelp() {
        doneButton.setForeground(Gray._60);
        helpButton.setForeground(JBColor.ORANGE);
    }

    public void setAllButtonVisualsToNone() {
        helpButton.setForeground(Gray._187);
        doneButton.setForeground(Gray._187);
    }

    private void sendStatuses() {

        ResourceManager.getInstance().getmSocket()
                .emit(CustomSocketEvents.STEPS_STATUS_TO_MENTOR, JsonMapper.stepStatusToJson(ResourceManager.getInstance().getStepsMap()));
    }

    private void sendMessage() {
        if (messageField.getText().isEmpty()) {
            return;
        }
        if (chatArea.getText().equals(FieldTexts.NO_MESSAGES)) {
            chatArea.setText("");
        }
        String senderName = ResourceManager.getInstance().getUserName();
        String messageText = messageField.getText();

        chatArea.append(String.format(MessageTemplates.MESSAGE_STRING_FORMAT, senderName, messageText));

        JSONObject sendMessage = new JSONObject();
        try {
            sendMessage.put(JsonFields.ROOM_ID, ResourceManager.getInstance().getRoomId());
            sendMessage.put(JsonFields.CONTENT, messageText);
        } catch (JSONException exception) {
            logger.warning("Connected panel JSON - " + exception.getMessage());
        }

        ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.MESSAGE_TO_MENTOR, sendMessage);

        Message message = new Message(senderName, LocalDateTime.now(), messageText);
        ResourceManager.getInstance().getAllMessageList().add(message);
        messageField.setText("");
        scrollChatToBottom();
    }

    public JPanel getConnectedJPanel() {
        return connectedPanel;
    }

    public void setVisible(boolean toggle) {
        connectedPanel.setVisible(toggle);
    }

    public void toggleMentorStatusLabelText() {
        if (ResourceManager.getInstance().isWatching()) {
            mentorStatusLabel.setText(FieldTexts.MENTOR_IS_WATCHING);
            mentorStatusLabel.setForeground(JBColor.GREEN);
            return;
        }

        mentorStatusLabel.setText(FieldTexts.MENTOR_IS_NOT_WATCHING);
        mentorStatusLabel.setForeground(Gray._187);

    }

    public void scrollChatToBottom() {
        JScrollBar vertical = this.chatScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void appendChat(Message message) {
        String currentChatType = Objects.requireNonNull(chatTypeComboBox.getSelectedItem()).toString();
        if (currentChatType.equals(ChatTypes.SHOW_ALL)) {
            appendChatField(message);
            return;
        }

        String messageType = getTypeOfMessage(message);

        if (messageType.equals(currentChatType)) {
            appendChatField(message);
        }

    }

    private String getTypeOfMessage(Message message) {
        switch (message.getSender()) {
            case SenderNames.TASK_STATUS_CHANGES -> {
                return ChatTypes.STATUSES;
            }
            case SenderNames.WATCH_STATUS -> {
                return ChatTypes.MENTOR_STATUS;
            }
            default -> {
                return ChatTypes.MESSAGES;
            }
        }
    }

    private void appendChatField(Message message) {
        chatArea.append(
                String.format(
                        MessageTemplates.MESSAGE_STRING_FORMAT,
                        message.getSender(),
                        message.getMessageText()
                )
        );
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    public void setAllSteps(List<Step> steps) {
        Map<String, Step> stepMap = steps.stream()
                .collect(Collectors.toMap(Step::toFormattedString, Function.identity()));
        ResourceManager.getInstance().setSteps(stepMap);

        String title = String.format(StringFormats.TASK_HEADER_FORMAT, steps.size());
        tabPanel.setTitleAt(0, title);
        comboBoxTasks.removeAllItems();
        for (Step step : steps) {
            String stepString = step.toFormattedString();
            if (step.getLanguage().equals(ParseTags.MD)) {
                step.setContent(MarkdownAndHtml.mdToHtml(step.getContent()));
            }
            comboBoxTasks.addItem(stepString);
        }
    }

    public void setAiHelpFieldText(String text) {
        this.aiHelpField.setText(text);
    }

    public void addCounterNonActive() {
        if (tabPanel.getSelectedIndex() == 1) {
            return;
        }
        ResourceManager.getInstance().setChatCounter(ResourceManager.getInstance().getChatCounter() + 1);
        tabPanel.setTitleAt(1, String.format(StringFormats.CHAT_TAB_UNREAD, ResourceManager.getInstance().getChatCounter()));
    }

    public void changeAiHelpTabName() {
        if (tabPanel.getSelectedIndex() == 2) {
            return;
        }
        tabPanel.setTitleAt(2, StringFormats.AI_HELP_UNREAD);
    }


}
