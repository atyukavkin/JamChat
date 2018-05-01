package com.smileberry.jamchat.service;


import com.smileberry.jamchat.model.Message;

import java.util.List;

public interface ReceivedMessageHandler {
    public void taskFinished(List<Message> messages, String error);
}
