package com.sportify.manager.communication.network;

import com.sportify.manager.services.NetMessage;
import java.io.Serializable;

public record NewMessageEvent(NetMessage message) implements Serializable {}
