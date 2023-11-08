package pro.sky.observer_java.model;

import pro.sky.observer_java.constants.StringFormats;

public class Steps {
    String name;
    String content;
    String language;
    String type;

    public Steps(){}

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

    @Override
    public String toString(){
        return String.format(StringFormats.TASK_FORMAT, this.name);
    }
}
