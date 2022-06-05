package kr.hnu.mainproject_3;

import java.io.Serializable;

public class Msg implements Serializable {
    private String sender;
    private String receiver;
    private String title;
    private String time;
    private String contents;
    private String isReply;

    public Msg(String sender, String receiver, String title, String time, String contents, String isReply) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.time = time;
        this.contents = contents;
        this.isReply = isReply;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setIsReply(String isReply) {this.isReply = isReply; }

    public String getIsReply() { return isReply; }


}