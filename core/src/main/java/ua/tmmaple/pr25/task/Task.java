package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Дія, що виконується ворогом.
 * @author uwuhasmile
 */
public interface Task {
    /**
     * @return true, якщо виконання дії завершено, та можна продовжити, false, якщо дія ще не завершена
     * @author uwuhasmile
     */
    boolean execute(Enemy enemy);

    /**
     * @return глибоко скопійована дія
     * @author uwuhasmile
     */
    default Task copy() { return this; }

    /**
     * @param condition умова, що перевіряється
     * @param then якщо умова повертає true
     * @return умовна дія
     * @author uwuhasmile
     */
    static IfElseTask ifElse(Condition condition, Task then) {
        return new IfElseTask(condition, then);
    }

    /**
     * @param condition умова, що перевіряється
     * @param thenArm якщо умова повертає true
     * @param elseArm якщо умова повертає false
     * @return умовна дія
     * @author uwuhasmile
     */
    static IfElseTask ifElse(Condition condition, Task thenArm, Task elseArm) {
        return new IfElseTask(condition, thenArm, elseArm);
    }

    /**
     * @param condition умова для циклу
     * @param body дія, яка виконується в тілі циклу
     * @return дія, що емулює цикл while () { }
     * @author uwuhasmile
     */
    static WhileTask whileLoop(Condition condition, Task body) {
        return new WhileTask(condition, body);
    }

    /**
     * @param times кількість повторень
     * @param body дія, яка виконується в тілі циклу
     * @return дія, що емулює цикл for { }
     * @author uwuhasmile
     */
    static RepeatTask repeat(Times times, Task body) {
        return new RepeatTask(times, body);
    }

    /**
     * @param ticks функція, що повертає кількість тіків
     * @return дія, що чекає певну кількість тіків перед продовженням
     * @author uwuhasmile
     */
    static WaitTask wait(Times ticks) {
        return new WaitTask(ticks);
    }

    /**
     * @param tasks набір дій для виконання
     * @return дія, що послідовно виконує внутрішні дії.
     * @author uwuhasmile
     */
    static SequenceTask sequence(Task... tasks) {
        return new SequenceTask(tasks);
    }

    /**
     * @param keyframes набір ключів (дій за часом)
     * @return дія, що виконує внутрішні дії, що розставлені за часом
     * @author uwuhasmile
     */
    static TimelineTask timeline(TimelineTask.Keyframe... keyframes) {
        return new TimelineTask(keyframes);
    }

    /**
     * @param task тіло ключа
     * @return ключ таймлайну з часовим маркером 0
     * @author uwuhasmile
     */
    static TimelineTask.Keyframe keyframe(Task task) {
        return new TimelineTask.Keyframe(task);
    }

    /**
     * @param timeMarker час, у тіках (один тік = 1/60 секунди)
     * @param task тіло ключа
     * @return ключ таймлайну
     * @author uwuhasmile
     */
    static TimelineTask.Keyframe keyframe(int timeMarker, Task task) {
        return new TimelineTask.Keyframe((short) timeMarker, task);
    }
}
