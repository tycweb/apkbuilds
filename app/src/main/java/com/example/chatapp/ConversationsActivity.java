package com.example.tycept;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

public class ConversationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        TextView title = findViewById(R.id.screenTitle);
        title.setText("Chats");

        ListView listView = findViewById(R.id.conversationListView);
        final ConversationAdapter adapter = new ConversationAdapter(
                this,
                SocketManager.getInstance().conversations,
                SocketManager.getInstance().myName
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject conv = adapter.getItem(position);
                String convId = conv.optString("id");
                String convTitle = ((TextView) view.findViewById(R.id.convTitle)).getText().toString();

                Intent intent = new Intent(ConversationsActivity.this, ChatActivity.class);
                intent.putExtra("conversationId", convId);
                intent.putExtra("conversationTitle", convTitle);
                startActivity(intent);
            }
        });
    }
}
