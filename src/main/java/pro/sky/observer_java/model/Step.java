package pro.sky.observer_java.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.sky.observer_java.constants.StringFormats;
import pro.sky.observer_java.constants.StudentSignal;

public class Step {
    String name;
    String content;
    String language;
    String type;

    @JsonIgnore
    StudentSignal status;

    public Step(){
        this.status = StudentSignal.NONE;
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


    public String toFormattedString(){
        return String.format(StringFormats.TASK_FORMAT, this.name);
    }

    public StudentSignal getStatus() {
        return status;
    }

    public void setStatus(StudentSignal status) {
        this.status = status;
    }
}
