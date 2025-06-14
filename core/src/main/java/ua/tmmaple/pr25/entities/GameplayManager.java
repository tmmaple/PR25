package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.graphics.Color;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Головний клас, що керує грою в стані активного ігрового процесу.
 * @author uwuhasmile
 */
public final class GameplayManager {
    private static final short DEATHBOMB_COOLDOWN = (short) 10;

    public static final int VIEWPORT_START_X = 48;
    public static final int VIEWPORT_START_Y = 0;
    public static final int VIEWPORT_WIDTH = 384;
    public static final int VIEWPORT_HEIGHT = 480;

    public static GameplayManager global;

    private static Flow.FlowNode<GameplayManager> node;

    private static Stage toLoad;

    private int coins;

    private int gameState;

    /**
     * Завантажує ресурси гравця, куль, дропів, візуальних ефектів, та HUD.
     * @author uwuhasmile
     */
    public static void load(Stage stage) {
        toLoad = stage;
        Player.load();
        BulletManager.load();
        DropManager.load();
        VfxManager.load();
        Hud.load();
    }

    /**
     * Реєструє в списку оновлень.
     * @author uwuhasmile
     */
    public static int register() {
        node = new Flow.FlowNode<>(global, null, GameplayManager::added, GameplayManager::removed);
        Flow.global.addToUpdate(node, 3);
        return 0;
    }

    /**
     * Видаляє зі списку оновлень.
     * @author uwuhasmile
     */
    public static void shutdown() {
        Flow.global.cut(node);
    }

    public GameplayManager() {
        StageManager.global = new StageManager();
        GameplayStats.global = new GameplayStats();
        Player.global = new Player();
        BulletManager.global = new BulletManager();
        EnemyManager.global = new EnemyManager();
        DropManager.global = new DropManager();
        BombManager.global = new BombManager();
        Background.global = new Background();
        VfxManager.global = new VfxManager();
        Hud.global = new Hud();
    }

    /**
     * @return кількість монет для продовження гри, що лишились в цій сесії
     */
    public int getCoins() {
        return coins;
    }

    /**
     * @return чи можуть ігрові елементи оновлюватись (наприклад, коли гра не стоїть в паузі).
     */
    public boolean canUpdate() {
        return gameState == 0;
    }

    /**
     * @return 0, якщо гра триває, 1, якщо гра в меню паузи, 2, якщо гра в меню монети
     * @author uwuhasmile
     */
    public int getPauseState() {
        return gameState;
    }

    /**
     * Ставить гру на паузу
     * @author uwuhasmile
     */
    public void pause() {
        gameState = 1;
    }

    /**
     * Продовжує гру після паузи. Якщо це було меню монет, то респавнить гравця та скидає статистику.
     * @author uwuhasmile
     */
    public void resume() {
        if (gameState == 2) {
            GameplayStats.global.coinUsed();
            Player.global.respawn();
            GameplayStats.global.respawn();
        }
        gameState = 0;
    }

    /**
     * Якщо ще є монети, то показує відповідне меню. В іншому випадку викидує з гри на екран показу результатів.
     * @author uwuhasmile
     */
    public void gameOver() {
        if (--coins > 0)
            gameState = 2;
    }

    /**
     * Ініціалізація після додавання до списку оновлення.
     * @author uwuhasmile
     */
    private int added() {
        StageManager.register();
        StageManager.global.load(toLoad);
        toLoad = null;
        GameplayStats.register();
        Player.register();
        EnemyManager.register();
        BulletManager.register();
        DropManager.register();
        BombManager.register();
        Background.register();
        VfxManager.register();
        Hud.register();
        gameState = 0;
        coins = 4;
        GraphicManager.global.backgroundColor.set(Color.BLACK);
        return 0;
    }

    /**
     * Очищення після видалення зі списку оновлення.
     * @author uwuhasmile
     */
    private int removed() {
        GameplayStats.shutdown();
        BombManager.shutdown();
        StageManager.shutdown();
        Background.shutdown();
        BulletManager.shutdown();
        Player.shutdown();
        EnemyManager.shutdown();
        VfxManager.shutdown();
        DropManager.shutdown();
        Hud.shutdown();
        return 0;
    }
}
