package com.example.tycept;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import org.json.JSONArray;

public class SocketManager {

    // Change this if your server URL ever changes.
    public static final String SERVER_URL = "https://chatting-htgk.onrender.com/";

    private static SocketManager instance;
    private Socket socket;

    // Session state kept here so it survives moving between activities.
    public String myName;
    public JSONArray conversations;

    private SocketManager() {
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        if (socket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.reconnection = true;
                opts.forceNew = false;
                socket = IO.socket(SERVER_URL, opts);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Bad server URL", e);
            }
        }
        if (!socket.connected()) {
            socket.connect();
        }
        return socket;
    }
}
