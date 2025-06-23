package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Дія, що емулює цикл for { }
 * @author uwuhasmile
 */
public final class RepeatTask implements Task {
    private final Times times;
    private final Task task;

    private short left;

    /**
     * @param times кількість повторень
     * @param task дія, яка виконується в тілі циклу
     * @author uwuhasmile
     */
    public RepeatTask(Times times, Task task) {
        this.times = times;
        this.task = task;
        left = -1;
    }

    @Override
    public boolean execute(Enemy enemy) {
        if (left == -1)
            left = times.get();
        boolean result = true;
        while (result && left > 0) {
            result = task.execute(enemy);
            if (result)
                --left;
        }
        if (left == 0)
            left = -1;
        return result;
    }

    @Override
    public Task copy() {
        return new RepeatTask(times, task.copy());
    }
}
