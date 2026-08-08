package com.example.tycept;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatActivity extends Activity {

    private String conversationId;
    private String myName;

    private ListView listView;
    private EditText messageInput;
    private Button sendButton;
    private TextView titleView;

    private List<ChatMessage> messages = new ArrayList<>();
    private ChatMessageAdapter adapter;

    private Socket socket;
    private Emitter.Listener onNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversationId = getIntent().getStringExtra("conversationId");
        String passedTitle = getIntent().getStringExtra("conversationTitle");
        myName = SocketManager.getInstance().myName;

        titleView = findViewById(R.id.chatTitle);
        titleView.setText(passedTitle);

        listView = findViewById(R.id.messageListView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        adapter = new ChatMessageAdapter(this, messages);
        listView.setAdapter(adapter);

        socket = SocketManager.getInstance().getSocket();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        setupIncomingMessageListener();
        openConversation();
    }

    private void openConversation() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("id", conversationId);
        } catch (Exception e) {
            return;
        }

        socket.emit("open-conversation", payload, new Ack() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleOpenResult(args);
                    }
                });
            }
        });
    }

    private void handleOpenResult(Object[] args) {
        if (args.length == 0 || !(args[0] instanceof JSONObject)) return;
        JSONObject result = (JSONObject) args[0];
        if (result.has("error")) return;

        JSONArray history = result.optJSONArray("history");
        messages.clear();
        if (history != null) {
            for (int i = 0; i < history.length(); i++) {
                JSONObject m = history.optJSONObject(i);
                addMessageFromJson(m);
            }
        }
        adapter.notifyDataSetChanged();
        if (!messages.isEmpty()) {
            listView.setSelection(messages.size() - 1);
        }
    }

    private void addMessageFromJson(JSONObject m) {
        if (m == null) return;
        String senderName = m.optString("name");
        String text = m.optString("text");
        if (m.optBoolean("deleted")) {
            text = "(deleted)";
        }
        long time = m.optLong("time", System.currentTimeMillis());
        int type = senderName.equals(myName) ? ChatMessage.TYPE_SENT : ChatMessage.TYPE_RECEIVED;
        messages.add(new ChatMessage(senderName, text, time, type));
    }

    private void setupIncomingMessageListener() {
        onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (args.length == 0 || !(args[0] instanceof JSONObject)) return;
                        JSONObject m = (JSONObject) args[0];
                        String msgConvId = m.optString("conversationId");
                        if (!msgConvId.equals(conversationId)) return;
                        addMessageFromJson(m);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(messages.size() - 1);
                    }
                });
            }
        };
        socket.on("message", onNewMessage);
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        JSONObject payload = new JSONObject();
        try {
            payload.put("conversationId", conversationId);
            payload.put("text", text);
        } catch (Exception e) {
            return;
        }

        socket.emit("message", payload);
        messageInput.setText("");
        // The server will echo this message back via the "message" event
        // (broadcast to the whole room, including us), so we don't add it
        // locally here — that would double it up.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null && onNewMessage != null) {
            socket.off("message", onNewMessage);
        }
    }
}
