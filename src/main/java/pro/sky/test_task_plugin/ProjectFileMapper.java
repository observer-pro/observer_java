package pro.sky.test_task_plugin;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProjectFileMapper {

    private final long MAX_FILE_SIZE_TO_TRANSFER = 20000;
    public ProjectFile filetoProjectFile(File file, String relative) throws IOException {
        ProjectFile projectFile = new ProjectFile();

        projectFile.setFileName(
                StringUtils.replaceChars(StringUtils.removeStart(file.getPath(),relative),'\\','/')
        );
        projectFile.setStatus("created");

        if(Files.size(file.toPath()) > MAX_FILE_SIZE_TO_TRANSFER){
            projectFile.setContent("File too large to transfer");
        }else {
            projectFile.setContent(contentsAsString(Files.readAllLines(Path.of(file.getPath()))));
        }
        return projectFile;
    }

    private String contentsAsString(List<String> strings){
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append("\n");
        }
        return sb.toString();
    }
}
