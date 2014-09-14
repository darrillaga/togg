package com.t3coode.togg.services.dtos;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t3coode.togg.services.dtos.WebSocketMessageDTO.ActionTypes;
import com.t3coode.togg.services.dtos.WebSocketMessageDTO.ServerModels;
import com.t3coode.togg.services.utils.JsonableImpl;

public class ServerWebSocketMessageDTO extends BaseDTO {

    private String type;
    private ObjectNode data;
    private String model;
    private String action;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectNode getData() {
        return data;
    }

    public void setData(ObjectNode data) {
        this.data = data;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isPing() {
        return type != null
                && type.equalsIgnoreCase(WebSocketMessageDTO.PING_TYPE);
    }

    public WebSocketMessageDTO buildMessage(String messageJSON) {

        WebSocketMessageDTO message = null;

        if (isPing()) {
            message = new WebSocketMessageDTO();
            message.setType(WebSocketMessageDTO.PING_TYPE);
            return message;
        }

        boolean found = false;
        int index = 0;

        ActionTypes actionType = ActionTypes.OTHER;
        ActionTypes[] actionTypes = ActionTypes.values();

        while (index < actionTypes.length && !found) {
            actionType = actionTypes[index];
            if (actionType.toString().equalsIgnoreCase(action)) {
                found = true;
            } else {
                index++;
            }
        }

        actionType = (found) ? actionType : ActionTypes.OTHER;

        ServerModels serverModel = null;
        ServerModels[] serverModels = ServerModels.values();
        index = 0;
        found = false;

        while (index < serverModels.length && !found) {
            serverModel = serverModels[index];
            if (serverModel.toString().equalsIgnoreCase(model)) {
                found = true;
            } else {
                index++;
            }
        }

        message = new WebSocketMessageDTO();
        message.setAction(action);
        message.setRawData(data.toString());
        message.setModel(model);

        try {
            message.setData((BaseDTO) JsonableImpl.createMapper().readValue(
                    data.toString(), serverModel.getServerClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        message.setActionType(actionType);

        return message;
    }
}
