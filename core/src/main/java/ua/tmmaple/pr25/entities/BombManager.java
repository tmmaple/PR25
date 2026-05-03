package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Керує бомбою гравця.
 * @author afiliushkin
 */
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

    private short left;
    private short nextClearLeft;

    /**
     * Завантажує ресурси.
     * @author afiliushkin
     */
    public static void load() {
        Assets.global.load(Anm.class, "game/bomb.anm");
    }

    /**
     * Реєструє в списки оновлення та відмалювання.
     * @author afiliushkin
     */
    public static void register() {
        if (updateNode != null)
            return;
        updateNode = new Flow.FlowNode<>(global, BombManager::update, BombManager::added, BombManager::removed);
        drawNode = new Flow.FlowNode<>(global, BombManager::draw);
        Flow.global.addToUpdate(updateNode, 6);
        Flow.global.addToDraw(drawNode, 8);
    }

    /**
     * Видаляє зі списків оновлення та відмалювання.
     * @author afiliushkin
     */
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

    /**
     * Ініціалізує віртуальні машини ANM для відображення фону бомби та портрету.
     * @author afiliushkin
     */
    private int added() {
        Anm anm = Assets.global.get(Anm.class, "game/bomb.anm");
        backgroundVm.loadAnm(anm);
        backgroundVm.loadScriptAndPlay("Background");
        backgroundVm.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y);
        portraitVm.loadAnm(anm);
        portraitVm.loadScriptAndPlay("Portrait");
        portraitVm.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y);
        return 0;
    }

    /**
     * Запускає бомбу та робить гравця безсмертним на певний час.
     * Не залежить від поточної статистики.
     * @author afiliushkin
     */
    public void use() {
        Audio.global.playSound("bomb.ogg", 1.4f);
        backgroundVm.interrupt((byte) 1);
        portraitVm.interrupt((byte) 1);
        left = DURATION;
        nextClearLeft = CLEAR_INTERVAL;
        Player.global.makeInvincible(INVINCIBILITY_DURATION);
    }

    /**
     * @return чи використовується бомба в цей момент часу
     * @author afiliushkin
     */
    public boolean isInUse() {
        return left > 0;
    }

    /**
     * Закінчує використання бомби.
     * @author afiliushkin
     */
    public void end() {
        left = (short) 0;
        nextClearLeft = (short) 0;
        backgroundVm.interrupt((byte) 2);
    }

    /**
     * Оновлює стан та таймери бомби, а також віртуальні машини.
     * @author afiliushkin
     */
    private int update() {
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

    /**
     * Відмальовує віртуальні машини бомби на екран.
     * @author afiliushkin
     */
    private int draw() {
        backgroundVm.draw();
        portraitVm.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Видаляє віртуальні машини та вивантажує ресурси.
     * @author afiliushkin
     */
    private int removed() {
        backgroundVm.delete();
        portraitVm.delete();
        Assets.global.unload("game/bomb.anm");
        return 0;
    }
}
