package pro.sky.observer_java.mapper;

import java.util.List;

public class EditorToString {
    public static String contentsAsString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append("\n");
        }
        return sb.toString();
    }
}
