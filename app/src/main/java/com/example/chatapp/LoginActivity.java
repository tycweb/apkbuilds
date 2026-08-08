package com.example.tycept;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.socket.client.Ack;
import io.socket.client.Socket;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText nameInput;
    private EditText passwordInput;
    private Button joinButton;
    private TextView errorText;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        joinButton = findViewById(R.id.joinButton);
        errorText = findViewById(R.id.errorText);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptJoin();
            }
        });
    }

    private void attemptJoin() {
        final String name = nameInput.getText().toString().trim();
        final String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(name)) {
            errorText.setText("Enter a name");
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        if (password.length() < 4) {
            errorText.setText("Password must be at least 4 characters");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        errorText.setVisibility(View.GONE);
        joinButton.setEnabled(false);
        joinButton.setText("Connecting…");

        final Socket socket = SocketManager.getInstance().getSocket();

        JSONObject payload = new JSONObject();
        try {
            payload.put("name", name);
            payload.put("password", password);
        } catch (Exception e) {
            return;
        }

        socket.emit("join", payload, new Ack() {
            @Override
            public void call(final Object... args) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleJoinResponse(args);
                    }
                });
            }
        });
    }

    private void handleJoinResponse(Object[] args) {
        joinButton.setEnabled(true);
        joinButton.setText("Join chat");

        if (args.length == 0 || !(args[0] instanceof JSONObject)) {
            errorText.setText("Something went wrong. Try again.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        JSONObject result = (JSONObject) args[0];

        if (result.has("error")) {
            String error = result.optString("error");
            if ("wrong-password".equals(error)) {
                errorText.setText("Wrong password for that name");
            } else if ("password-required".equals(error)) {
                errorText.setText("Password too short");
            } else {
                errorText.setText("Couldn't join: " + error);
            }
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        SocketManager.getInstance().myName = result.optString("name");
        SocketManager.getInstance().conversations = result.optJSONArray("conversations");

        Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
        startActivity(intent);
        finish();
    }
}
