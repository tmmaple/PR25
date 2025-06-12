package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Logger;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.stages.StageTest;
import ua.tmmaple.pr25.util.Tweener;

public final class GameplayManager {
    private static final short DEATHBOMB_COOLDOWN = (short) 10;

    public static final int VIEWPORT_START_X = 48;
    public static final int VIEWPORT_START_Y = 0;
    public static final int VIEWPORT_WIDTH = 384;
    public static final int VIEWPORT_HEIGHT = 480;

    public static GameplayManager global;

    private static Flow.FlowNode<GameplayManager> updateNode;
    private static Flow.FlowNode<GameplayManager> drawNode;

    private short deathbombCooldown;

    private boolean loading;

    public static int register() {
        updateNode = new Flow.FlowNode<>(global, GameplayManager::update, GameplayManager::added, GameplayManager::removed);
        drawNode = new Flow.FlowNode<>(global, GameplayManager::draw);
        Flow.global.addToUpdate(updateNode, 5);
        Flow.global.addToDraw(drawNode, 5);
        return 0;
    }

    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }

    public GameplayManager() {
        Background.global = new Background();
        BombManager.global = new BombManager();
        Player.global = new Player();
        EnemyManager.global = new EnemyManager();
        DropManager.global = new DropManager();
        StageManager.global = new StageManager();
        GameplayStats.global = new GameplayStats();
        VfxManager.global = new VfxManager();
    }

    public boolean canUpdate() {
        return deathbombCooldown == 0;
    }

    private int update() {
        if (loading && Assets.global.isLoaded()) {
            loading = false;
            GameplayStats.register();
            Background.register();
            BombManager.register();
            Player.register();
            BulletManager.register();
            EnemyManager.register();
            DropManager.register();
            VfxManager.register();
            StageManager.register();
            StageManager.global.load(new StageTest());
        }
        if (deathbombCooldown > 0) {
            --deathbombCooldown;
            if (deathbombCooldown == 0) {
                gameOver();
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int draw() {
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
