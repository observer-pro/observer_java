package pro.sky.test_task_plugin;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProjectFileMapper {
    public ProjectFile filetoProjectFile(File file, String relative) throws IOException {
        ProjectFile projectFile = new ProjectFile();

        projectFile.setFileName(
                StringUtils.replaceChars(StringUtils.removeStart(file.getPath(),relative),'\\','/')
        );
        projectFile.setStatus("created");

        projectFile.setContent(contentsAsString(Files.readAllLines(Path.of(file.getPath()))));

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
