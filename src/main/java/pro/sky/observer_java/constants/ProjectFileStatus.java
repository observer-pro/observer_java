package pro.sky.observer_java.constants;

public enum ProjectFileStatus {
    CHANGED("changed"),
    CREATED("created"),
    REMOVED("removed");

    private final String status;

    ProjectFileStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }
}
