package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Послідовно виконує внутрішні дії.
 * @author uwuhasmile
 */
public final class SequenceTask implements Task {
    private final Task[] tasks;
    private int index;

    /**
     * @param tasks набір дій для виконання
     * @author uwuhasmile
     */
    public SequenceTask(Task[] tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean execute(Enemy enemy) {
        boolean result = true;
        while (result && index < tasks.length) {
            result = tasks[index].execute(enemy);
            if (result) ++index;
        }
        if (result)
            index = 0;
        return result;
    }

    @Override
    public Task copy() {
        Task[] tasks = new Task[this.tasks.length];
        for (int i = 0; i < tasks.length; ++i)
            tasks[i] = this.tasks[i].copy();
        return new SequenceTask(tasks);
    }
}
