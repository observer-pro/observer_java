package pro.sky.observer_java;

import com.intellij.ProjectTopics;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import pro.sky.observer_java.resources.ResourceManager;

import javax.swing.*;
import java.util.List;

public class SkyPanelToolWindowFactory implements ToolWindowFactory, DumbAware {
    SkyPanelToolWindowContent toolWindowContent;
    Content content;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ResourceManager.setConnectedPanel(new ConnectedPanel());
        ResourceManager.setInactivePanel(new InactivePanel());
        ResourceManager.setToolWindow(toolWindow);
        ResourceManager.setSkyPanelToolWindowFactory(this);

        toolWindowContent = new SkyPanelToolWindowContent();
        ResourceManager.setSkyPanelToolWindowContent(toolWindowContent);

        content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }



    public static class SkyPanelToolWindowContent {
        private final JPanel contentPanel = new JPanel();

        public SkyPanelToolWindowContent() {
            contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));

            ResourceManager.setContentPanel(contentPanel);


            ResourceManager.getInactivePanel().setVisible(true);
            ResourceManager.getConnectedPanel().setVisible(false);

            contentPanel.add(ResourceManager.getConnectedPanel().getConnectedJPanel());
            contentPanel.add(ResourceManager.getInactivePanel().getInactiveJPanel());
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}