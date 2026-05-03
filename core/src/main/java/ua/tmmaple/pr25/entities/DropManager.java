package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Керування дропами (предметами, що випадають з ворогів)
 * @author SkyWarp
 */
public class DropManager {
    private enum DropType {
        STAR,
        POWER,
    }

    private static final int POWER_DROP = 1;
    private static final int SCORE_DROP = 500;
    private static final float TERMINAL_SPEED = -5.0f;
    private static final float GRAVITY = -0.11f;

    public static DropManager global;

    private Drop[] drops;
    private int free;

    private static Flow.FlowNode<DropManager> updateNode;
    private static Flow.FlowNode<DropManager> drawNode;

    private short dropSoundCooldown;

    private Anm anm;

    public DropManager() {
        drops = new Drop[64];
        for (int i = 0; i < drops.length; ++i)
            drops[i] = new Drop(i);
    }

    /**
     * Завантажує ресурси
     * @author afiliushkin
     */
    public static void load() {
        Assets.global.load(Anm.class, "game/drops.anm");
    }

    /**
     * Реєструє в списку оновлень та відмалювання.
     * @author SkyWarp
     */
    public static void register() {
        updateNode = new Flow.FlowNode<>(global, DropManager::update, DropManager::added, DropManager::removed);
        drawNode = new Flow.FlowNode<>(global, DropManager::draw);
        Flow.global.addToUpdate(updateNode, 7);
        Flow.global.addToDraw(drawNode, 4);
    }

    /**
     * Видаляє зі списку оновлень та відмалювання.
     * @author SkyWarp
     */
    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }

    /**
     * Спавнить зірку.
     * @param pos позиція
     * @author afiliushkin
     */
    public void spawnStar(Vector2 pos) {
        Drop d = pull();
        if (d == null) return;
        d.type = DropType.STAR;
        d.speed = 3.0f;
        d.position.set(pos);
        d.sprite.loadAnm(anm);
        d.sprite.loadScriptAndPlay("Star");
    }

    /**
     * Спавнить предмет потужності.
     * @param pos позиція
     * @author afiliushkin
     */
    public void spawnPower(Vector2 pos) {
        Drop d = pull();
        if (d == null) return;
        d.type = DropType.POWER;
        d.speed = 3.0f;
        d.position.set(pos);
        d.sprite.loadAnm(anm);
        d.sprite.loadScriptAndPlay("Power");
    }

    /**
     * Забирає дроп з пулу.
     * @return якщо пул не заповнений, то витягнутий дроп. Інакше null.
     * @author afiliushkin
     */
    private Drop pull() {
        if (free == drops.length)
            return null;
        Drop drop = drops[free++];
        drop.active = true;
        while (free < drops.length && drops[free].active)
            ++free;
        return drop;
    }

    /**
     * Повертає кулю в пул.
     * @author afiliushkin
     */
    private void delete(Drop drop) {
        drop.active = false;
        drop.sprite.delete();
        if (free > drop.idx)
            free = drop.idx;
    }

    /**
     * Ініціалізує всі дропи.
     * @author afiliushkin
     */
    private int added() {
        anm = Assets.global.get(Anm.class, "game/drops.anm");
        for (Drop drop : drops)
            drop.active = false;
        return 0;
    }

    /**
     * Оновлює всі активні дропи та перевіряє їм колізію з гравцем.
     * @author SkyWarp
     */
    private int update() {
        if (!GameplayManager.global.canUpdate() || Player.global.isDeathBombing())
            return Flow.FLOW_RESULT_CONTINUE;
        if (dropSoundCooldown > 0)
            --dropSoundCooldown;
        for (Drop drop : drops) {
            if (!drop.active) continue;
            drop.collider.setPosition(drop.position.x, drop.position.y);
            if (Intersector.intersectPolygons(Player.global.grazeBox, drop.collider, null)) {
                long points;
                if (drop.type == DropType.STAR) {
                    points = SCORE_DROP;
                    if (drop.position.y < GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT * 0.5f)
                        points /= 2;
                    GameplayStats.global.score(points);
                } else {
                    points = POWER_DROP;
                    GameplayStats.global.power((int) points);
                }
                Hud.global.pickup(drop.position, points);
                if (dropSoundCooldown == 0) {
                    Audio.global.playSound("drop.ogg", 1.0f);
                    dropSoundCooldown = 5;
                }
                delete(drop);
                continue;
            }
            if (drop.position.y < GameplayManager.VIEWPORT_START_Y - 24.0f) {
                delete(drop);
                continue;
            }
            drop.speed += GRAVITY;
            if (drop.speed < TERMINAL_SPEED)
                drop.speed = TERMINAL_SPEED;
            drop.position.y += drop.speed;
            drop.sprite.teleport();
            drop.sprite.position.set(drop.position);
            drop.sprite.execute();
        }
        return 0;
    }

    /**
     * Відмальовує дропи на екрані.
     * @author SkyWarp
     */
    private int draw() {
        for (Drop drop : drops)
            if (drop.active)
                drop.sprite.draw();
        return 0;
    }

    /**
     * Очищує всі ресурси та дропи після видалення зі списків.
     * @author afiliushkin
     */
    private int removed() {
        for (Drop drop : drops)
            delete(drop);
        anm = null;
        Assets.global.unload("game/drops.anm");
        return 0;
    }

    /**
     * Дроп та його параметри.
     * @author SkyWarp
     */
    private final class Drop {
        public final int idx;

        public DropType type;
        public GraphicManager.AnmVirtualMachine sprite;
        public final Vector2 position;
        public final Polygon collider;
        public float speed;
        boolean active;

        private Drop(int idx) {
            this.idx = idx;
            sprite = GraphicManager.global.new AnmVirtualMachine();
            position = new Vector2();
            collider = new Polygon(new float[] { -8.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, 8.0f, -8.0f });
            active = false;
        }
    }
}
