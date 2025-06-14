package ua.tmmaple.pr25.assets;

import com.badlogic.gdx.math.RandomXS128;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.entities.Background;
import ua.tmmaple.pr25.entities.StageManager;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.task.TimelineTask;

/**
 * Базовий клас для стадії (тобто, рівня) у грі.
 * @author uwuhasmile
 */
public abstract class Stage {
    /** Значення змінюється зі сталою швидкістю **/
    protected static final byte INTERPOLATION_LINEAR = 0;
    /** Значення змінюється спочатку повільно, а потім швидко **/
    protected static final byte INTERPOLATION_EASE_IN = 1;
    /** Значення змінюється спочатку швидко, а потім повільно **/
    protected static final byte INTERPOLATION_EASE_OUT = 2;
    /** Значення змінюється спочатку повільно, потім швидко, потім знову повільно **/
    protected static final byte INTERPOLATION_EASE_IN_OUT = 3;

    private StageManager owner;
    protected Background background;
    protected RandomXS128 random;

    public Stage() {
        random = new RandomXS128();
    }

    /**
     * @return список ANM-ресурсів стадії для завантаження.
     * @author uwuhasmile
     */
    public abstract String[] anmList();

    /**
     * @return список BGM-ресурсів (фонової музики) для завантаження.
     * @author uwuhasmile
     */
    public abstract String[] bgmList();

    // TODO: видалити
    public abstract void reset();

    /**
     * @return Головний таймлайн подій під час стадії, такі як спавн ворогів, рух ворогів, спавн куль, і т.д.
     * @author uwuhasmile
     */
    public abstract TimelineTask main();

    /**
     * Ініціалізує стадію після завантаження.
     * @param owner StageManager, що контролює цю стадію
     * @param background посилання на фон для легкого контролю
     * @author uwuhasmile
     */
    public void init(StageManager owner, Background background) {
        this.owner = owner;
        this.background = background;
    }

    /**
     * @param idx індекс ANM-ресурсу
     * @return завантажений стадією ANM-ресурс
     * @author uwuhasmile
     */
    protected Anm getAnm(int idx) {
        return owner.anms[idx];
    }

    /**
     * Програє музику на стадії.
     * @param idx індекс завантаженого BGM-ресурсу
     * @author uwuhasmile
     */
    protected void playMusic(int idx) {
        Audio.global.playMusic(owner.bgms[idx], true);
    }

    /**
     * Плавно закінчує музику з затуханням.
     * @param t час затухання в секундах
     * @author uwuhasmile
     */
    protected void fadeMusic(float t) {
        Audio.global.fadeMusic(t);
    }

    /**
     * Зупиняє музику.
     * @author uwuhasmile
     */
    protected void stopMusic() {
        Audio.global.stopMusic();
    }

    /**
     * Завантажує та переходить до наступної стадії.
     * @param stage об'єкт стадії
     */
    protected void nextStage(Stage stage) {
        owner.load(stage);
    }
}
