package ua.tmmaple.pr25.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Logger;
import ua.tmmaple.pr25.assets.loaders.AnmLoader;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class Assets {
    private static Assets instance;

    private AssetManager manager;
    private boolean loaded;

    public Assets() {
        manager = null;
        loaded = false;
    }

    public static void register(Assets instance) {
        if (Assets.instance != null) throw new PR25RuntimeException("Asset manager is already initialized");
        Assets.instance = instance;

        Flow.FlowNode<Assets> node = new Flow.FlowNode<>(instance, Assets::update, Assets::added, Assets::removed);
        Flow.addToUpdate(node, 999);
    }

    public static <T> T get(Class<T> type, String filename) {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        if (!instance.manager.isLoaded(filename)) throw new PR25RuntimeException("Asset " + filename + " is not loaded");
        return instance.manager.get(filename, type);
    }

    public static <T> void load(Class<T> type, String filename) {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        instance.manager.load(filename, type);
    }

    public static <T> void load(Class<T> type, String filename, AssetLoaderParameters<T> params) {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        instance.manager.load(filename, type, params);
    }

    public static void unload(String filename) {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        instance.manager.unload(filename);
    }

    public static void unload() {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        for (String a : instance.manager.getAssetNames())
            instance.manager.unload(a);
    }

    public static void flush() {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        instance.manager.clear();
    }

    public static boolean isLoaded() {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        return instance.loaded;
    }

    public static boolean isLoaded(String filename) {
        if (instance == null) throw new PR25RuntimeException("Asset manager is not initialized");

        return instance.manager.isLoaded(filename);
    }

    private static int added(Assets instance) {
        instance.manager = new AssetManager();
        instance.manager.setLoader(Anm.class, new AnmLoader(new InternalFileHandleResolver()));
        return 0;
    }

    private static int update(Assets instance) {
        instance.loaded = instance.manager.update(17);
        if (!instance.loaded) {
            Logger.info("Loading assets: " + (int)(instance.manager.getProgress() * 100) + "%");
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private static int removed(Assets instance) {
        instance.manager.dispose();
        Assets.instance = null;
        return 0;
    }
}
