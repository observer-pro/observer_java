package pro.sky.observer_java.mapper;

import org.apache.commons.lang.StringUtils;
import pro.sky.observer_java.constants.MappingConstants;
import pro.sky.observer_java.constants.ProjectFileStatus;
import pro.sky.observer_java.model.ProjectFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class ProjectFileMapper {
    private static final long MAX_FILE_SIZE_TO_TRANSFER = 20000;

    private final Logger logger = Logger.getLogger(ProjectFileMapper.class.getName());

    public ProjectFile filetoProjectFile(String filePath, String relative, ProjectFileStatus status) throws IOException {
        ProjectFile projectFile = new ProjectFile();

        projectFile.setFilename(
                StringUtils.removeStart(StringUtils.replaceChars(filePath, '\\', '/'), relative)
        );
        projectFile.setStatus(status);

        if (status.equals(ProjectFileStatus.REMOVED)) {
            return projectFile;
        }

        Path path = Path.of(filePath);

        if (Files.size(path) > MAX_FILE_SIZE_TO_TRANSFER) {
            projectFile.setContent(MappingConstants.TOO_LARGE);
        } else {
            projectFile.setContent(contentsAsString(Files.readAllLines(path)));
        }
        logger.info("fileToProjectFile Created");
        return projectFile;
    }

    private String contentsAsString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append("\n");
        }
        return sb.toString();
    }
}
