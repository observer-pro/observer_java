package pro.sky.observer_java.events;

import com.intellij.openapi.vfs.newvfs.events.*;
import pro.sky.observer_java.mapper.ProjectFileMapper;
import pro.sky.observer_java.resources.ResourceManager;

import java.io.IOException;
import java.util.logging.Logger;

public class EventManager {
    private static final Logger logger = Logger.getLogger(EventManager.class.getName());

    ProjectFileMapper mapper = new ProjectFileMapper();
    public void addContentChangeEventToEditorEventList(VFileContentChangeEvent event) {
        mapAndAddEvents(event.getPath(), "changed");
//        try {
//            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(event.getFile().getPath(),
//                    ResourceManager.getToolWindow().getProject().getBasePath(),
//                    "changed"));
//        }catch (IOException e){
//            logger.warning("addContentChangeEventToEditorEventList " + e.getMessage());
//        }
//
//        logger.info("addContentChangeEventToEditorEventList processed event - " + event);
    }
    public void addCreateEventToEditorEventList(VFileCreateEvent event){
        mapAndAddEvents(event.getPath(), "created");
//        try {
//            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(event.getFile().getPath(),
//                    ResourceManager.getToolWindow().getProject().getBasePath(),
//                    "created"));
//        }catch (IOException e){
//            logger.warning("addCreateEventToEditorEventList " + e.getMessage());
//        }
//        logger.info("addContentChangeEventToEditorEventList processed event - " + event);
    }

    public void addDeleteEventToEditorEventList(VFileDeleteEvent event){
        mapAndAddEvents(event.getPath(), "removed");
//        try {
//            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(event.getPath(),
//                    ResourceManager.getToolWindow().getProject().getBasePath(),
//                    "removed"));
//        }catch (IOException e){
//            logger.warning("addDeleteEventToEditorEventList " + e.getMessage());
//        }
//        logger.info("addContentChangeEventToEditorEventList processed event - " + event);
    }

    public void addPropertyChangeEventToEditorEventList(VFilePropertyChangeEvent event){
        mapAndAddEvents(event.getPath(), "created");
        mapAndAddEvents(event.getOldPath(), "removed");

//        try{
//            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(event.getPath(),
//                    ResourceManager.getToolWindow().getProject().getBasePath(),
//                    "created"));
//
//            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(event.getOldPath(),
//                    ResourceManager.getToolWindow().getProject().getBasePath(),  "removed"));
//        }catch (IOException e){
//            logger.warning("addPropertyChangeEventToEditorEventList " + e.getMessage());
//        }
//        logger.info("created event - " + event);
    }

    private void mapAndAddEvents(String path, String status){
        try {
            ResourceManager.getEditorUpdateEvents().add(mapper.filetoProjectFile(path,
                    ResourceManager.getToolWindow().getProject().getBasePath(),
                    status));
        }catch (IOException e){
            logger.warning("event method exception" + e.getMessage());
        }

        logger.info(status + " event - " + path);
    }
}
