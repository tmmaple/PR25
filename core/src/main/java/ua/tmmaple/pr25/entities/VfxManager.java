package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

public final class VfxManager {
    public static VfxManager global;

    public enum Vfx {
        NONE,
        DUST_PIECES,
        ENEMY_DAMAGE,
        ENEMY_BLUE_DEATH,
        ENEMY_ORANGE_DEATH,
        MIDBOSS_BLUE_DEATH,
        MIDBOSS_ORANGE_DEATH,
        BOSS_BLUE_DEATH,
        BOSS_ORANGE_DEATH,
        PLAYER_DEATH,
    }

    private enum MovementType {
        DIRECTIONAL,
        ORBITAL,
    }

    private Anm anm;
    private final Particle[] particles;
    private int free;

    private static Flow.FlowNode<VfxManager> updateNode;
    private static Flow.FlowNode<VfxManager> drawNode;

    public static void register() {
        if (updateNode != null) return;
        updateNode = new Flow.FlowNode<>(global, VfxManager::update, VfxManager::added, VfxManager::removed);
        drawNode = new Flow.FlowNode<>(global, VfxManager::draw);
        Flow.global.addToUpdate(updateNode, 4);
        Flow.global.addToDraw(drawNode, 3);
    }

    public static void shutdown() {
        if (updateNode == null) return;
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    public VfxManager() {
        particles = new Particle[256];
        for (int i = 0; i < particles.length; ++i)
            particles[i] = new Particle(i);
    }

    public void spawnDust(Vector2 center, float startAngle, int count, float radius, float speed) {
        if (anm == null) return;
        if (count > particles.length)
            count = particles.length;
        float angleStep = MathUtils.PI2 / count;
        for (int i = 0; i < count; ++i) {
            Particle p = pull();
            p.vm.loadScriptAndPlay("Dust");
            p.angle = startAngle + angleStep * i;
            p.position.set(center).add(radius * MathUtils.cos(p.angle), radius * MathUtils.sin(p.angle));
            p.timeLeft = (short) 8;
            p.speed = speed;
        }
    }

    public void spawnPlayerDeath(Vector2 center) {
        if (anm == null) return;
        float angleStep = MathUtils.PI2 / 32;
        for (int i = 0; i < 32; ++i) {
            Particle p = pull();
            p.vm.loadScriptAndPlay("PlayerDeath");
            p.angle = MathUtils.degRad * 30f + angleStep * i;
            p.position.set(center).add(2.0f * MathUtils.cos(p.angle), 2.0f * MathUtils.sin(p.angle));
            p.timeLeft = (short) 24;
            p.speed = 6.0f;
        }
    }

    public void spawnEnemyDamage(Vector2 position) {
        if (anm == null) return;
        Particle p = pull();
        p.vm.loadScriptAndPlay("EnemyDamage");
        p.position.set(position);
        p.timeLeft = (short) 12;
    }

    public void spawnEnemyDeath(Vector2 position, String script) {
        if (anm == null) return;
        Particle p = pull();
        p.vm.loadScriptAndPlay(script);
        p.position.set(position);
        p.timeLeft = (short) 24;
    }

    public void spawnMidbossDeath(Vector2 position, String script) {
        if (anm == null) return;
        float angleStep = MathUtils.PI2 / 10;
        for (int i = 0; i < 10; ++i) {
            Particle p = pull();
            p.vm.loadScriptAndPlay(script);
            p.movementType = MovementType.ORBITAL;
            p.angle = MathUtils.degRad * 40.0f + angleStep * i;
            p.position.set(position);
            p.radiusX = 48.0f;
            p.radiusY = 48.0f;
            p.timeLeft = (short) 30;
            p.speed = MathUtils.degRad * 2.0f;
        }
    }

    public void spawnBossDeath(Vector2 position, String script) {
        if (anm == null) return;
        float angleStep = MathUtils.PI2 / 32;
        for (int i = 0; i < 32; ++i) {
            Particle p = pull();
            p.vm.loadScriptAndPlay(script);
            p.movementType = MovementType.ORBITAL;
            p.angle = MathUtils.degRad * -25.0f + angleStep * i;
            p.position.set(position);
            p.radiusX = 72.0f;
            p.radiusY = 72.0f;
            p.timeLeft = (short) 30;
            p.speed = MathUtils.degRad * 2.0f;
        }
    }

    public void spawn(Vfx vfx, Vector2 position) {
        switch (vfx) {
            case NONE: return;
            case DUST_PIECES:
                spawnDust(position, 0.0f, 5, 2.0f, 5.0f);
                break;
            case ENEMY_DAMAGE:
                spawnEnemyDamage(position);
                break;
            case ENEMY_BLUE_DEATH:
                spawnEnemyDeath(position, "EnemyDeathBlue");
                break;
            case ENEMY_ORANGE_DEATH:
                spawnEnemyDeath(position, "EnemyDeathOrange");
                break;
            case MIDBOSS_BLUE_DEATH:
                spawnMidbossDeath(position, "EnemyDeathBlue");
                break;
            case MIDBOSS_ORANGE_DEATH:
                spawnMidbossDeath(position, "EnemyDeathOrange");
                break;
            case BOSS_BLUE_DEATH:
                spawnBossDeath(position, "EnemyDeathBlue");
                break;
            case BOSS_ORANGE_DEATH:
                spawnBossDeath(position, "EnemyDeathOrange");
                break;
            case PLAYER_DEATH:
                spawnPlayerDeath(position);
                break;
        }
    }

    private int added() {
        Assets.global.load(Anm.class, "game/vfx.anm");
        clear();
        return 0;
    }

    private int update() {
        if (anm == null && Assets.global.isLoaded("game/vfx.anm"))
            anm = Assets.global.get(Anm.class, "game/vfx.anm");
        if (!GameplayManager.global.canUpdate())
            return Flow.FLOW_RESULT_CONTINUE;
        for (int i = 0; i < particles.length; ++i) {
            Particle p = particles[i];
            if (!p.active)
                continue;
            if (p.timeLeft == (short) 0) {
                put(p);
                continue;
            }
            --p.timeLeft;
            Vector2 position = new Vector2();
            switch (p.movementType) {
                case DIRECTIONAL: {
                    p.position.add(new Vector2(p.speed, 0.0f).setAngleRad(p.angle));
                    position.set(p.position);
                } break;
                case ORBITAL: {
                    p.angle += p.speed;
                    position.set(p.position.x + MathUtils.cos(p.angle) * p.radiusX, p.position.y + MathUtils.sin(p.angle) * p.radiusY);
                } break;
            }
            p.vm.position.set(position);
            p.vm.angle = p.angle;
            p.vm.execute();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int draw() {
        for (int i = 0; i < particles.length; ++i) {
            Particle p = particles[i];
            if (p.active)
                p.vm.draw();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int removed() {
        clear();
        anm = null;
        Assets.global.unload("game/vfx.anm");
        return 0;
    }

    public void clear() {
        for (Particle particle : particles)
            particle.reset();
        free = 0;
    }

    private Particle pull() {
        if (anm == null)
            return null;
        Particle particle = particles[free++];
        particle.reset();
        particle.active = true;
        particle.vm.loadAnm(anm);
        while (free < particles.length && particles[free].active)
            ++free;
        if (free == particles.length)
            free = 0;
        return particle;
    }

    private void put(Particle particle) {
        particle.reset();
        free = particle.idx;
    }

    private final class Particle {
        public final int idx;

        public final GraphicManager.AnmVirtualMachine vm;
        public MovementType movementType;
        public float angle;
        public float speed;
        public final Vector2 position;
        public float radiusX;
        public float radiusY;
        public short timeLeft;
        public boolean active;

        public Particle(int idx) {
            this.idx = idx;
            vm = GraphicManager.global.new AnmVirtualMachine();
            position = new Vector2();
        }

        public void reset() {
            movementType = MovementType.DIRECTIONAL;
            angle = 0.0f;
            speed = 0.0f;
            position.setZero();
            radiusX = 0.0f;
            radiusY = 0.0f;
            vm.delete();
            active = false;
        }
    }
}
