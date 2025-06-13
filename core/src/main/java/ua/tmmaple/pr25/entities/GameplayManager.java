package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Logger;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.stages.Stage01;

public final class GameplayManager {
    private static final short DEATHBOMB_COOLDOWN = (short) 10;

    public static final int VIEWPORT_START_X = 48;
    public static final int VIEWPORT_START_Y = 0;
    public static final int VIEWPORT_WIDTH = 384;
    public static final int VIEWPORT_HEIGHT = 480;

    public static GameplayManager global;

    private static Flow.FlowNode<GameplayManager> node;

    private int coins;

    private int gameState;

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
        Hud.global = new Hud();
    }

    public int getCoins() {
        return coins;
    }

    public boolean canUpdate() {
        return gameState == 0;
    }

    public int getGameState() {
        return gameState;
    }

    public void pause() {
        gameState = 1;
    }

    public void resume() {
        if (gameState == 2) {
            Player.global.respawn();
            GameplayStats.global.respawn();
        }
        gameState = 0;
    }

    private int update() {
        if (loading && Assets.global.isLoaded()) {
            loading = false;
            StageManager.register();
            StageManager.global.load(new Stage01());
            GameplayStats.register();
            Player.register();
            EnemyManager.register();
            BulletManager.register();
            DropManager.register();
            BombManager.register();
            Background.register();
            VfxManager.register();
            Hud.register();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    public void gameOver() {
        if (--coins > 0)
            gameState = 2;
    }

    private int added() {
        Player.load();
        BulletManager.load();
        DropManager.load();
        Hud.load();
        coins = 4;
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
        DropManager.shutdown();
        Hud.shutdown();
        return 0;
    }
}
