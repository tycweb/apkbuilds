package com.example.tycept;

public class ChatMessage {
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    public String senderName;
    public String text;
    public long time;
    public int type;

    public ChatMessage(String senderName, String text, long time, int type) {
        this.senderName = senderName;
        this.text = text;
        this.time = time;
        this.type = type;
    }
}
