package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Дія, що чекає певну кількість тіків перед продовженням.
 * @author afiliushkin
 */
public final class WaitTask implements Task {
    private final Times ticks;
    private short left;

    /**
     * @param ticks функція, що повертає кількість тіків
     * @author afiliushkin
     */
    public WaitTask(Times ticks) {
        this.ticks = ticks;
        left = -1;
    }

    @Override
    public boolean execute(Enemy enemy) {
        if (left > 0) {
            --left;
            if (left == 0) {
                left = -1;
                return true;
            }
        } else if (left == -1)
            left = ticks.get();
        return false;
    }

    @Override
    public Task copy() {
        return new WaitTask(ticks);
    }
}
