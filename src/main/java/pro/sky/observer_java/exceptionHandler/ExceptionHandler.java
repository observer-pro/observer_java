package pro.sky.observer_java.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.model.ErrorReport;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.Arrays;
import java.util.Objects;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String currentlyOpenFileString = Objects.requireNonNull(FileEditorManager.getInstance(
                ResourceManager
                        .getInstance()
                        .getToolWindow()
                        .getProject()
        ).getSelectedEditor()).getFile().getPath();

        ErrorReport errorReport = new ErrorReport(
                ResourceManager.getInstance().getRoomId(),
                ResourceManager.getInstance().getUserId(),
                Arrays.toString(e.getStackTrace()),
                currentlyOpenFileString,
                e.getMessage()
                );

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(errorReport);
            ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.ERROR_FROM_CLIENT,new JSONObject(json));
        } catch (JsonProcessingException|JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}
