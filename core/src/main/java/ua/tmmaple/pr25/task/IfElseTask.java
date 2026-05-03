package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Умовна дія ворога.
 * @author afiliushkin
 */
public final class IfElseTask implements Task {
    private final Condition condition;
    private final Task thenArm;
    private final Task elseArm;

    private byte conditionResult;

    /**
     * @param condition умова, що перевіряється
     * @param thenArm якщо умова повертає true
     * @param elseArm якщо умова повертає false
     * @author afiliushkin
     */
    public IfElseTask(Condition condition, Task thenArm, Task elseArm) {
        this.condition = condition;
        this.thenArm = thenArm;
        this.elseArm = elseArm;
    }

    /**
     * @param condition умова, що перевіряється
     * @param thenArm якщо умова повертає true
     * @author afiliushkin
     */
    public IfElseTask(Condition condition, Task thenArm) {
        this.condition = condition;
        this.thenArm = thenArm;
        elseArm = null;
    }

    @Override
    public boolean execute(Enemy enemy) {
        if (conditionResult == 0)
            conditionResult = (byte) (condition.check() ? 1 : -1);
        boolean result;
        if (conditionResult == 1)
            result = thenArm.execute(enemy);
        else if (elseArm != null)
            result = elseArm.execute(enemy);
        else
            result = true;
        if (result) {
            conditionResult = 0;
            return true;
        }
        return false;
    }

    @Override
    public Task copy() {
        return new IfElseTask(condition, thenArm.copy(), elseArm == null ? null : elseArm.copy());
    }
}
