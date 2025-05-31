package ua.tmmaple.pr25.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.util.HashMap;

public final class Audio {
    public static Audio global;

    private final HashMap<String, Sound> sounds;
    private Bgm bgm;

    public Audio() {
        sounds = new HashMap<>();
    }

    public void initialize() {
        FileHandle root = Gdx.files.internal("sounds");
        if (!root.exists() || !root.isDirectory()) return;
        for (FileHandle fh : root.list()) {
            if (fh.isDirectory()) continue;
            if (!fh.extension().equalsIgnoreCase("wav") && !fh.extension().equalsIgnoreCase("ogg"))
                continue;
            Sound sound = Gdx.audio.newSound(fh);
            sounds.put(fh.name(), sound);
        }
    }

    public void playSound(String name, float volume) {
        if (!sounds.containsKey(name)) throw new PR25RuntimeException("No sound " + name);
        Sound sound = sounds.get(name);
        sound.play(God.global.sfxVolume() * volume);
    }

    public void playMusic(String name, boolean loop) {
        if (bgm != null) stopMusic();
        bgm = Assets.global.get(Bgm.class, name);
        bgm.setVolume(God.global.musicVolume());
        bgm.completionListener = m -> { bgm.completionListener = null; bgm = null; };
        bgm.play(loop);
    }

    public void pauseMusic() {
        bgm.pause();
    }

    public void update() {
        if (bgm != null)
            bgm.setVolume(God.global.musicVolume());
    }

    public void stopMusic() {
        bgm.stop();
        bgm.completionListener = null;
    }

    public void shutdown() {
        for (Sound sound : sounds.values())
            sound.dispose();
        sounds.clear();
    }
}
