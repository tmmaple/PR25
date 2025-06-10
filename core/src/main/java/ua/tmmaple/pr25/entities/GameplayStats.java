package ua.tmmaple.pr25.entities;

public final class GameplayStats {
    private static final byte COINS = 4;
    private static final int GRAZE_BOMB_THRESHOLD = 25;

    private long totalScore;
    private byte coins;

    private int power;
    private long score;
    private long graze;

    private short grazeBombCounter;

    public GameplayStats() { }

    public boolean canBomb() {
        return grazeBombCounter == GRAZE_BOMB_THRESHOLD;
    }

    public void graze() {
        ++graze;
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
    }
}
