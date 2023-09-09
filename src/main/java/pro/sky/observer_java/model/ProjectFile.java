package pro.sky.observer_java.model;

import pro.sky.observer_java.constants.ProjectFileStatus;

public class ProjectFile {
    private String filename;
    private ProjectFileStatus status;
    private String content;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ProjectFileStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectFileStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
