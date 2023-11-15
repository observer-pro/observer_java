package pro.sky.observer_java.events;

import com.intellij.openapi.vfs.newvfs.events.*;
import pro.sky.observer_java.constants.ProjectFileStatus;
import pro.sky.observer_java.mapper.ProjectFileMapper;
import pro.sky.observer_java.resources.ResourceManager;

import java.io.IOException;
import java.util.logging.Logger;

public class EventManager {
    private final Logger logger = Logger.getLogger(EventManager.class.getName());
    private final ResourceManager resourceManager;
    ProjectFileMapper mapper;

    public EventManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.mapper = new ProjectFileMapper(resourceManager);
    }

    public void addContentChangeEventToEditorEventList(VFileContentChangeEvent event) {
        mapAndAddEvents(event.getPath(), ProjectFileStatus.CHANGED);
    }
    public void addCreateEventToEditorEventList(VFileCreateEvent event){
        mapAndAddEvents(event.getPath(), ProjectFileStatus.CREATED);
    }

    public void addDeleteEventToEditorEventList(VFileDeleteEvent event){
        mapAndAddEvents(event.getPath(), ProjectFileStatus.REMOVED);
    }
    public void addPropertyChangeEventToEditorEventList(VFilePropertyChangeEvent event){
        mapAndAddEvents(event.getPath(), ProjectFileStatus.CREATED);
        mapAndAddEvents(event.getOldPath(), ProjectFileStatus.REMOVED);
    }
    public void addMoveEventToEditorEventList(VFileMoveEvent event) {
        mapAndAddEvents(event.getPath(), ProjectFileStatus.CREATED);
        mapAndAddEvents(event.getOldPath(), ProjectFileStatus.REMOVED);
    }
    private void mapAndAddEvents(String path, ProjectFileStatus status){
        if(resourceManager.getObserverIgnore().checkIfIsInIgnored(path)){
            return;
        }
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
