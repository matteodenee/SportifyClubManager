package com.sportify.manager.communication.network;

import java.io.Serializable;

public record LoginRequest(String userId) implements Serializable {}
