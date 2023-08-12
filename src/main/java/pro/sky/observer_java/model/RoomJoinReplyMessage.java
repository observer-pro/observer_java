package pro.sky.observer_java.model;

public class RoomJoinReplyMessage {
    Long room_id;
    Long user_id;


    public Long getRoomId() {
        return room_id;
    }

    public void setRoomId(Long roomId) {
        this.room_id = roomId;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }
}
