package ua.tmmaple.pr25.task;

import ua.tmmaple.pr25.entities.Enemy;

/**
 * Виконує дії, що розставлені за часом.
 * Проте виконання не буде повністю слідувати часу - якщо є дія, що повертає false за якихось умов, то продовження виконання буде відкладене.
 * @author uwuhasmile
 */
public final class TimelineTask implements Task {
    private final Keyframe[] keyframes;
    private int index;
    private short time;

    /**
     * @param keyframes набір ключів
     * @author uwuhasmile
     */
    public TimelineTask(Keyframe[] keyframes) {
        this.keyframes = keyframes;
        index = 0;
        time = -1;
    }

    @Override
    public boolean execute(Enemy enemy) {
        if (time == -1) {
            index = 0;
            time = 0;
        }
        boolean result = true;
        while (result && index < keyframes.length && time >= keyframes[index].timeMarker) {
            result = keyframes[index].task.execute(enemy);
            if (result)
                ++index;
        }
        if (index < keyframes.length) {
            ++time;
            return false;
        }
        time = -1;
        return true;
    }

    @Override
    public Task copy() {
        Keyframe[] keyframes = new Keyframe[this.keyframes.length];
        for (int i = 0; i < keyframes.length; ++i)
            keyframes[i] = new Keyframe(this.keyframes[i].timeMarker, this.keyframes[i].task.copy());
        return new TimelineTask(keyframes);
    }

    /**
     * Ключ на таймлайні, до якого прив'язана дія.
     * @author uwuhasmile
     */
    public static class Keyframe {
        private final short timeMarker;
        private final Task task;

        /**
         * @param timeMarker час, у тіках (один тік = 1/60 секунди)
         * @param task тіло ключа
         * @author uwuhasmile
         */
        public Keyframe(short timeMarker, Task task) {
            this.timeMarker = timeMarker;
            this.task = task;
        }

        /**
         * @param task тіло ключа
         * @author uwuhasmile
         */
        public Keyframe(Task task) {
            this.timeMarker = 0;
            this.task = task;
        }
    }
}
