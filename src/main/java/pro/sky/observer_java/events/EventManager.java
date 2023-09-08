package pro.sky.observer_java.events;

import com.intellij.openapi.vfs.newvfs.events.*;
import pro.sky.observer_java.mapper.ProjectFileMapper;
import pro.sky.observer_java.resources.ResourceManager;

import java.io.IOException;
import java.util.logging.Logger;

public class EventManager {
    private static final Logger logger = Logger.getLogger(EventManager.class.getName());

    private final ResourceManager resourceManager;

    public EventManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    ProjectFileMapper mapper = new ProjectFileMapper();
    public void addContentChangeEventToEditorEventList(VFileContentChangeEvent event) {
        mapAndAddEvents(event.getPath(), "changed");
    }
    public void addCreateEventToEditorEventList(VFileCreateEvent event){
        mapAndAddEvents(event.getPath(), "created");
    }

    public void addDeleteEventToEditorEventList(VFileDeleteEvent event){
        mapAndAddEvents(event.getPath(), "removed");
    }
    public void addPropertyChangeEventToEditorEventList(VFilePropertyChangeEvent event){
        mapAndAddEvents(event.getPath(), "created");
        mapAndAddEvents(event.getOldPath(), "removed");
    }
    public void addMoveEventToEditorEventList(VFileMoveEvent event) {
        mapAndAddEvents(event.getPath(), "created");
        mapAndAddEvents(event.getOldPath(), "removed");
    }
    private void mapAndAddEvents(String path, String status){
        try {
            resourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(path,
                    resourceManager.getToolWindow().getProject().getBasePath(),
                    status));
        }catch (IOException e){
            logger.warning("event method exception" + e.getMessage());
        }

        logger.info(status + " event - " + path);
    }
}
