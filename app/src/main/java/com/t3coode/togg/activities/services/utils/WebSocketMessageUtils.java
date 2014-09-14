package com.t3coode.togg.activities.services.utils;

import com.codebutler.android_websockets.WebSocketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t3coode.togg.services.dtos.ServerWebSocketMessageDTO;
import com.t3coode.togg.services.dtos.WebSocketMessageDTO;
import com.t3coode.togg.services.dtos.WebSocketMessageDTO.ActionTypes;
import com.t3coode.togg.services.utils.JsonableImpl;

public class WebSocketMessageUtils {

    public static WebSocketMessageDTO processMessage(WebSocketClient client,
            String message, MessageActionsProcessor messageActionsProcessor) {

        WebSocketMessageDTO socketMessage = null;

        ObjectMapper mapper = JsonableImpl.createMapper();
        try {
            socketMessage = mapper.readValue(message,
                    ServerWebSocketMessageDTO.class).buildMessage(message);

            if (socketMessage.isPing()) {
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put("type", "pong");
                client.send(node.toString());
            } else if (socketMessage.getData() != null) {

                if (socketMessage.getActionType() == ActionTypes.UPDATE) {
                    messageActionsProcessor
                            .onMessageActionUpdate(socketMessage);
                } else if (socketMessage.getActionType() == ActionTypes.INSERT) {
                    messageActionsProcessor
                            .onMessageActionCreate(socketMessage);
                } else if (socketMessage.getActionType() == ActionTypes.DELETE) {
                    messageActionsProcessor
                            .onMessageActionDelete(socketMessage);
                } else {
                    messageActionsProcessor.onMessageActionOther(socketMessage);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return socketMessage;
    }

    public static interface MessageActionsProcessor {
        void onMessageActionUpdate(WebSocketMessageDTO message);

        void onMessageActionCreate(WebSocketMessageDTO message);

        void onMessageActionDelete(WebSocketMessageDTO message);

        void onMessageActionOther(WebSocketMessageDTO message);
    }
}
