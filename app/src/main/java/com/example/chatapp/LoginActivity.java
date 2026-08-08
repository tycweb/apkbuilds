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
import io.socket.emitter.Emitter;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText nameInput;
    private EditText passwordInput;
    private Button joinButton;
    private TextView errorText;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;
    private boolean joinResolved;

    private Emitter.Listener onConnectError;
    private Emitter.Listener onConnect;

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
            showError("Enter a name");
            return;
        }
        if (password.length() < 4) {
            showError("Password must be at least 4 characters");
            return;
        }

        errorText.setVisibility(View.GONE);
        joinButton.setEnabled(false);
        joinButton.setText("Waking up server…");
        joinResolved = false;

        final Socket socket = SocketManager.getInstance().getSocket();

        // Free hosting tiers (like Render's free plan) can take 30-60s to wake
        // up from sleep on the first request. Give clear feedback instead of a
        // silent hang, and bail out with a helpful message if it's truly stuck.
        onConnect = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!joinResolved) {
                            joinButton.setText("Connecting…");
                        }
                    }
                });
            }
        };
        onConnectError = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (joinResolved) return;
                        String detail = args.length > 0 ? String.valueOf(args[0]) : "unknown error";
                        showError("Can't reach server: " + detail);
                        resetButton();
                    }
                });
            }
        };
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (joinResolved) return;
                showError("Still waiting on the server. Free hosting can take up to a minute to wake up — try again in a bit.");
                resetButton();
            }
        };
        mainHandler.postDelayed(timeoutRunnable, 45000);

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
                        joinResolved = true;
                        mainHandler.removeCallbacks(timeoutRunnable);
                        handleJoinResponse(args);
                    }
                });
            }
        });
    }

    private void resetButton() {
        joinResolved = true;
        joinButton.setEnabled(true);
        joinButton.setText("Join chat");
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void handleJoinResponse(Object[] args) {
        resetButton();

        if (args.length == 0 || !(args[0] instanceof JSONObject)) {
            showError("Something went wrong. Try again.");
            return;
        }

        JSONObject result = (JSONObject) args[0];

        if (result.has("error")) {
            String error = result.optString("error");
            if ("wrong-password".equals(error)) {
                showError("Wrong password for that name");
            } else if ("password-required".equals(error)) {
                showError("Password too short");
            } else {
                showError("Couldn't join: " + error);
            }
            return;
        }

        SocketManager.getInstance().myName = result.optString("name");
        SocketManager.getInstance().conversations = result.optJSONArray("conversations");

        Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
        startActivity(intent);
        finish();
    }
}
