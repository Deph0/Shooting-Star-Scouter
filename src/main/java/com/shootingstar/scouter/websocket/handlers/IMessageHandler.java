package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonObject;

public interface IMessageHandler
{
    public void onData(JsonObject data);
}
