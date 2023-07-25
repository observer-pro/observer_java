package pro.sky.test_task_plugin;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SkyPanelToolWindowFactory implements ToolWindowFactory, DumbAware {
    SkyPanelToolWindowContent toolWindowContent;
    Content content;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        Resources.connectedPanel = new ConnectedPanel();
        Resources.inactivePanel = new InactivePanel();
        Resources.toolWindow = toolWindow;
        Resources.skyPanelToolWindowFactory = this;

        toolWindowContent = new SkyPanelToolWindowContent();
        Resources.skyPanelToolWindowContent = toolWindowContent;

        content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }



    public static class SkyPanelToolWindowContent {
        private final JPanel contentPanel = new JPanel();

        public SkyPanelToolWindowContent() {
            contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));

            Resources.contentPanel = contentPanel;

            Resources.inactivePanel.getInactivePanel().setVisible(true);

            Resources.connectedPanel.setVisible(false);

            contentPanel.add(Resources.connectedPanel.getConnectedPanel());
            contentPanel.add(Resources.inactivePanel.getInactivePanel());

        }


        public JPanel getContentPanel() {
            return contentPanel;
        }

//        private JScrollPane createResultField() {
//            resultTextField.setRows(contentPanel.getHeight()/20);
//            resultTextField.setText("Result Data will be displayed here");
//            resultTextField.setEditable(false);
//            JScrollPane scroll = new JBScrollPane(resultTextField);
//            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//            scroll.setSize( 100, 250);

//            return scroll;
//        }

//        @NotNull
//        private JPanel createTopPanel(int n) {
//            JPanel skyPanel = new JPanel();
//            skyPanel.setLayout(new FlowLayout());
//            skyPanel.setSize(300,200);


//            JTextPane valueText = new JTextPane();
//            valueText.setText("Value " + n);
//            skyPanel.add(valueText);

//            JTextField valueTextField = switch (n) {
//                case 1 -> value1TextField;
//                case 2 -> value2TextField;
//                default -> new JTextField();
//            };

//            valueTextField.setSize(400,50);
//            valueTextField.setBounds(50,150,200,30);
//            skyPanel.add(valueTextField);
//            return skyPanel;
//        }

//        @NotNull
//        private JPanel createControlsPanel() {
//            JPanel controlsPanel = new JPanel();
//            JButton getValue = new JButton("get value");
//            getValue.addActionListener(e -> calculateValue());
//            controlsPanel.add(getValue);

//            JButton getTree = new JButton("get tree");
//            getTree.addActionListener(e -> getProjectTreeText());
//            controlsPanel.add(getTree);


//            JButton getSocket = new JButton("get socket");
//            controlsPanel.add(getSocket);

//            getSocket.addActionListener(e ->
//            {
//                if (mSocket == null || !mSocket.connected()) {
//                    try {
//                        connectToSocket();
//                    } catch (InterruptedException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                } else {
//                    resultTextField.setText("Socket Already connected");
//                }
//            });
//            return controlsPanel;
//        }

//        public void connectToSocket(String url){
//            mSocket = IO.socket(URI.create(url), options);
//            String connectedString = "Connected!";
//

//            mSocket.on(Socket.EVENT_CONNECT, args -> resultTextField.setText("Connected"));
//            mSocket.on(Socket.EVENT_DISCONNECT, args -> resultTextField.setText("DISCONNECTED"));
//            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> resultTextField.setText("ERROR: " + Arrays.asList(args)));
//            mSocket.connect();
//        }

//        private void connectToSocket() throws InterruptedException {
//
//            mSocket = IO.socket(SOCKET_URL, options);
//
//            resultTextField.setRows(contentPanel.getHeight()/25);
//            resultTextField.setText("Socket Waiting ");
//
//            mSocket.on(Socket.EVENT_CONNECT, args -> resultTextField.setText("CONNECTED"));
//            mSocket.on(Socket.EVENT_DISCONNECT, args -> resultTextField.setText("DISCONNECTED"));
//            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> resultTextField.setText("ERROR: " + Arrays.asList(args)));
//
//            mSocket.connect();
//        }
//
//        private void getProjectTreeText() {
//            Project project = ProjectManager.getInstance().getOpenProjects()[0];

//            if (project.getBasePath() != null) {
//                resultTextField.setText(
//                        FileStructureStringer
//                                .printDirectoryTree(
//                                        project.getName(),new File(project.getBasePath()+"/src")
//                                )
//                );
//                resultTextField.setRows(
//                        Math.max(contentPanel.getHeight() / 25,
//                                StringUtils.countMatches(resultTextField.getText(), "\n"))
//                );
//            } else {
//                resultTextField.setText("No Content");
//            }
//        }

//        private void calculateValue() {
//            String value1 = value1TextField.getText();
//            String value2 = value2TextField.getText();
//            resultTextField.setRows(contentPanel.getHeight()/25);
//            if (value1.isEmpty() || value2.isEmpty()) {
//                resultTextField.setText("No Content");
//            } else if (!StringUtils.isNumeric(value1) || !StringUtils.isNumeric(value2) ) {
//                resultTextField.setText("Wrong input");
//            } else {
//                resultTextField.setText(
//                        String.format("%d + %d = %d",
//                                Integer.parseInt(value1),
//                                Integer.parseInt(value2) ,
//                                Integer.parseInt(value1) + Integer.parseInt(value2)));
//            }
//        }
//


    }
}