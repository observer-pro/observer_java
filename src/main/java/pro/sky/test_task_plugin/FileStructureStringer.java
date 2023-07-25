package pro.sky.test_task_plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileStructureStringer {

    List<File> files = new ArrayList<>();
    List<ProjectFile> projectFiles = new ArrayList<>();

    public void listFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                if (file.getPath().contains(".idea")) {
                    continue;
                }
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), files);
                }
            }
    }

    public String getProjectFilesList(Project project) {

        File dir = new File(Objects.requireNonNull(project.getBasePath()));
        listFiles(project.getBasePath(), files);


        ProjectFileMapper projectFileMapper = new ProjectFileMapper();
        for (File file : files) {
            try {
                projectFiles.add(projectFileMapper.filetoProjectFile(file, dir.getName()));
            }catch (IOException e){
                System.out.println("oops");
            }
        }
        String json;
                ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(projectFiles);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }



}