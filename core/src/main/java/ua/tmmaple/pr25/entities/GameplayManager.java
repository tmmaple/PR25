package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.util.Tweener;

public final class GameplayManager {
    public static final int VIEWPORT_START_X = 48;
    public static final int VIEWPORT_START_Y = 0;
    public static final int VIEWPORT_WIDTH = 384;
    public static final int VIEWPORT_HEIGHT = 480;

    public static GameplayManager global;

    private static Flow.FlowNode<GameplayManager> updateNode;
    private static Flow.FlowNode<GameplayManager> drawNode;

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
        Player.global = new Player();
    }

    private int update() {
        if (loading && Assets.global.isLoaded()) {
            loading = false;
            Player.register();
            BulletManager.register();
            new Enemy(300, 300);
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int draw() {
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int added() {
        Assets.global.load(Anm.class,"game/plr.anm");
        Assets.global.load(Anm.class,"stages/st01BG.anm");
        loading = true;
        return 0;
    }

    private int removed() {
        BulletManager.shutdown();
        Player.shutdown();
        return 0;
    }
}
