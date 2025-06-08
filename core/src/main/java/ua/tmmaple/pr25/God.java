package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.utils.I18NBundle;
import ua.tmmaple.pr25.entities.GameplayManager;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.util.Locale;

/**
 * Налаштування гри та її поточний стан.
 */
public final class God {
    private enum State {
        INIT,
        MAIN_MENU,
        GAME,
        RESULT_SCREEN,
        ENDING,
        CREDITS
    }

    public static God global;

    public static final int LANGUAGE_ENGLISH = 0;
    public static final int LANGUAGE_UKRAINIAN = 1;

    public static final byte INPUT_STATE_NONE = 0;
    public static final byte INPUT_STATE_PRESSED = 1;
    public static final byte INPUT_STATE_JUST_PRESSED = 2;
    public static final byte INPUT_STATE_RELEASED = 0;

    public static final int INPUT_MOVE_UP = 0;
    public static final int INPUT_MOVE_DOWN = 1;
    public static final int INPUT_MOVE_LEFT = 2;
    public static final int INPUT_MOVE_RIGHT = 3;
    public static final int INPUT_FIRE = 4;
    public static final int INPUT_BOMB = 5;
    public static final int INPUT_FOCUS = 6;
    public static final int INPUT_UI_UP = 7;
    public static final int INPUT_UI_DOWN = 8;
    public static final int INPUT_UI_LEFT = 9;
    public static final int INPUT_UI_RIGHT = 10;
    public static final int INPUT_UI_ACCEPT = 11;
    public static final int INPUT_UI_DISCARD = 12;

    private static final String LANGUAGE_BASE_HANDLE = "i18n/content";

    private static final Locale[] LANGUAGES = {
        new Locale("en", "US"),
        new Locale("uk", "UA"),
    };

    private State state;

    public final int[] controls = {
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,
        Input.Keys.Z, Input.Keys.X, Input.Keys.SHIFT_LEFT,
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,
        Input.Keys.ENTER, Input.Keys.ESCAPE
    };
    private final byte[] input = new byte[controls.length];

    private int windowScale;

    private float sfxVolume;
    private float musicVolume;

    private FileHandle i18nHandle;
    private I18NBundle i18n;
    private int language;

    private Preferences prefs;

    public God() {
        i18nHandle = Gdx.files.internal("i18n/content");
        windowScale = 0;
        sfxVolume = 1.0f;
        musicVolume = 1.0f;
        state = State.INIT;
    }

    /**
     * Реєструє global в список оновлення.
     * @author uwuhasmile
     */
    public static int register() {
        Flow.FlowNode<God> node = new Flow.FlowNode<>(global, God::update, God::added, God::removed);
        return Flow.global.addToUpdate(node, 998);
    }

    /**
     * Ініціалізує екземпляр God після реєстрації в список оновлень.
     * В ініціалізацію входить завантаження та застосування налаштувань.
     * @author uwuhasmile
     */
    private static int added(God god) {
        GameplayManager.global = new GameplayManager();
        GameplayManager.register();

        I18NBundle.setExceptionOnMissingKey(false);
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
        if (language.equals("en")) god.language = LANGUAGE_ENGLISH;
        else if (language.equals("uk")) god.language = LANGUAGE_UKRAINIAN;

        god.setWindowMode(god.windowScale);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        return 0;
    }

    /**
     * Оновлює екземпляр God.
     * @author uwuhasmile
     */
    private static int update(God god) {
        god.updateControls();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Оновлює стан клавіш керування.
     * @author uwuhasmile
     */
    private void updateControls() {
        for (int i = 0; i < controls.length; ++i) {
            boolean pressed = Gdx.input.isKeyPressed(controls[i]);
            if (pressed) {
                if (input[i] == INPUT_STATE_NONE || input[i] == INPUT_STATE_RELEASED)
                    input[i] = INPUT_STATE_JUST_PRESSED;
                else if (input[i] == INPUT_STATE_JUST_PRESSED)
                    input[i] = INPUT_STATE_PRESSED;
            } else {
                if (input[i] == INPUT_STATE_PRESSED || input[i] == INPUT_STATE_JUST_PRESSED)
                    input[i] = INPUT_STATE_RELEASED;
                else if (input[i] == INPUT_STATE_RELEASED)
                    input[i] = INPUT_STATE_NONE;
            }
        }
    }

    /**
     * Зберігає параметри після видалення зі списку оновлення.
     * @author uwuhasmile
     */
    private static int removed(God god) {
        GameplayManager.shutdown();

        god.prefs.putString("language", god.language == LANGUAGE_ENGLISH ? "en" : "uk");
        god.prefs.putInteger("keyMoveUp", god.controls[0]);
        god.prefs.putInteger("keyMoveDown", god.controls[1]);
        god.prefs.putInteger("keyMoveLeft", god.controls[2]);
        god.prefs.putInteger("keyMoveRight", god.controls[3]);
        god.prefs.putInteger("keyFire", god.controls[4]);
        god.prefs.putInteger("keyBomb", god.controls[5]);
        god.prefs.putInteger("keyFocus", god.controls[6]);
        god.prefs.putInteger("windowScale", god.windowScale);
        god.prefs.putFloat("sfxVolume", god.sfxVolume);
        god.prefs.putFloat("musicVolume", god.musicVolume);
        god.prefs.flush();
        return 0;
    }

    /**
     * Встановлює режим вікна.
     * Якщо scale дорівнює 0, то гра переходить в повноекранний режим.
     * @param scale розмір вікна в межах [0, 6]
     * @author uwuhasmile
     */
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

    /**
     * Встановлює мову.
     * @param language 0 - англійська, 1 - українська
     * @author uwuhasmile
     */
    public void setLanguage(int language) {
        if (language < 0 || language >= LANGUAGES.length)
            language = LANGUAGE_ENGLISH;
        this.language = language;
        i18n = I18NBundle.createBundle(i18nHandle, LANGUAGES[language]);
    }

    /**
     * @return стан дії вводу, вказаної в control
     * @author uwuhasmile
     */
    public byte inputState(int control) {
        if (control < 0 || control >= controls.length) throw new PR25RuntimeException("Control " + control + " doesn't exist");
        return input[control];
    }

    /**
     * @param volume нова гучність звукових ефектів
     * @author uwuhasmile
     */
    public void setSfxVolume(float volume) {
        sfxVolume = volume < 0.0f ? 0.0f : Math.min(volume, 1.0f);
    }

    /**
     * @param volume нова гучність музики
     * @author uwuhasmile
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume < 0.0f ? 0.0f : Math.min(volume, 1.0f);
    }

    /**
     * @return поточний розмір вікна, 0 - повноекранний режим
     * @author uwuhasmile
     */
    public int windowScale() {
        return windowScale;
    }

    /**
     * @return поточна гучність звукових ефектів
     * @author uwuhasmile
     */
    public float sfxVolume() {
        return sfxVolume;
    }

    /**
     * @return поточна гучність музики
     * @author uwuhasmile
     */
    public float musicVolume() {
        return musicVolume;
    }

    /**
     * @return поточна мова
     * @author uwuhasmile
     */
    public int language() {
        return language;
    }
}
