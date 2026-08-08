package com.example.chatapp;

public class Message {
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private String text;
    private int type;
    private String time;

    public Message(String text, int type, String time) {
        this.text = text;
        this.type = type;
        this.time = time;
    }

    public String getText() { return text; }
    public int getType() { return type; }
    public String getTime() { return time; }
}
