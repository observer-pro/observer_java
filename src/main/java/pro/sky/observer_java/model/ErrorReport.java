package pro.sky.observer_java.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorReport {
    @JsonProperty("room_id")
    int roomId;

    @JsonProperty("user_id")
    int userId;
    String content;
    String path;
    String function;

    public ErrorReport() {
    }

    public ErrorReport(int roomId, int userId, String content, String path, String function) {
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.path = path;
        this.function = function;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
