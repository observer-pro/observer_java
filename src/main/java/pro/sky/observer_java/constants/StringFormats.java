package pro.sky.observer_java.constants;

public class StringFormats {
    public final static String TASK_FORMAT = "Task %s";
    public final static String TASK_HEADER_FORMAT = "Tasks (%d)";
    public static final String CHAT_TAB_READ = "Chat";
    public static final String CHAT_TAB_UNREAD = "Chat (+%d)";
    public static final String AI_HELP_UNREAD = "AI Help (DONE)";
    public static final String AI_HELP_READ = "AI Help";
    public static final String DONE_MESSAGE_FORMAT = "You have sent %s for a review";
    public static final String HELP_MESSAGE_FORMAT = "You have requested help on %s";

    public static final String TASK_ACCEPTED = "%s has been accepted by the mentor";

    public static final String CHAT_FORMAT = "%s : %s \n";

    public static final String TASK_FIELD_HTML_FORMAT = "<style>h1, h2, h3 {margin: 0px;} p {margin-top: 0px}</style> %s";

    public static final String TASK_SQUARES_FORMAT = "<html> " +
            "<head> " +
            "<meta charset=\"utf-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width\"> " +
            "<title>JS Bin</title> " +
            "</head> " +
            "<body style=\"font-family: Arial, sans-serif;\"> <font color=\"#444\" size=\"14px\"><b>" +
            "<main style=\"color: #444; font-size: 14px; font-weight: bold; line-height: 30px;\"> %s" +
            "</main> " +
            "</body> " +
            "</html> ";

    public static final String SPAN_STYLE_FORMAT = "<span style=\"padding: 6px; border-top-left-radius: 4px; background-color: %s; font-family: Arial, sans-serif; border-collapse: separate; \">&nbsp;%s&nbsp;</span>&nbsp";
}
