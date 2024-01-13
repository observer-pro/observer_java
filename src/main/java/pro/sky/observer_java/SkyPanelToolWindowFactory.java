package pro.sky.observer_java;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import pro.sky.observer_java.resources.ResourceManager;

import javax.swing.*;

public class SkyPanelToolWindowFactory implements ToolWindowFactory, DumbAware {
    private SkyPanelToolWindowContent toolWindowContent;
    private Content content;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ResourceManager.getInstance().setConnectedPanel(new ConnectedPanel());
        ResourceManager.getInstance().setInactivePanel(new InactivePanel());
        ResourceManager.getInstance().setToolWindow(toolWindow);

        toolWindowContent = new SkyPanelToolWindowContent();

        content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public class SkyPanelToolWindowContent {
        private final JPanel contentPanel = new JPanel();

        public SkyPanelToolWindowContent() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            ResourceManager.getInstance().getInactivePanel().setVisible(true);
            ResourceManager.getInstance().getConnectedPanel().setVisible(false);

            contentPanel.add(ResourceManager.getInstance().getConnectedPanel().getConnectedJPanel());
            contentPanel.add(ResourceManager.getInstance().getInactivePanel().getInactiveJPanel());
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}