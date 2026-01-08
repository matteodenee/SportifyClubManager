package com.sportify.manager.CompositionManagement;

import java.sql.*;
import java.util.List;

import compositionManagement.Bl.RoleAssignment;
import com.sportify.manager.dao.PostgresUserDAO;

public class PostgresCompositionDAO extends CompositionDAO {

    private final Connection con;

    public PostgresCompositionDAO(Connection connection) {
        this.con = connection;
    }

    @Override
    public boolean saveComposition(int matchId, int teamId, List<RoleAssignment> assignments) {
        String del = "DELETE FROM match_composition WHERE match_id=? AND team_id=?";
        String ins = """
            INSERT INTO match_composition(match_id, team_id, role, slot_index, player_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try {
            con.setAutoCommit(false);

            try (PreparedStatement psDel = con.prepareStatement(del)) {
                psDel.setInt(1, matchId);
                psDel.setInt(2, teamId);
                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = con.prepareStatement(ins)) {
                for (RoleAssignment a : assignments) {
                    psIns.setInt(1, matchId);
                    psIns.setInt(2, teamId);
                    psIns.setString(3, a.getRole());
                    psIns.setInt(4, a.getSlotIndex());
                    psIns.setString(5, a.getPlayerId());
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            con.commit();
            con.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        }
    }

    @Override
    public boolean hasAnyComposition(int matchId, int teamId) {
        String sql = "SELECT 1 FROM match_composition WHERE match_id=? AND team_id=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
