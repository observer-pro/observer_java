package pro.sky.observer_java.resources;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class BubbleNotifications {
    private final Notification balloonNotificationConnected;
    private final Notification balloonNotificationDisconnected;
    private final Notification balloonNotificationError;
    private final Notification balloonNotificationWrongName;
    private final Notification balloonNotificationWrongRoom;

    public BubbleNotifications(){
        String groupId = "pro.sky";

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
}
