package pro.sky.observer_java.scheduler;

import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class UpdateProjectScheduledSending implements Runnable {
    @Override
    public void run() {
        List<ProjectFile> updatedFiles = ResourceManager.getEditorUpdateEvents();
        if (updatedFiles.isEmpty()) {
            return;
        }
//        Project project = ResourceManager.getToolWindow().getProject();
//        File dir = new File(Objects.requireNonNull(project.getBasePath()));
//        List<ProjectFile> updatedProjectFiles = new ArrayList<>();
//        ProjectFileMapper mapper = new ProjectFileMapper();

        FileStructureStringer stringer = new FileStructureStringer();
        String json = stringer.getJsonString(updatedFiles);

        ResourceManager.getmSocket().emit("sharing/code_update", stringer.getJsonObjectFromString(json));
        ResourceManager.setEditorUpdateEvents(new ArrayList<>());
    }
}
