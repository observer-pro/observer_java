package pro.sky.observer_java.mapper;

import org.apache.commons.lang3.StringUtils;
import pro.sky.observer_java.constants.FieldTexts;
import pro.sky.observer_java.constants.ProjectFileStatus;
import pro.sky.observer_java.model.ProjectFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProjectFileMapper {
    private final long MAX_FILE_SIZE_TO_TRANSFER = 20000;
    private final Logger logger = Logger.getLogger(ProjectFileMapper.class.getName());

    public ProjectFileMapper() {
    }

    public ProjectFile filetoProjectFile(String filePath, String relative, ProjectFileStatus status) throws IOException {

        ProjectFile projectFile = new ProjectFile();
        Path path = Paths.get(filePath);
        Path result;
        try {
            result = Paths.get(relative).relativize(path);
        } catch (RuntimeException e) {
            result = Paths.get(useOldRelativize(filePath, relative));
        }

        if (result.startsWith("..")) {
            logger.warning("File relativize ERROR -  result");
            result = Paths.get(useOldRelativize(filePath, relative));
        }

        projectFile.setFilename(
                StreamSupport.stream(result.spliterator(), false)
                        .map(Path::toString)
                        .collect(Collectors.joining("/"))
        );

        projectFile.setStatus(status);

        if (status.equals(ProjectFileStatus.REMOVED)) {
            return projectFile;
        }

        if (Files.size(path) > MAX_FILE_SIZE_TO_TRANSFER) {
            projectFile.setContent(FieldTexts.TOO_LARGE);
        } else {
            projectFile.setContent(EditorToString.contentsAsString(Files.readAllLines(path)));
        }
        logger.info("fileToProjectFile Created");
        return projectFile;
    }

    private String useOldRelativize(String path, String relative) {
        return StringUtils.removeStart(path, relative);
    }
}
