package com.sportify.manager.dao;

import java.util.List;
import com.sportify.manager.services.RoleAssignment;


public interface CompositionDAO {


    boolean saveComposition(int matchId, int teamId, List<RoleAssignment> assignments);


    boolean hasAnyComposition(int matchId, int teamId);
}