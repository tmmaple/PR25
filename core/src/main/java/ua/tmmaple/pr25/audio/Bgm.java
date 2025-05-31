package ua.tmmaple.pr25.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Музичний трек.
 * Обгортка над {@link com.badlogic.gdx.audio.Music}, але може повторюватись з певної позиції.
 * @author uwuhasmile
 */
public class Bgm implements Disposable {
    private float loopPoint;

    public OnCompletionListener completionListener;
    public boolean looping;

    private Music backing;
    private boolean ownBacking;

    public Bgm(Music backing, float loopPoint, boolean ownBacking) {
        this.backing = backing;
        this.loopPoint = loopPoint;
        this.ownBacking = ownBacking;
    }

    public Bgm(Music backing, float loopPoint) {
        this.backing = backing;
        this.loopPoint = loopPoint;
        ownBacking = false;
    }

    public Bgm(FileHandle file) {
        parse(file);
    }

    /**
     * Програє трек.
     * @param looping чи повторювати трек після програвання
     * @author uwuhasmile
     */
    public void play(boolean looping) {
        this.looping = looping;
        backing.play();
        backing.setOnCompletionListener(m -> {
            if (looping) {
                m.play();
                m.setPosition(loopPoint);
            } else {
                if (completionListener != null)
                    completionListener.onCompletion(this);
            }
        });
        backing.setLooping(false);
    }

    /**
     * Ставить програвання на паузу.
     * @author uwuhasmile
     */
    public void pause() {
        backing.pause();
    }

    /**
     * Продовжу програвання.
     * @author uwuhasmile
     */
    public void resume() {
        backing.play();
    }

    /**
     * Зупиняє програвання.
     * @author uwuhasmile
     */
    public void stop() {
        backing.stop();
        backing.setOnCompletionListener(null);
    }

    /**
     * Встановлює гучність.
     * @author uwuhasmile
     */
    public void setVolume(float volume) {
        backing.setVolume(volume);
    }

    /**
     * @return чи програється трек на даний момент
     * @author uwuhasmile
     */
    public boolean isPlaying() {
        return backing.isPlaying();
    }

    /**
     * Читає з файлу .bgm.
     * В файлі на першому рядку обов'язково має бути ім'я аудіофайлу, на другому позиція повторення в секундах.
     * @author uwuhasmile
     */
    private void parse(FileHandle file) {
        try (BufferedReader r = file.reader(256)) {
            String line = r.readLine();
            backing = Gdx.audio.newMusic(Gdx.files.internal(line));
            ownBacking = true;
            line = r.readLine();
            loopPoint = Float.parseFloat(line);
        } catch (IOException e) {
            throw new PR25RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        if (ownBacking)
            backing.dispose();
    }

    public interface OnCompletionListener {
        void onCompletion(Bgm music);
    }
}
