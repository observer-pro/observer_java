package pro.sky.observer_java.fileProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.mapper.ProjectFileMapper;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStructureStringer {

    private final List<File> files = new ArrayList<>();
    private final List<ProjectFile> projectFiles = new ArrayList<>();

    ResourceManager resourceManager;

    public FileStructureStringer(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

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

        String basePath = project.getBasePath();
        listFiles(project.getBasePath(), files);


        ProjectFileMapper projectFileMapper = new ProjectFileMapper();
        for (File file : files) {
            ProjectFile projectFile;
            if(pathContainsIgnored(file.getPath())){
                continue;
            }
            try {
                projectFile = projectFileMapper.filetoProjectFile(file.getPath(), basePath, "created");
                if (projectFile != null) {
                    projectFiles.add(projectFile);
                }
            } catch (IOException e) {
                System.out.println("To project File Exception" + e.getMessage());
            }
        }
        return getJsonStringFromProjectFileList(projectFiles);
    }

    private static boolean pathContainsIgnored(String path) {
        if(path.contains(".idea")||path.contains("venv")){
            return true;
        }
        return false;
    }

    public String getJsonStringFromProjectFileList(List<ProjectFile> projectFiles) {
        String json;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(projectFiles);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public JSONObject getJsonObjectFromString(String json) {
        JSONObject sendMessage = new JSONObject();
        JSONArray data;
        try {
            data = new JSONArray(json);
            sendMessage.put("room_id", resourceManager.getRoomId());
            sendMessage.put("files", data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return sendMessage;
    }

}