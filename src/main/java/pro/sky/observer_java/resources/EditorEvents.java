package pro.sky.observer_java.resources;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import pro.sky.observer_java.mapper.EditorToString;

import java.io.IOException;
import java.nio.file.Files;

public class EditorEvents {

    Project openProject;

    public EditorEvents(Project openProject) {
        this.openProject = openProject;
    }

    public String getOpenEditorText() throws IOException {
        FileEditor editor = FileEditorManager.getInstance(openProject).getSelectedEditor();
        assert editor != null;
        return EditorToString.contentsAsString(Files.readAllLines(editor.getFile().toNioPath()));
    }
}
