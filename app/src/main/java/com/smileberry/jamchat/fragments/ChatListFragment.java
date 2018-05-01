package com.smileberry.jamchat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smileberry.jamchat.R;
import com.smileberry.jamchat.adapters.MessagesAdapter;
import com.smileberry.jamchat.model.Message;
import com.smileberry.jamchat.service.ReceivedMessageHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private ReceivedMessageHandler chatReceiveMessageHandler;
    private MessagesAdapter messagesAdapter;

    public ChatListFragment() {

        chatReceiveMessageHandler = new ReceivedMessageHandler() {
            @Override
            public void taskFinished(List<Message> messages, String error) {
                if (messagesAdapter != null) {
                    List<Message> storedMessages = messagesAdapter.getItems();
                    for (Message message : messages) {
                        if (!storedMessages.contains(message)) {
                            messagesAdapter.getItems().add(message);
                        }
                    }
                    messagesAdapter.notifyDataSetInvalidated();
                    messagesAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        messagesAdapter = new MessagesAdapter(getActivity(), new ArrayList<Message>());

        ListView chatList = (ListView) rootView.findViewById(R.id.chat_list);
        chatList.setAdapter(messagesAdapter);

        return rootView;
    }

    public void updateChatList(Message message) {
        messagesAdapter.getItems().add(message);
        messagesAdapter.notifyDataSetInvalidated();
        messagesAdapter.notifyDataSetChanged();
    }

    public ReceivedMessageHandler getChatReceiveMessageHandler() {
        return chatReceiveMessageHandler;
    }
}
