package io.particle.hydroalert;




public class Message implements Comparable<Message>{

    private String message;
    private String createdAt;

    public Message(String message, String createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(Message o) {
        return getCreatedAt().compareTo(o.getCreatedAt());
    }
}
