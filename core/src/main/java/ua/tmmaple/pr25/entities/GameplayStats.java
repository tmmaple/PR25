package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;

/**
 * Статистика гравця під час гри.
 * @author afiliushkin
 */
public final class GameplayStats {
    public static GameplayStats global;

    private static final int GRAZE_BOMB_THRESHOLD = 10;
    private static final short GRAZE_BOMB_REMOVE_COOLDOWN = 120;

    private long hiScore;
    private long score;
    private int power;
    private long graze;

    private int bombsUsed;
    private int coinsUsed;

    private short grazeBombCounter;
    private short grazeBombRemoveCooldown;

    private static Flow.FlowNode<GameplayStats> node;

    public GameplayStats() { }

    /**
     * Реєструє в списку оновлень.
     * @author afiliushkin
     */
    public static void register() {
        if (node != null)
            return;
        node = new Flow.FlowNode<>(global, GameplayStats::update, GameplayStats::added, GameplayStats::removed);
        Flow.global.addToUpdate(node, 11);
    }

    /**
     * Видаляє зі списку оновлень.
     * @author afiliushkin
     */
    public static void shutdown() {
        if (node == null)
            return;
        Flow.global.cut(node);
        node = null;
    }

    /**
     * @return чи має гравець достатньо очок дотику, щоб можна було скористатися бомбою
     * @author afiliushkin
     */
    public boolean canBomb() {
        return grazeBombCounter == GRAZE_BOMB_THRESHOLD;
    }

    /**
     * Має викликатись при використанні гравцем бомби, тоді віднімає кількість дотиків, потрібних для здобуття бомби.
     * @author afiliushkin
     */
    public void bombUsed() {
        graze -= grazeBombCounter;
        if (graze < 0)
            graze = 0;
        grazeBombCounter = 0;
        ++bombsUsed;
    }

    /**
     * Зарахувати дотик гравцеві.
     * @author afiliushkin
     */
    public void graze() {
        score += 10;
        ++graze;
        grazeBombRemoveCooldown = GRAZE_BOMB_REMOVE_COOLDOWN;
        if (!canBomb())
            ++grazeBombCounter;
    }

    /**
     * Зарахувати очки гравцеві.
     * @param amount кількість очок
     * @author afiliushkin
     */
    public void score(long amount) {
        score += amount;
        if (score > hiScore)
            hiScore = score;
    }

    /**
     * Зарахувати потужність гравцеві, якщо вона ще не досягла 100.
     * @param amount кількість очок потужності
     * @author afiliushkin
     */
    public void power(int amount) {
        if (power >= 100)
            return;
        power += amount;
        if (power >= 100)
            power = 100;
    }

    /**
     * @return поточна кількість очок
     * @author afiliushkin
     */
    public long getScore() {
        return score;
    }

    /**
     * @return поточна найвища кількість очок
     * @author afiliushkin
     */
    public long getHiScore() {
        return hiScore;
    }

    /**
     * @return поточна кількість очок дотику
     * @author afiliushkin
     */
    public long getGraze() {
        return graze;
    }

    /**
     * @return поточна кількість очок дотику для використання бомби
     * @author afiliushkin
     */
    public long getBombCounter() {
        return grazeBombCounter;
    }

    /**
     * @return поточна потужність
     * @author afiliushkin
     */
    public int getPower() {
        return power;
    }

    /**
     * @return чи досяг гравець максимальної кількості очок потужності
     * @author afiliushkin
     */
    public boolean isFullPower() {
        return power == 100;
    }

    /**
     * Скидає статистику для нової гри
     * @author afiliushkin
     */
    public void reset() {
        hiScore = God.global.hiScore();
        power = 0;
        score = 0L;
        graze = 0L;
        grazeBombCounter = 0;
        coinsUsed = 0;
    }

    /**
     * Зараховує використання монети для продовження гри після смерті.
     * @author afiliushkin
     */
    public void coinUsed() {
        ++coinsUsed;
    }

    /**
     * @return кількість бомб, використаних за гру
     *
     */
    public int getBombsUsed() {
        return bombsUsed;
    }

    /**
     * @return кількість монет, використаних за гру
     * @author afiliushkin
     */
    public int getCoinsUsed() {
        return coinsUsed;
    }

    /**
     * Нараховує додаткові очки за дотик, та скидає значення до таких для наступного рівня.
     * @author afiliushkin
     */
    public void nextLevel() {
        score += graze * 100;
        graze = 0L;
        if (!canBomb())
            grazeBombCounter = 0;
    }

    /**
     * Скидає очки дотику та потужності після респавну.
     * @author afiliushkin
     */
    public void respawn() {
        graze = 0L;
        power = 0;
        grazeBombCounter = 0;
    }

    /**
     * Скидає показники до нуля після додавання до списку оновлень (нова гра).
     * @author afiliushkin
     */
    private int added() {
        reset();
        return 0;
    }

    /**
     * Оновлює таймер очок дотику для використання бомби.
     * @author afiliushkin
     */
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

    /**
     * Записує остаточний результат
     * @author afiliushkin
     */
    private int removed() {
        God.global.updateHiScore(hiScore);
        return 0;
    }
}
