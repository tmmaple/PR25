package ua.tmmaple.pr25.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Logger;
import ua.tmmaple.pr25.assets.loaders.AnmLoader;
import ua.tmmaple.pr25.assets.loaders.BgmLoader;
import ua.tmmaple.pr25.audio.Bgm;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class Assets {
    public static Assets global;

    private AssetManager manager;
    private boolean loaded;

    public Assets() {
        manager = null;
        loaded = false;
    }

    public static int register() {
        Flow.FlowNode<Assets> node = new Flow.FlowNode<>(global, Assets::update, Assets::added, Assets::removed);
        return Flow.global.addToUpdate(node, 999);
    }

    public <T> T get(Class<T> type, String filename) {
        if (!manager.isLoaded(filename)) throw new PR25RuntimeException("Asset " + filename + " is not loaded");
        return manager.get(filename, type);
    }

    public <T> void load(Class<T> type, String filename) {
        manager.load(filename, type);
    }

    public <T> void load(Class<T> type, String filename, AssetLoaderParameters<T> params) {
        manager.load(filename, type, params);
    }

    public void unload(String filename) {
        manager.unload(filename);
    }

    public void unload() {
        for (String a : manager.getAssetNames())
            manager.unload(a);
    }

    public void flush() {
        manager.clear();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isLoaded(String filename) {
        return manager.isLoaded(filename);
    }

    private static int added(Assets assets) {
        assets.manager = new AssetManager();
        assets.manager.setLoader(Anm.class, new AnmLoader(new InternalFileHandleResolver()));
        assets.manager.setLoader(Bgm.class, new BgmLoader(new InternalFileHandleResolver()));
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
        Assets.global = null;
        return 0;
    }
}
