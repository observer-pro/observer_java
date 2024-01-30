package pro.sky.observer_java.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.sky.observer_java.constants.StringFormats;
import pro.sky.observer_java.constants.StepStatus;

public class Step {
    private String name;
    private String content;
    private String language;
    private String type;

    @JsonIgnore
    private StepStatus status;

    public Step() {
        this.status = StepStatus.NONE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String toFormattedString() {
        return String.format(StringFormats.TASK_FORMAT, this.name);
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }
}
