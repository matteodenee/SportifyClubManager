package com.sportify.manager.CompositionManagement;

import java.util.List;
import compositionManagement.Bl.RoleAssignment;

public abstract class CompositionDAO {
    public abstract boolean saveComposition(int matchId, int teamId, List<RoleAssignment> assignments);
    public abstract boolean hasAnyComposition(int matchId, int teamId);
}
