package pro.sky.test_task_plugin;

import java.io.File;
public class FileStructureStringer {


    public static String printDirectoryTree(String project, File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        int indent = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(project);
        sb.append("\n");

        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, int indent,
                                           StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("- ");
        sb.append(folder.getName());
       // sb.append("/");
        sb.append("\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                printDirectoryTree(file, indent + 1, sb);
            } else {
                printFile(file, indent + 1, sb);
            }
        }

    }

    private static void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("-- ");
        sb.append(file.getName());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        return "  ".repeat(Math.max(0, indent));
    }
}