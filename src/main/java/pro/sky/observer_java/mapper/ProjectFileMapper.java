package pro.sky.observer_java.mapper;

import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.apache.commons.lang.StringUtils;
import pro.sky.observer_java.model.ProjectFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class ProjectFileMapper {
    private final long MAX_FILE_SIZE_TO_TRANSFER = 20000;

    private static final Logger logger = Logger.getLogger(ProjectFileMapper.class.getName());

    public ProjectFile filetoProjectFile(String filePath, String relative, String status) throws IOException {
        ProjectFile projectFile = new ProjectFile();
        if(filePath.contains(".idea")){
            return null;
        }

        projectFile.setFilename(
                StringUtils.removeStart(StringUtils.replaceChars(filePath,'\\','/'),relative)
        );
        projectFile.setStatus(status);

        if(status.equals("removed")){
            return projectFile;
        }

        Path path = Path.of(filePath);

        if(Files.size(path) > MAX_FILE_SIZE_TO_TRANSFER){
            projectFile.setContent("File too large to transfer");
        }else{
            projectFile.setContent(contentsAsString(Files.readAllLines(path)));
        }
        logger.info("filetoProjectFile Created");
        return projectFile;
    }

    private String contentsAsString(List<String> strings){
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append("\n");
        }
        return sb.toString();
    }

    public void addRenameEventToProjectFileEventList(VFileEvent event){
//        if (event.getPath().contains(".idea")) {
//            return;
//        }
//        System.out.println("Event = " + event);
//
//        String status;
//        String eventString = event.toString();
//        if (eventString.contains("create")) {
//            status = "created";
//        } else if (eventString.contains("update")) {
//            status = "changed";
//        } else if (eventString.contains("deleted")) {
//            status = "removed";
//        } else if (eventString.contains("property(name) changed")) {
//            nameChangeEvent(event);
//            return;
//        } else {
//            return;
//        }
//        try {
//            ResourceManager.getEditorUpdateEvents().add(
//                    filetoProjectFile(
//                            new File(event.getPath()),
//                            ResourceManager.getToolWindow().getProject().getName(),
//                            status
//                    )
//            );
//        } catch (IOException e) {
//            throw new RuntimeException("Update message Exception " + e.getMessage());
//        }
    }


//    private void nameChangeEvent(VFileEvent event) {
//        try {
//            ResourceManager.getEditorUpdateEvents().add(
//                    filetoProjectFile(
//                            new File(event.getPath()),
//                            ResourceManager.getToolWindow().getProject().getName(),
//                            "created"
//                    )
//            );
//        } catch (IOException e){
//            throw new RuntimeException("Update message Exception " + e.getMessage());
//        }
//    }
}
