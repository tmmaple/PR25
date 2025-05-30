package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;

public final class Logger {
    public static void info(String msg) {
        Gdx.app.log("INFO", msg);
    }

    public static void info(Class<?> cl, String msg) {
        Gdx.app.log("INFO " + cl.getSimpleName(), msg);
    }

    public static void info(String tag, String msg) {
        Gdx.app.log("INFO " + tag, msg);
    }

    public static void error(String msg) {
        Gdx.app.error("ERROR", msg);
    }

    public static void error(Class<?> cl, String msg) {
        Gdx.app.error("ERROR " + cl.getSimpleName(), msg);
    }

    public static void error(String tag, String msg) {
        Gdx.app.error("ERROR " + tag, msg);
    }

    public static void debug(String msg) {
        Gdx.app.debug("DEBUG", msg);
    }

    public static void debug(Class<?> cl, String msg) {
        Gdx.app.debug("DEBUG " + cl.getSimpleName(), msg);
    }

    public static void debug(String tag, String msg) {
        Gdx.app.debug("DEBUG " + tag, msg);
    }
}
