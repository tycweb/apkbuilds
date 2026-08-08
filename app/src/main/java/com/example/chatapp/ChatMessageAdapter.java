package com.example.tycept;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = getItem(position);
        int layout = message.type == ChatMessage.TYPE_SENT
                ? R.layout.item_message_sent
                : R.layout.item_message_received;

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(layout, parent, false);
        }

        TextView senderView = view.findViewById(R.id.messageSender);
        TextView textView = view.findViewById(R.id.messageText);
        TextView timeView = view.findViewById(R.id.messageTime);

        if (senderView != null) {
            if (message.type == ChatMessage.TYPE_RECEIVED) {
                senderView.setText(message.senderName);
                senderView.setVisibility(View.VISIBLE);
            } else {
                senderView.setVisibility(View.GONE);
            }
        }

        textView.setText(message.text);
        timeView.setText(DateFormat.format("hh:mm a", message.time));

        return view;
    }
}
