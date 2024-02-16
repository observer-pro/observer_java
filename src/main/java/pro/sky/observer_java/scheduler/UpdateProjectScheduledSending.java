package pro.sky.observer_java.scheduler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.Collections;
import java.util.List;

public class UpdateProjectScheduledSending implements Runnable {

    private final Project project;
    public UpdateProjectScheduledSending() {

        this.project = ResourceManager.getInstance().getToolWindow().getProject();
    }

    @Override
    public void run() {

        List<ProjectFile> updatedFiles = ResourceManager.getInstance().getEditorUpdateEvents();
        updatedFiles.removeAll(Collections.singleton(null));

        VirtualFile apiDir = project.getBaseDir();
        VfsUtil.markDirtyAndRefresh(true, true, true, apiDir);


        if (updatedFiles.isEmpty()) {

            apiDir.refresh(false,true);
            ApplicationManager.getApplication().invokeAndWait(() -> {
                FileDocumentManager.getInstance().saveAllDocuments();
            });
            return;
        }

        FileStructureStringer stringer = new FileStructureStringer();
        String json = stringer.getJsonStringFromProjectFileList(updatedFiles);

        ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.CODE_UPDATE, stringer.getCodeSendJsonObjectFromString(json));
        ResourceManager.getInstance().clearEditorUpdateEvents();

    }
}
