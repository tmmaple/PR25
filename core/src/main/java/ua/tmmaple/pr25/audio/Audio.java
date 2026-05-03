package ua.tmmaple.pr25.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.util.HashMap;

/**
 * Керує програванням звуків та музики.
 * @author afiliushkin
 */
public final class Audio {
    public static Audio global;

    private float fade;
    private float fadeStep;

    private final HashMap<String, Sound> sounds;
    private Bgm bgm;

    public Audio() {
        sounds = new HashMap<>();
    }

    /**
     * Ініціалізує менеджер та завантажує всі звуки в теці assets/sounds.
     * @author afiliushkin
     */
    public void initialize() {
        String[] assets = Gdx.files.internal("assets.txt").readString().split("\n");
        for (String f : assets) {
            FileHandle fh =  Gdx.files.internal(f);
            if (fh.isDirectory()) continue;
            if (!fh.extension().equalsIgnoreCase("wav") && !fh.extension().equalsIgnoreCase("ogg"))
                continue;
            Sound sound = Gdx.audio.newSound(fh);
            sounds.put(fh.name(), sound);
        }
    }

    /**
     * Програє звук з певною гучністю.
     * @throws PR25RuntimeException якщо звуку з таким ім'ям не існує.
     * @author afiliushkin
     */
    public void playSound(String name, float volume) {
        if (!sounds.containsKey(name)) throw new PR25RuntimeException("No sound " + name);
        Sound sound = sounds.get(name);
        sound.play(God.global.sfxVolume() * volume);
    }

    /**
     * Програє музику з певною гучністю.
     * Трек має бути завантажений через {@link ua.tmmaple.pr25.assets.Assets} заздалегідь.
     * @author afiliushkin
     */
    public void playMusic(String name, boolean loop) {
        if (bgm != null) stopMusic();
        bgm = Assets.global.get(Bgm.class, name);
        bgm.setVolume(God.global.musicVolume());
        bgm.completionListener = m -> { bgm.completionListener = null; bgm = null; };
        bgm.play(loop);
        fade = 1.0f;
        fadeStep = 0.0f;
    }

    /**
     * Ставить трек на паузу.
     * @author afiliushkin
     */
    public void pauseMusic() {
        if (bgm != null && bgm.isPlaying())
            bgm.pause();
    }

    /**
     * Продовжує трек після паузи.
     * @author afiliushkin
     */
    public void resumeMusic() {
        if (bgm != null && !bgm.isPlaying())
            bgm.resume();
    }

    public void fadeMusic(float t) {
        if (bgm == null) return;
        fade = 1.0f;
        fadeStep = 1.0f / t;
    }

    /**
     * Оновлює гучність музики до актуальних параметрів, якщо вона грає.
     * @author afiliushkin
     */
    public void update(float delta) {
        if (bgm != null && bgm.isPlaying()) {
            bgm.setVolume(God.global.musicVolume() * fade);
            if (fadeStep > 0.0f) {
                fade -= delta * fadeStep;
                if (fade <= 0.0f)
                    stopMusic();
            }
        }
    }

    /**
     * Зупиняє музику.
     * @author afiliushkin
     */
    public void stopMusic() {
        if (bgm == null) return;
        bgm.stop();
        bgm.completionListener = null;
        fade = 0.0f;
        fadeStep = 0.0f;
        bgm = null;
    }

    /**
     * Очищує всі завантажені звуки з пам'яті.
     * @author afiliushkin
     */
    public void shutdown() {
        for (Sound sound : sounds.values())
            sound.dispose();
        sounds.clear();
    }
}
