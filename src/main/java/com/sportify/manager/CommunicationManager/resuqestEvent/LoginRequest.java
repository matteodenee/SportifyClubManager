package com.sportify.manager.CommunicationManager.resuqestEvent;

import java.io.Serializable;

public record LoginRequest(String userId) implements Serializable {}
