package pro.sky.observer_java.scheduler;

import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateProjectScheduledSending implements Runnable {
    private final ResourceManager resourceManager;
    public UpdateProjectScheduledSending(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void run() {
        List<ProjectFile> updatedFiles = resourceManager.getEditorUpdateEvents();

        updatedFiles.removeAll(Collections.singleton(null));

        if (updatedFiles.isEmpty()) {
            return;
        }

        FileStructureStringer stringer = new FileStructureStringer(resourceManager);
        String json = stringer.getJsonStringFromProjectFileList(updatedFiles);

        resourceManager.getmSocket().emit(CustomSocketEvents.CODE_UPDATE, stringer.getJsonObjectFromString(json));
        resourceManager.setEditorUpdateEvents(new ArrayList<>());
    }
}
