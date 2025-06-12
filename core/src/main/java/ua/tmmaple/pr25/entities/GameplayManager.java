package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Logger;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.stages.StageTest;

public final class GameplayManager {
    private static final short DEATHBOMB_COOLDOWN = (short) 10;

    public static final int VIEWPORT_START_X = 48;
    public static final int VIEWPORT_START_Y = 0;
    public static final int VIEWPORT_WIDTH = 384;
    public static final int VIEWPORT_HEIGHT = 480;

    public static GameplayManager global;

    private static Flow.FlowNode<GameplayManager> node;

    private short deathbombCooldown;

    private boolean loading;

    public static int register() {
        node = new Flow.FlowNode<>(global, GameplayManager::update, GameplayManager::added, GameplayManager::removed);
        Flow.global.addToUpdate(node, 3);
        return 0;
    }

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
    }

    public boolean canUpdate() {
        return deathbombCooldown == 0;
    }

    private int update() {
        if (loading && Assets.global.isLoaded()) {
            loading = false;
            StageManager.register();
            StageManager.global.load(new StageTest());
            GameplayStats.register();
            Player.register();
            EnemyManager.register();
            BulletManager.register();
            DropManager.register();
            BombManager.register();
            Background.register();
            VfxManager.register();

        }
        if (deathbombCooldown > 0) {
            --deathbombCooldown;
            if (deathbombCooldown == 0) {
                gameOver();
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    public void gameOver() {
        Player.global.respawn();
        Logger.info("Game Over");
    }

    private int added() {
        Player.load();
        BulletManager.load();
        loading = true;
        return 0;
    }

    private int removed() {
        GameplayStats.shutdown();
        BombManager.shutdown();
        StageManager.shutdown();
        Background.shutdown();
        BulletManager.shutdown();
        Player.shutdown();
        EnemyManager.shutdown();
        VfxManager.shutdown();
        return 0;
    }
}
