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
    }
}