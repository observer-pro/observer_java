package pro.sky.observer_java.scheduler;

import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateProjectScheduledSending implements Runnable {
    @Override
    public void run() {
        List<ProjectFile> updatedFiles = ResourceManager.getEditorUpdateEvents();
        if (updatedFiles.isEmpty()) {
            return;
        }
        updatedFiles.removeAll(Collections.singleton(null));

        FileStructureStringer stringer = new FileStructureStringer();
        String json = stringer.getJsonStringFromProjectFileList(updatedFiles);

        ResourceManager.getmSocket().emit("sharing/code_update", stringer.getJsonObjectFromString(json));
        ResourceManager.setEditorUpdateEvents(new ArrayList<>());
    }
}
