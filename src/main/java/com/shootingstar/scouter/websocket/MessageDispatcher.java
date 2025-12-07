package com.shootingstar.scouter.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shootingstar.scouter.websocket.handlers.IMessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher
{
    private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);
    private final Map<String, IMessageHandler> handlers;
    private static final JsonParser JSON_PARSER = new JsonParser();

    public MessageDispatcher()
    {
        this.handlers = new HashMap<>();
    }

    public void registerHandler(String messageType, IMessageHandler handler)
    {
        handlers.put(messageType, handler);
    }

    public void dispatch(String message)
    {
        try {
            JsonElement element = JSON_PARSER.parse(message);
            JsonObject json = element.getAsJsonObject();
            
            if (!json.has("type")) {
                return;
            }
            
            String type = json.get("type").getAsString();
            IMessageHandler messageHandler = handlers.get(type);
            
            if (messageHandler != null && json.has("data")) {
                JsonElement data = json.get("data");
                // Pass the data element directly (can be array or object)
                if (data.isJsonObject()) {
                    messageHandler.handle(data.getAsJsonObject());
                } else if (data.isJsonArray()) {
                    // For arrays, wrap them in a temporary object
                    JsonObject wrapper = new JsonObject();
                    wrapper.add("data", data);
                    messageHandler.handle(wrapper);
                }
            }
        } catch (Exception ex) {
            log.error("Error dispatching message", ex);
        }
    }
}
