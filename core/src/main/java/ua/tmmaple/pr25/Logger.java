package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;

/**
 * Глобальний логгер для гри.
 * @author afiliushkin
 */
public final class Logger {
    /**
     * Виводить інформацію.
     * @author afiliushkin
     */
    public static void info(String msg) {
        Gdx.app.log("INFO", msg);
    }

    /**
     * Виводить інформацію з тегом класу.
     * @author afiliushkin
     */
    public static void info(Class<?> cl, String msg) {
        Gdx.app.log("INFO " + cl.getSimpleName(), msg);
    }

    /**
     * Виводить інформацію з власним тегом.
     * @author afiliushkin
     */
    public static void info(String tag, String msg) {
        Gdx.app.log("INFO " + tag, msg);
    }

    /**
     * Виводить помилку.
     * @author afiliushkin
     */
    public static void error(String msg) {
        Gdx.app.error("ERROR", msg);
    }

    /**
     * Виводить помилку з тегом класу.
     * @author afiliushkin
     */
    public static void error(Class<?> cl, String msg) {
        Gdx.app.error("ERROR " + cl.getSimpleName(), msg);
    }

    /**
     * Виводить помилку з власним тегом.
     * @author afiliushkin
     */
    public static void error(String tag, String msg) {
        Gdx.app.error("ERROR " + tag, msg);
    }

    /**
     * Виводить повідомлення для дебагу.
     * @author afiliushkin
     */
    public static void debug(String msg) {
        Gdx.app.debug("DEBUG", msg);
    }

    /**
     * Виводить повідомлення для дебагу з тегом класу.
     * @author afiliushkin
     */
    public static void debug(Class<?> cl, String msg) {
        Gdx.app.debug("DEBUG " + cl.getSimpleName(), msg);
    }

    /**
     * Виводить повідомлення для дебагу з власним тегом.
     * @author afiliushkin
     */
    public static void debug(String tag, String msg) {
        Gdx.app.debug("DEBUG " + tag, msg);
    }
}
