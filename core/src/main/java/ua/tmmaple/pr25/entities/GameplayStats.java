package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;

public final class GameplayStats {
    public static GameplayStats global;

    private static final byte COINS = 4;
    private static final int GRAZE_BOMB_THRESHOLD = 50;
    private static final short GRAZE_BOMB_REMOVE_COOLDOWN = 10;

    private long totalScore;
    private byte coins;

    private int power;
    private long score;
    private long graze;

    private short grazeBombCounter;
    private short grazeBombRemoveCooldown;

    private static Flow.FlowNode<GameplayStats> node;

    public GameplayStats() { }

    public static void register() {
        if (node != null)
            return;
        node = new Flow.FlowNode<>(global, GameplayStats::update, GameplayStats::added);
        Flow.global.addToUpdate(node, 2);
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

    public void graze() {
        ++graze;
        grazeBombRemoveCooldown = GRAZE_BOMB_REMOVE_COOLDOWN;
        if (!canBomb())
            ++grazeBombCounter;
    }

    public void score(long amount) {
        score += amount;
        totalScore += amount;
    }

    public void reset() {
        totalScore = 0L;
        coins = COINS;
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
