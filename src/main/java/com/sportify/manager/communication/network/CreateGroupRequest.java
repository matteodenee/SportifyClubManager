package com.sportify.manager.communication.network;

import java.io.Serializable;
import java.util.List;

public record CreateGroupRequest(String groupName, List<String> memberIds) implements Serializable {}
