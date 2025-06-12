package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

public final class BombManager {
    private static final short DURATION = 420;
    private static final short INVINCIBILITY_DURATION = 460;
    private static final short CLEAR_INTERVAL = 20;
    private static final float CLEAR_RADIUS = 192.0f;

    public static BombManager global;

    private static Flow.FlowNode<BombManager> updateNode;
    private static Flow.FlowNode<BombManager> drawNode;

    private final GraphicManager.AnmVirtualMachine backgroundVm;
    private final GraphicManager.AnmVirtualMachine portraitVm;
    private boolean loading;

    private short left;
    private short nextClearLeft;

    public static void register() {
        if (updateNode != null)
            return;
        updateNode = new Flow.FlowNode<>(global, BombManager::update, BombManager::added, BombManager::removed);
        drawNode = new Flow.FlowNode<>(global, BombManager::draw);
        Flow.global.addToUpdate(updateNode, 6);
        Flow.global.addToDraw(drawNode, 8);
    }

    public static void shutdown() {
        if (updateNode == null)
            return;
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    public BombManager() {
        backgroundVm = GraphicManager.global.new AnmVirtualMachine();
        portraitVm = GraphicManager.global.new AnmVirtualMachine();
    }

    private int added() {
        loading = true;
        Assets.global.load(Anm.class, "game/bomb.anm");
        return 0;
    }

    public void use() {
        Audio.global.playSound("bomb.ogg", 1.4f);
        backgroundVm.interrupt((byte) 1);
        portraitVm.interrupt((byte) 1);
        left = DURATION;
        nextClearLeft = CLEAR_INTERVAL;
        Player.global.makeInvincible(INVINCIBILITY_DURATION);
    }

    public boolean isInUse() {
        return left > 0;
    }

    public void end() {
        left = (short) 0;
        nextClearLeft = (short) 0;
        backgroundVm.interrupt((byte) 2);
    }

    private int update() {
        if (loading && Assets.global.isLoaded("game/bomb.anm")) {
            Anm anm = Assets.global.get(Anm.class, "game/bomb.anm");
            loading = false;
            backgroundVm.loadAnm(anm);
            backgroundVm.loadScriptAndPlay("Background");
            backgroundVm.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y);
            portraitVm.loadAnm(anm);
            portraitVm.loadScriptAndPlay("Portrait");
            portraitVm.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y);
        }
        if (!GameplayManager.global.canUpdate())
            return Flow.FLOW_RESULT_CONTINUE;
        if (left > 0) {
            --left;
            if (nextClearLeft > 0)
                --nextClearLeft;
            if (nextClearLeft == 0) {
                nextClearLeft = CLEAR_INTERVAL;
                BulletManager.global.destroyEnemyBulletsInRadius(Player.global.position, CLEAR_RADIUS);
                if (EnemyManager.global.damageAllInRadius(Player.global.position, CLEAR_RADIUS, 100)) {
                    Audio.global.playSound("bombClear.ogg", 1.0f);
                    Background.global.shakeCamera(12);
                }
            }
            if (left == 0)
                end();
        }
        backgroundVm.execute();
        portraitVm.execute();

        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int draw() {
        backgroundVm.draw();
        portraitVm.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int removed() {
        return 0;
    }
}
