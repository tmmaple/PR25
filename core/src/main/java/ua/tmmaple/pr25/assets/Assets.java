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

/**
 * Керує завантаженням та вивантаженням ресурсів.
 * @author uwuhasmile
 */
public final class Assets {
    public static Assets global;

    private AssetManager manager;
    private boolean loaded;

    public Assets() {
        manager = null;
        loaded = false;
    }

    /**
     * Реєструє global в список оновлення.
     * @author uwuhasmile
     */
    public static int register() {
        Flow.FlowNode<Assets> node = new Flow.FlowNode<>(global, Assets::update, Assets::added, Assets::removed);
        return Flow.global.addToUpdate(node, 1);
    }

    /**
     * @return ресурс типу T за іменем
     * @throws PR25RuntimeException якщо ресурс ще не був завантажений
     */
    public <T> T get(Class<T> type, String filename) {
        if (!manager.isLoaded(filename)) throw new PR25RuntimeException("Asset " + filename + " is not loaded");
        return manager.get(filename, type);
    }

    /**
     * Додає ресурс до черги завантажень, якщо його ще не завантажено.
     * @author uwuhasmile
     */
    public <T> void load(Class<T> type, String filename) {
        loaded = false;
        manager.load(filename, type);
    }

    /**
     * Додає ресурс до черги завантажень, якщо його ще не завантажено.
     * @param params Параметри для завантаження ресурсу.
     * @author uwuhasmile
     */
    public <T> void load(Class<T> type, String filename, AssetLoaderParameters<T> params) {
        loaded = false;
        manager.load(filename, type, params);
    }

    /**
     * Вивантажує ресурс, якщо ним ніхто не користується.
     * @author uwuhasmile
     */
    public void unload(String filename) {
        if (manager.isLoaded(filename))
            manager.unload(filename);
    }

    /**
     * Вивантажує всі ресурси, якщо вони не використовуються.
     * @author uwuhasmile
     */
    public void unload() {
        for (String a : manager.getAssetNames())
            manager.unload(a);
    }

    /**
     * Повністю очищує всі ресурси.
     * @author uwuhasmile
     */
    public void flush() {
        manager.clear();
    }

    /**
     * @return чи не завантажуються ніякі ресурси
     * @author uwuhasmile
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @return чи завантажений ресурс filename
     * @author uwuhasmile
     */
    public boolean isLoaded(String filename) {
        return manager.isLoaded(filename);
    }

    /**
     * Ініціалізує екземпляр Assets після додавання до списку оновлення.
     * @author uwuhasmile
     */
    private static int added(Assets assets) {
        assets.manager = new AssetManager();
        assets.manager.setLoader(Anm.class, new AnmLoader(new InternalFileHandleResolver()));
        assets.manager.setLoader(Bgm.class, new BgmLoader(new InternalFileHandleResolver()));
        return 0;
    }

    /**
     * Оновлює екземпляр Assets.
     * Під час одного оновлення намагається завантажити якомога більше ресурсів.
     * @author uwuhasmile
     */
    private static int update(Assets instance) {
        instance.loaded = instance.manager.update(17);
        if (!instance.loaded) {
            Logger.info("Loading assets: " + (int)(instance.manager.getProgress() * 100) + "%");
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Очищається від всіх ресурсів після видалення зі списку оновлення.
     * @author uwuhasmile
     */
    private static int removed(Assets instance) {
        instance.manager.dispose();
        return 0;
    }
}
