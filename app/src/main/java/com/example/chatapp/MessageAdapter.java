package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        int layout = message.getType() == Message.TYPE_SENT
                ? R.layout.item_message_sent
                : R.layout.item_message_received;

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(layout, parent, false);
        }

        TextView text = view.findViewById(R.id.messageText);
        TextView time = view.findViewById(R.id.messageTime);
        text.setText(message.getText());
        time.setText(message.getTime());

        return view;
    }
}
