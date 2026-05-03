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
 * @author afiliushkin
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
     * @author afiliushkin
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
     * @author afiliushkin
     */
    public <T> void load(Class<T> type, String filename) {
        loaded = false;
        manager.load(filename, type);
    }

    /**
     * Додає ресурс до черги завантажень, якщо його ще не завантажено.
     * @param params Параметри для завантаження ресурсу.
     * @author afiliushkin
     */
    public <T> void load(Class<T> type, String filename, AssetLoaderParameters<T> params) {
        loaded = false;
        manager.load(filename, type, params);
    }

    /**
     * Вивантажує ресурс, якщо ним ніхто не користується.
     * @author afiliushkin
     */
    public void unload(String filename) {
        if (manager.isLoaded(filename)) {
            manager.unload(filename);
            System.gc();
        }
    }

    /**
     * Вивантажує всі ресурси, якщо вони не використовуються.
     * @author afiliushkin
     */
    public void unload() {
        for (String a : manager.getAssetNames())
            manager.unload(a);
        System.gc();
    }

    /**
     * Повністю очищує всі ресурси.
     * @author afiliushkin
     */
    public void flush() {
        manager.clear();
        System.gc();
    }

    /**
     * @return чи не завантажуються ніякі ресурси
     * @author afiliushkin
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @return чи завантажений ресурс filename
     * @author afiliushkin
     */
    public boolean isLoaded(String filename) {
        return manager.isLoaded(filename);
    }

    /**
     * Ініціалізує екземпляр Assets після додавання до списку оновлення.
     * @author afiliushkin
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
     * @author afiliushkin
     */
    private static int update(Assets instance) {
        instance.loaded = instance.manager.update(17);
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Очищається від всіх ресурсів після видалення зі списку оновлення.
     * @author afiliushkin
     */
    private static int removed(Assets instance) {
        instance.manager.dispose();
        System.gc();
        return 0;
    }
}
