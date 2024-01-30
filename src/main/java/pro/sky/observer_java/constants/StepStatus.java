package pro.sky.observer_java.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StepStatus {
    @JsonProperty("NONE")
    NONE,
    @JsonProperty("IN_PROGRESS")
    IN_PROGRESS,
    @JsonProperty("HELP")
    HELP,
    @JsonProperty("DONE")
    DONE,
    @JsonProperty("ACCEPTED")
    ACCEPTED
}
