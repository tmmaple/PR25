package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;

public final class GameplayStats {
    public static GameplayStats global;

    private static final int GRAZE_BOMB_THRESHOLD = 100;
    private static final short GRAZE_BOMB_REMOVE_COOLDOWN = 10;

    private long totalScore;

    private long score;
    private int power;
    private long graze;

    private short grazeBombCounter;
    private short grazeBombRemoveCooldown;

    private static Flow.FlowNode<GameplayStats> node;

    public GameplayStats() { }

    public static void register() {
        if (node != null)
            return;
        node = new Flow.FlowNode<>(global, GameplayStats::update, GameplayStats::added);
        Flow.global.addToUpdate(node, 11);
    }

    public static void shutdown() {
        if (node == null)
            return;
        Flow.global.cut(node);
        node = null;
    }

    public boolean canBomb() {
        return grazeBombCounter == GRAZE_BOMB_THRESHOLD;
    }

    public void bombUsed() {
        graze -= grazeBombCounter;
        if (graze < 0)
            graze = 0;
        grazeBombCounter = 0;
    }

    public void graze() {
        score += 10;
        ++graze;
        grazeBombRemoveCooldown = GRAZE_BOMB_REMOVE_COOLDOWN;
        if (!canBomb())
            ++grazeBombCounter;
    }

    public void score(long amount) {
        score += amount;
        totalScore += amount;
    }

    public long getScore() {
        return score;
    }

    public long getGraze() {
        return graze;
    }

    public long getBombCounter() {
        return grazeBombCounter;
    }

    public void power(int amount) {
        if (power >= 100)
            return;
        power += amount;
        if (power >= 100)
            power = 100;
    }

    public int getPower() {
        return power;
    }

    public boolean isFullPower() {
        return power == 100;
    }

    public void reset() {
        power = 0;
        totalScore = 0L;
        score = 0L;
        graze = 0L;
        grazeBombCounter = 0;
    }

    public void resetLevel() {
        score = 0L;
        graze = 0L;
        if (!canBomb())
            grazeBombCounter = 0;
    }

    private int added() {
        reset();
        return 0;
    }

    private int update() {
        if (!GameplayManager.global.canUpdate())
            return Flow.FLOW_RESULT_CONTINUE;
        if (!canBomb() && grazeBombCounter > 0) {
            if (grazeBombRemoveCooldown > 0) {
                --grazeBombRemoveCooldown;
            }
            if (grazeBombRemoveCooldown == 0) {
                --grazeBombCounter;
                grazeBombRemoveCooldown = GRAZE_BOMB_REMOVE_COOLDOWN;
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
