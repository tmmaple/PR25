package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Обробка усіх снарядів (поки тільки гравця)
 * @author SkyWarp
 */
public class BulletManager {
    public static final BulletType[] BULLET_TYPES = {
        new BulletType("BULLET_8x8_RED", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_ORANGE", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_YELLOW", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_GREEN", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_BLUE", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_PURPLE", false, 6.0f, 6.0f),
        new BulletType("BULLET_8x8_WHITE", false, 6.0f, 6.0f),
        new BulletType("BULLET_4x8_RED", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_ORANGE", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_YELLOW", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_GREEN", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_BLUE", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_PURPLE", true, 8.0f, 4.0f),
        new BulletType("BULLET_4x8_WHITE", true, 8.0f, 4.0f),
        new BulletType("BULLET_12x12_RED", false, 8.0f, 8.0f),
        new BulletType("BULLET_12x12_ORANGE", false, 8.0f, 8.0f),
        new BulletType("BULLET_12x12_YELLOW", false, 8.0f, 8.0f),
        new BulletType("BULLET_12x12_GREEN", false, 8.0f, 10.0f),
        new BulletType("BULLET_12x12_BLUE", false, 8.0f, 10.0f),
        new BulletType("BULLET_12x12_PURPLE", false, 8.0f, 10.0f),
        new BulletType("BULLET_12x12_WHITE", false, 8.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_RED", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_ORANGE", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_YELLOW", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_GREEN", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_BLUE", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_PURPLE", false, 10.0f, 10.0f),
        new BulletType("BULLET_RINGED_12x12_WHITE", false, 10.0f, 10.0f),
        new BulletType("BULLET_8x12_RED", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_ORANGE", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_YELLOW", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_GREEN", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_BLUE", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_PURPLE", true, 10.0f, 6.0f),
        new BulletType("BULLET_8x12_WHITE", true, 10.0f, 6.0f),
        new BulletType("BULLET_4x12_RED", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_ORANGE", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_YELLOW", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_GREEN", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_BLUE", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_PURPLE", true, 12.0f, 4.0f),
        new BulletType("BULLET_4x12_WHITE", true, 12.0f, 4.0f),
        new BulletType("BULLET_16x16_RED", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_ORANGE", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_YELLOW", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_GREEN", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_BLUE", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_PURPLE", false, 12.0f, 12.0f),
        new BulletType("BULLET_16x16_WHITE", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_RED", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_ORANGE", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_YELLOW", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_GREEN", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_BLUE", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_PURPLE", false, 12.0f, 12.0f),
        new BulletType("BULLET_RINGED_16x16_WHITE", false, 12.0f, 12.0f),
        new BulletType("BULLET_10x16_RED", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_ORANGE", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_YELLOW", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_GREEN", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_BLUE", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_PURPLE", true, 16.0f, 10.0f),
        new BulletType("BULLET_10x16_WHITE", true, 16.0f, 10.0f),
        new BulletType("BULLET_32x32_RED", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_ORANGE", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_YELLOW", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_GREEN", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_BLUE", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_PURPLE", false, 24.0f, 24.0f),
        new BulletType("BULLET_32x32_WHITE", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_RED", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_ORANGE", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_YELLOW", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_GREEN", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_BLUE", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_PURPLE", false, 24.0f, 24.0f),
        new BulletType("BULLET_RINGED_32x32_WHITE", false, 24.0f, 24.0f),
        new BulletType("BULLET_16x32_RED", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_ORANGE", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_YELLOW", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_GREEN", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_BLUE", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_PURPLE", true, 30.0f, 12.0f),
        new BulletType("BULLET_16x32_WHITE", true, 30.0f, 12.0f),
    };

    public static BulletManager global;

    public final PlayersBullet[] plrSmallBullets;
    public final PlayersBullet[] plrBigBullets;
    public final EnemyBullet[] enemyBullets;

    private Anm anm;

    private short playerHitSoundCooldown;

    private static Flow.FlowNode<BulletManager> updateNode;
    private static Flow.FlowNode<BulletManager> drawNode;

    public BulletManager() {
        plrSmallBullets = new PlayersBullet[32];
        plrBigBullets = new PlayersBullet[32];
        enemyBullets = new EnemyBullet[600];
    }

    public static void load() {
        Assets.global.load(Anm.class,"game/bullets.anm");
    }

    public static void register(){
        updateNode = new Flow.FlowNode<>(global, BulletManager::update, BulletManager::added);
        Flow.global.addToUpdate(updateNode, 8);
        drawNode = new Flow.FlowNode<>(global, BulletManager::draw);
        Flow.global.addToDraw(drawNode, 6);
    }

    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }

    public void clear() {
        for (PlayersBullet b : plrSmallBullets)
            b.active = false;
        for (PlayersBullet b : plrBigBullets)
            b.active = false;
        for (EnemyBullet b : enemyBullets)
            b.active = false;
    }

    public void destroyEnemyBullets() {
        for (EnemyBullet b : enemyBullets) {
            if (b.active) {
                VfxManager.global.spawnDust(b.position, b.getAngle(), 12, 0.0f, 4.0f);
                b.active = false;
            }
        }
    }

    public void destroyEnemyBulletsInRadius(Vector2 origin, float radius) {
        for (EnemyBullet b : enemyBullets)
            if (b.active && b.position.dst2(origin) <= radius * radius) {
                VfxManager.global.spawnDust(b.position, b.getAngle(), 12, 0.0f, 4.0f);
                b.active = false;
            }
    }

    private static int update(BulletManager bulletManager) {
        if (!GameplayManager.global.canUpdate() || Player.global.isDeathBombing())
            return Flow.FLOW_RESULT_CONTINUE;
        bulletManager.updateBullets();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private static int added(BulletManager bulletManager) {
        bulletManager.playerHitSoundCooldown = (short) 0;
        bulletManager.anm = Assets.global.get(Anm.class, "game/bullets.anm");
        for (int i=0; i<bulletManager.plrSmallBullets.length; i++) {
            bulletManager.plrSmallBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), "SmallBullet", 2, 15.0f);
        }
        for (int i=0; i<bulletManager.plrBigBullets.length; i++) {
            bulletManager.plrBigBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), "BigBullet", 5, 10.0f);
        }
        for (int i=0; i<bulletManager.enemyBullets.length; i++) {
            bulletManager.enemyBullets[i] = bulletManager.new EnemyBullet(bulletManager.anm);
        }
        return 0;
    }

    private static int removed(BulletManager bulletManager) {
        Assets.global.unload("game/bullets.anm");
        return 0;
    }

    /**
     * Оновлює в усіх пулах ті кулі, які активні
     */
    private void updateBullets() {
        if (playerHitSoundCooldown > 0)
            --playerHitSoundCooldown;
        small_bullets:
        for (PlayersBullet bullet : plrSmallBullets) {
            if (!bullet.active) continue;
            if (bullet.position.y > GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT + 32.0f) {
                bullet.toPool();
                continue;
            }
            for (Enemy enemy: EnemyManager.global.enemies){
                if (!enemy.active || !enemy.hasCollision()) continue;
                if (Intersector.intersectPolygons(enemy.hitbox, bullet.collider, null)) {
                    if (playerHitSoundCooldown == (short) 0) {
                        Audio.global.playSound("plrBulletHit.ogg", 1.0f);
                        playerHitSoundCooldown = 5;
                    }
                    VfxManager.global.spawnEnemyDamage(bullet.sprite.absolutePosition());
                    enemy.damage(bullet.damage);
                    bullet.toPool();
                    continue small_bullets;
                }
            }
            bullet.position.add(bullet.velocity);
            bullet.collider.setPosition(bullet.position.x, bullet.position.y);
            bullet.sprite.position.set(bullet.position);
            bullet.sprite.execute();
        }
        big_bullets:
        for (PlayersBullet bullet : plrBigBullets) {
            if (!bullet.active) continue;
            if (bullet.position.y > GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT + 32.0f) {
                bullet.toPool();
                continue;
            }
            for (Enemy enemy: EnemyManager.global.enemies){
                if (!enemy.active || !enemy.hasCollision()) continue;
                if (Intersector.intersectPolygons(enemy.hitbox, bullet.collider, null)) {
                    if (playerHitSoundCooldown == (short) 0) {
                        Audio.global.playSound("plrBulletHit.ogg", 1.0f);
                        playerHitSoundCooldown = 5;
                    }
                    VfxManager.global.spawnEnemyDamage(bullet.sprite.absolutePosition());
                    enemy.damage(bullet.damage);
                    bullet.toPool();
                    continue big_bullets;
                }
            }
            bullet.position.add(bullet.velocity);
            bullet.collider.setPosition(bullet.position.x, bullet.position.y);
            bullet.sprite.position.set(bullet.position);
            bullet.sprite.execute();
        }
        for (EnemyBullet bullet : enemyBullets) {
            if (!bullet.active) continue;
            bullet.position.add(bullet.velocity);
            bullet.collider.setPosition(bullet.position.x, bullet.position.y);
            bullet.setSpeed(bullet.getSpeed() + bullet.acceleration);
            bullet.setAngle(bullet.getAngle() + bullet.getAngularSpeed());
            bullet.setAngularSpeed(bullet.getAngularSpeed() + bullet.angularAcceleration);
            if (bullet.rotates) {
                bullet.collider.setRotation(MathUtils.radDeg * bullet.getAngle());
                bullet.sprite.angle = bullet.getAngle();
            }
            bullet.sprite.position.set(bullet.position);
            if (Intersector.intersectPolygons(bullet.collider, Player.global.hitbox, null)) {
                bullet.toPool();
                Player.global.damage();
            } else if (!BombManager.global.isInUse() && !bullet.grazed && Intersector.intersectPolygons(bullet.collider, Player.global.grazeBox, null)) {
                Player.global.graze();
                bullet.grazed = true;
            } else if (bullet.position.y < GameplayManager.VIEWPORT_START_Y - 32.0
                    || bullet.position.y > GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT + 32.0f
                    || bullet.position.x < GameplayManager.VIEWPORT_START_X - 32.0f
                    || bullet.position.x > GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH + 32.0f)
                bullet.toPool();
            bullet.sprite.execute();
        }
    }

    private static int draw(BulletManager bulletManager) {
        for (PlayersBullet bullet : bulletManager.plrSmallBullets) {
            if (bullet.active) {
                bullet.sprite.draw();
            }
        }
        for (PlayersBullet bullet : bulletManager.plrBigBullets) {
            if (bullet.active) {
                bullet.sprite.draw();
            }
        }
        for (EnemyBullet bullet : bulletManager.enemyBullets) {
            if (bullet.active) {
                bullet.sprite.draw();
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * @param pos Позиція спавну
     */
    public void createSmallPlayerBullet(Vector2 pos) {
        int i = 0;
        int max = plrSmallBullets.length;
        while (i < max && plrSmallBullets[i].active) i++;
        if (i < max){
            plrSmallBullets[i].active = true;
            plrSmallBullets[i].position.set(pos);
        }
    }

    /**
     * @param pos Позиція спавну
     */
    public void createBigPlayerBullet(Vector2 pos) {
        int i = 0;
        int max = plrBigBullets.length;
        while (i < max && plrBigBullets[i].active) i++;
        if (i < max){
            plrBigBullets[i].active = true;
            plrBigBullets[i].position.set(pos);
        }
    }

    public EnemyBullet createEnemyBullet(Vector2 pos, float angle, float speed, float acceleration, float angularSpeed, float angularAcceleration, int type) {
        int i = 0;
        int max = enemyBullets.length;
        while (i < max && enemyBullets[i].active) i++;
        if (i < max){
            enemyBullets[i].grazed = false;
            enemyBullets[i].active = true;
            enemyBullets[i].setVelocity(angle, speed);
            enemyBullets[i].acceleration = acceleration;
            enemyBullets[i].setAngularSpeed(angularSpeed);
            enemyBullets[i].angularAcceleration = angularAcceleration;
            enemyBullets[i].position.set(pos);
            enemyBullets[i].setType(BULLET_TYPES[type]);
            return enemyBullets[i];
        }
        return null;
    }

    /**
     * Будь-які кулі гравця
     * @author SkyWarp
     */
    public class PlayersBullet extends Bullet {
        public final int damage;

        public PlayersBullet(Anm source, String sprite, int damage, float speed) {
            super(source);
            float[] vertices = this.collider.getVertices();
            vertices[0] = -2.0f;
            vertices[1] = -2.0f;
            vertices[2] = -2.0f;
            vertices[3] = 2.0f;
            vertices[4] = 2.0f;
            vertices[5] = 2.0f;
            vertices[6] = 2.0f;
            vertices[7] = -2.0f;
            this.collider.setOrigin(-2.0f, -2.0f);
            this.damage = damage;
            this.sprite.loadScriptAndPlay(sprite);
            this.setVelocity(MathUtils.HALF_PI, speed);
        }
    }
    /**
     * Будь-які кулі ворогів
     * @author SkyWarp
     */
    public class EnemyBullet extends Bullet {
        public boolean rotates;

        public EnemyBullet(Anm source) {
            super(source);
        }

        public void setType(BulletType type) {
            sprite.loadScriptAndPlay(type.sprite);
            this.rotates = type.rotates;
            float[] vertices = collider.getVertices();
            vertices[0] = -type.collisionWidth * 0.5f;
            vertices[1] = -type.collisionHeight * 0.5f;
            vertices[2] = -type.collisionWidth * 0.5f;
            vertices[3] = type.collisionHeight * 0.5f;
            vertices[4] = type.collisionWidth * 0.5f;
            vertices[5] = type.collisionHeight * 0.5f;
            vertices[6] = type.collisionWidth * 0.5f;
            vertices[7] = -type.collisionHeight * 0.5f;
        }
    }

    public abstract class Bullet {
        private static final int LIFETIME = 100000;

        public final Polygon collider;
        public final GraphicManager.AnmVirtualMachine sprite;
        public final Vector2 position;
        public final Vector2 velocity;
        public boolean active;
        private float speed;
        public float acceleration;
        private float angle;
        private float angularSpeed;
        public float angularAcceleration;
        public boolean rotates;
        public boolean grazed;

        private Bullet(Anm source) {
            position = new  Vector2();
            velocity = new Vector2();
            collider = new Polygon(new float[8]);
            sprite = GraphicManager.global.new AnmVirtualMachine();
            sprite.loadAnm(source);
        }

        public void setVelocity(float angle, float speed) {
            if (speed < 0.0f)
                speed = 0.0f;
            this.angle = angle;
            this.speed = speed;
            velocity.set(1.0f, 0.0f).setAngleRad(angle).scl(speed);
        }

        public void setSpeed(float speed) {
            this.speed = speed;
            velocity.nor().scl(speed);
        }

        public void setAngle(float angle) {
            this.angle = angle;
            velocity.setAngleRad(angle);
        }

        public void setAngularSpeed(float angularSpeed) {
            this.angularSpeed = angularSpeed;
        }

        public float getSpeed() {
            return speed;
        }

        public float getAngle() {
            return angle;
        }

        public float getAngularSpeed() {
            return angularSpeed;
        }

        /**
         * Переведення в неактивний стан та скидання змінних
         */
        protected void toPool() {
            active = false;
        }
    }

    public static final class BulletType {
        public final String sprite;
        public final boolean rotates;
        public final float collisionWidth;
        public final float collisionHeight;

        public BulletType(String spriteName, boolean rotates, float collisionWidth, float collisionHeight) {
            this.sprite = spriteName;
            this.rotates = rotates;
            this.collisionWidth = collisionWidth;
            this.collisionHeight = collisionHeight;
        }
    }
}
