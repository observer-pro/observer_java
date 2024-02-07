package pro.sky.observer_java.resources;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import pro.sky.observer_java.constants.Alert;

public class BubbleNotifications {
    private final Notification balloonNotificationConnected;
    private final Notification balloonNotificationDisconnected;
    private final Notification balloonNotificationError;
    private final Notification balloonNotificationWrongName;
    private final Notification balloonNotificationWrongRoom;

    private Notification  balloonNotificationAlert;

    private final Notification balloonNotificationRoomClosed;
    private final String groupId = "pro.sky";

    public BubbleNotifications(){

        balloonNotificationConnected =
                new Notification(groupId, "Connected to socket!", NotificationType.IDE_UPDATE);
        balloonNotificationConnected.setTitle("Connection success");

        balloonNotificationDisconnected =
                new Notification(groupId, "Disconnected from socket!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Disconnected!");

        balloonNotificationError =
                new Notification(groupId, "Error connecting to socket!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationWrongName =
                new Notification(groupId, "Name not filled!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationWrongRoom =
                new Notification(groupId, "Room not filled!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        balloonNotificationRoomClosed =   new Notification(groupId, "Room closed!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Room closed!");
    }

    public void error(Project openProject){
        balloonNotificationError.notify(openProject);
    }
    public void connected(Project openProject) {
        balloonNotificationConnected.notify(openProject);
    }
    public void disconnected(Project openProject) {
        balloonNotificationDisconnected.notify(openProject);
    }
    public void wrongName(Project openProject) {
        balloonNotificationWrongName.notify(openProject);
    }
    public void wrongRoom(Project openProject) {
        balloonNotificationWrongRoom.notify(openProject);
    }

    public void createNotificationAndNotify(Alert alert, String message, Project openProject) {
        switch (alert){
            case INFO -> {
                balloonNotificationAlert = new Notification(groupId, message, NotificationType.INFORMATION);
                balloonNotificationAlert.setTitle("Info!");
            }
            case SUCCESS -> {
                balloonNotificationAlert = new Notification(groupId, message, NotificationType.IDE_UPDATE);
                balloonNotificationAlert.setTitle("Success!");
            }
            case WARNING -> {
                balloonNotificationAlert = new Notification(groupId, message, NotificationType.WARNING);
                balloonNotificationAlert.setTitle("Warning!");
            }
            case ERROR -> {
                balloonNotificationAlert = new Notification(groupId, message, NotificationType.ERROR);
                balloonNotificationAlert.setTitle("ERROR!");
            }
        }
        balloonNotificationAlert.setContent(message).notify(openProject);
    }
    public void createChatNotificationAndNotify(String message, Project openProject) {
        new Notification(groupId, message, NotificationType.INFORMATION).setTitle("Host").notify(openProject);
    }

}
