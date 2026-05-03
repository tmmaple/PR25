package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Дія, що емулює цикл while () { }
 * @author afiliushkin
 */
public final class WhileTask implements Task {
    private final Condition condition;
    private final Task task;

    /**
     * @param condition умова для циклу
     * @param task дія, яка виконується в тілі циклу
     * @author afiliushkin
     */
    public WhileTask(Condition condition, Task task) {
        this.condition = condition;
        this.task = task;
    }

    @Override
    public boolean execute(Enemy enemy) {
        boolean result = true;
        while (result && condition.check())
            result = task.execute(enemy);
        return result;
    }

    @Override
    public Task copy() {
        return new WhileTask(this.condition, this.task.copy());
    }
}
