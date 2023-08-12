package pro.sky.observer_java.model;

public class DataMessage {
   private Long client_id;
   private String data;

    public DataMessage(Long client_id, String data) {
        this.client_id = client_id;
        this.data = data;
    }

    public Long getClient_id() {
        return client_id;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
