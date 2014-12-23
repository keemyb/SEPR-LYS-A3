package test;

import gameLogic.Player;
import gameLogic.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerManagerTest {
    private PlayerManager pm;

    @Before
    public void setUp() throws Exception {
        pm = new PlayerManager();
        pm.createPlayers(2);
    }

    @Test
    public void testGetCurrentPlayer() throws Exception {
        Player p1 = pm.getCurrentPlayer();
        pm.turnOver();

        // player should change after PlayerManager.turnOver() is called
        assertNotEquals(p1, pm.getCurrentPlayer());
    }

    @Test
    public void testPlayerChanged() throws Exception {
        Player p1 = pm.getCurrentPlayer();
        int resourceCount = p1.getResources().size();
        int goalCount = p1.getGoals().size();

        pm.turnOver();
        pm.turnOver();

        // resource count should increase when p1 has another turn
        assertTrue(p1.getResources().size() > resourceCount);
        assertTrue(p1.getGoals().size() > goalCount);
    }
}