package com.example.tycept;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
        final ChatMessage message = getItem(position);
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
        ImageView imageView = view.findViewById(R.id.messageImage);
        View videoContainer = view.findViewById(R.id.messageVideoContainer);

        if (senderView != null) {
            if (message.type == ChatMessage.TYPE_RECEIVED) {
                senderView.setText(message.senderName);
                senderView.setVisibility(View.VISIBLE);
            } else {
                senderView.setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(message.text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(message.text);
        }

        if (!TextUtils.isEmpty(message.imageUrl)) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(message.imageUrl).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExternally(message.imageUrl, "image/*");
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
            Glide.with(getContext()).clear(imageView);
        }

        if (!TextUtils.isEmpty(message.videoUrl)) {
            videoContainer.setVisibility(View.VISIBLE);
            videoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExternally(message.videoUrl, "video/*");
                }
            });
        } else {
            videoContainer.setVisibility(View.GONE);
        }

        timeView.setText(DateFormat.format("hh:mm a", message.time));

        return view;
    }

    private void openExternally(String url, String mimeType) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), mimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e2) {
                Toast.makeText(getContext(), "No app found to open this", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
