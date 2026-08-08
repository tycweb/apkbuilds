package com.example.chatapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private ListView listView;
    private EditText inputField;
    private Button sendButton;

    private List<Message> messageList;
    private MessageAdapter adapter;
    private Handler handler = new Handler();
    private Random random = new Random();

    private String[] autoReplies = {
            "Got it!",
            "Interesting, tell me more.",
            "Haha, nice.",
            "I see what you mean.",
            "That makes sense.",
            "Okay, sounds good!",
            "Really? That's cool.",
            "Thanks for sharing that."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList);
        listView.setAdapter(adapter);

        addMessage("Hey there! \uD83D\uDC4B Send me a message.", Message.TYPE_RECEIVED);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String text = inputField.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        addMessage(text, Message.TYPE_SENT);
        inputField.setText("");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String reply = autoReplies[random.nextInt(autoReplies.length)];
                addMessage(reply, Message.TYPE_RECEIVED);
            }
        }, 700);
    }

    private void addMessage(String text, int type) {
        String time = DateFormat.format("hh:mm a", System.currentTimeMillis()).toString();
        messageList.add(new Message(text, type, time));
        adapter.notifyDataSetChanged();
        listView.setSelection(messageList.size() - 1);
    }
}
