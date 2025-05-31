package ua.tmmaple.pr25.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.io.BufferedReader;
import java.io.IOException;

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

    public void pause() {
        backing.pause();
    }

    public void resume() {
        backing.play();
    }

    public void stop() {
        backing.stop();
        backing.setOnCompletionListener(null);
    }

    public void setVolume(float volume) {
        backing.setVolume(volume);
    }

    public boolean isPlaying() {
        return backing.isPlaying();
    }

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
