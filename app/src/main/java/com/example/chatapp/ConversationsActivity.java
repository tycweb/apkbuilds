package com.example.tycept;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.json.JSONObject;

public class ConversationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        TextView presenceLine = findViewById(R.id.presenceLine);
        String myName = SocketManager.getInstance().myName;
        presenceLine.setText("logged in as " + myName);

        ListView listView = findViewById(R.id.conversationListView);
        final ConversationAdapter adapter = new ConversationAdapter(
                this,
                SocketManager.getInstance().conversations,
                myName
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.startAnimation(AnimationUtils.loadAnimation(ConversationsActivity.this, R.anim.press_scale));
                JSONObject conv = adapter.getItem(position);
                String convId = conv.optString("id");
                String convTitle = ((TextView) view.findViewById(R.id.convTitle)).getText().toString();

                Intent intent = new Intent(ConversationsActivity.this, ChatActivity.class);
                intent.putExtra("conversationId", convId);
                intent.putExtra("conversationTitle", convTitle);
                startActivity(intent);
            }
        });

        findViewById(R.id.brandLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ConversationsActivity.this, R.anim.press_scale));
                Toast.makeText(ConversationsActivity.this, "Signed in as " + SocketManager.getInstance().myName, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.newChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ConversationsActivity.this, R.anim.press_scale));
                Toast.makeText(ConversationsActivity.this, "New chat — coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.featuresTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ConversationsActivity.this, R.anim.press_scale));
                Toast.makeText(ConversationsActivity.this, "Features tab — coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.menuTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(ConversationsActivity.this, R.anim.press_scale));
                Toast.makeText(ConversationsActivity.this, "Menu tab — coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
