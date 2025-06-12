package ua.tmmaple.pr25.assets;

import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.entities.Background;
import ua.tmmaple.pr25.entities.StageManager;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.task.TimelineTask;

public abstract class Stage {
    protected static final byte INTERPOLATION_LINEAR = 0;
    protected static final byte INTERPOLATION_EASE_IN = 1;
    protected static final byte INTERPOLATION_EASE_OUT = 2;
    protected static final byte INTERPOLATION_EASE_IN_OUT = 3;

    private StageManager owner;
    protected Background background;

    public abstract String[] anmList();
    public abstract String[] bgmList();

    public abstract void reset();

    public abstract TimelineTask main();

    public void init(StageManager owner, Background background) {
        this.owner = owner;
        this.background = background;
    }

    protected Anm getAnm(int idx) {
        return owner.anms[idx];
    }

    protected void playMusic(int idx) {
        Audio.global.playMusic(owner.bgms[idx], true);
    }

    protected void fadeMusic(float t) {
        Audio.global.fadeMusic(t);
    }

    protected void stopMusic() {
        Audio.global.stopMusic();
    }

    protected void nextStage(Stage stage) {
        owner.load(stage);
    }
}
