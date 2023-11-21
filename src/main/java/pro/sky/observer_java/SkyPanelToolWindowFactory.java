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
    private ResourceManager resourceManager;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        this.resourceManager = new ResourceManager();
        resourceManager.setConnectedPanel(new ConnectedPanel(resourceManager));
        resourceManager.setInactivePanel(new InactivePanel(resourceManager));
        resourceManager.setToolWindow(toolWindow);

        toolWindowContent = new SkyPanelToolWindowContent();

        content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public class SkyPanelToolWindowContent {
        private final JPanel contentPanel = new JPanel();

        public SkyPanelToolWindowContent() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            resourceManager.getInactivePanel().setVisible(true);
            resourceManager.getConnectedPanel().setVisible(false);

            contentPanel.add(resourceManager.getConnectedPanel().getConnectedJPanel());
            contentPanel.add(resourceManager.getInactivePanel().getInactiveJPanel());
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}