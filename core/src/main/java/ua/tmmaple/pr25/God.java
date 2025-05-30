package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Cursor;
import ua.tmmaple.pr25.i18n.Language;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class God {
    public static God global;

    private Preferences prefs;

    private final int[] controls = {
        com.badlogic.gdx.Input.Keys.UP,
        com.badlogic.gdx.Input.Keys.DOWN,
        com.badlogic.gdx.Input.Keys.LEFT,
        com.badlogic.gdx.Input.Keys.RIGHT,
        com.badlogic.gdx.Input.Keys.Z,
        com.badlogic.gdx.Input.Keys.X,
        com.badlogic.gdx.Input.Keys.SHIFT_LEFT,
    };

    private int windowScale;
    private float sfxVolume;
    private float musicVolume;
    private int language;

    public God() {
        windowScale = 0;
        sfxVolume = 1.0f;
        musicVolume = 1.0f;
    }

    public static int register() {
        Flow.FlowNode<God> node = new Flow.FlowNode<>(global, God::update, God::added, God::removed);
        return Flow.global.addToUpdate(node, 998);
    }

    private static int added(God god) {
        god.prefs = Gdx.app.getPreferences("pr25_settings");
        String language = god.prefs.getString("language", "en");
        god.controls[0] = god.prefs.getInteger("keyMoveUp", god.controls[0]);
        god.controls[1] = god.prefs.getInteger("keyMoveDown", god.controls[1]);
        god.controls[2] = god.prefs.getInteger("keyMoveLeft", god.controls[2]);
        god.controls[3] = god.prefs.getInteger("keyMoveRight", god.controls[3]);
        god.controls[4] = god.prefs.getInteger("keyFire", god.controls[4]);
        god.controls[5] = god.prefs.getInteger("keyBomb", god.controls[5]);
        god.controls[6] = god.prefs.getInteger("keyFocus", god.controls[6]);
        god.windowScale = god.prefs.getInteger("windowScale", 2);
        god.sfxVolume = god.prefs.getFloat("sfxVolume", 1.0f);
        god.musicVolume = god.prefs.getFloat("musicVolume", 1.0f);
        if (language.equals("en")) god.language = Language.LANGUAGE_ENGLISH;
        else if (language.equals("uk")) god.language = Language.LANGUAGE_UKRAINIAN;
        god.setWindowMode(god.windowScale);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

        return 0;
    }

    private static int update(God instance) {
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private static int removed(God instance) {
        instance.prefs.putString("language", instance.language == Language.LANGUAGE_ENGLISH ? "en" : "uk");
        instance.prefs.putInteger("keyMoveUp", instance.controls[0]);
        instance.prefs.putInteger("keyMoveDown", instance.controls[1]);
        instance.prefs.putInteger("keyMoveLeft", instance.controls[2]);
        instance.prefs.putInteger("keyMoveRight", instance.controls[3]);
        instance.prefs.putInteger("keyFire", instance.controls[4]);
        instance.prefs.putInteger("keyBomb", instance.controls[5]);
        instance.prefs.putInteger("keyFocus", instance.controls[6]);
        instance.prefs.putInteger("windowScale", instance.windowScale);
        instance.prefs.putFloat("sfxVolume", instance.sfxVolume);
        instance.prefs.putFloat("musicVolume", instance.musicVolume);
        instance.prefs.flush();
        return 0;
    }

    public void setWindowMode(int scale) {
        scale = Math.min(scale, 6);
        windowScale = scale;
        if (scale > 0) {
            switch (scale) {
                case 1: Gdx.graphics.setWindowedMode(Game.BASE_WINDOW_WIDTH, Game.BASE_WINDOW_HEIGHT); break;
                case 2: Gdx.graphics.setWindowedMode(800, 600); break;
                case 3: Gdx.graphics.setWindowedMode(1024, 768); break;
                case 4: Gdx.graphics.setWindowedMode(1280, 960); break;
                case 5: Gdx.graphics.setWindowedMode(1440, 1080); break;
                case 6: Gdx.graphics.setWindowedMode(1600, 1200); break;
            }
        } else {
            Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(mode);
        }
    }

    public int[] controls() {
        return controls;
    }

    public int windowScale() {
        return windowScale;
    }

    public boolean isFullscreen() {
        return windowScale == 0;
    }

    public float sfxVolume() {
        return sfxVolume;
    }

    public float musicVolume() {
        return musicVolume;
    }

    public int language() {
        return language;
    }
}
