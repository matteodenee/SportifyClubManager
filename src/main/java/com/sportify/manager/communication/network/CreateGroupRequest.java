package com.sportify.manager.communication.network;

import java.io.Serializable;

public record CreateGroupRequest(String groupName) implements Serializable {}
