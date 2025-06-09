package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Обробка усіх снарядів (поки тільки гравця)
 * @author SkyWarp
 */
public class BulletManager {
    public static BulletManager global;

    PlayersBullet[] plrSmallBullets;
    PlayersBullet[] plrBigBullets;
    EnemyBullet[] enemyBullets;

    private static Flow.FlowNode<BulletManager> updateNode;
    private static Flow.FlowNode<BulletManager> drawNode;

    public BulletManager() {
        plrSmallBullets = new PlayersBullet[32];
        plrBigBullets = new PlayersBullet[32];
        enemyBullets = new EnemyBullet[600];
    }

    public static void register(){
        updateNode = new Flow.FlowNode<>(global, BulletManager::update, BulletManager::added);
        Flow.global.addToUpdate(updateNode, 2);
        drawNode = new Flow.FlowNode<>(global, BulletManager::draw);
        Flow.global.addToDraw(drawNode, 555);
    }

    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }

    private static int update(BulletManager bulletManager) {
        bulletManager.updateBullets();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private static int added(BulletManager bulletManager) {
        for (int i=0; i<bulletManager.plrSmallBullets.length; i++) {
            bulletManager.plrSmallBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), 14, 2, 0, 20, 0, 0, 100);
        }
        for (int i=0; i<bulletManager.plrBigBullets.length; i++) {
            bulletManager.plrBigBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), 13, 5, 0, 40, 0, 0, 100);
        }
        for (int i=0; i<bulletManager.enemyBullets.length; i++) {
            bulletManager.enemyBullets[i] = bulletManager.new EnemyBullet(Assets.global.get(Anm.class,"game/plr.anm"), 13, 0.1f, 0, -0.3f, 5, 100);
        }
        return 0;
    }

    /**
     * Оновлює в усіх пулах ті кулі, які активні
     */
    private void updateBullets() {
        for (PlayersBullet bullet : plrSmallBullets) {
            if (bullet.active) {
                bullet.sprite.position.set(bullet.position);
                bullet.sprite.execute();
                bullet.move();
                bullet.checkCollision();
                if (bullet.lifeTime==0){
                    bullet.toPool();
                }
                bullet.lifeTime--;
            }
        }
        for (PlayersBullet bullet : plrBigBullets) {
            if (bullet.active) {
                bullet.sprite.position.set(bullet.position);
                bullet.sprite.execute();
                bullet.move();
                bullet.checkCollision();
                if (bullet.lifeTime==0){
                    bullet.toPool();
                }
                bullet.lifeTime--;
            }
        }
        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.active) {
                bullet.sprite.position.set(bullet.position);
                bullet.sprite.execute();
                bullet.move();
                bullet.checkCollision();
                if (bullet.lifeTime==0){
                    bullet.toPool();
                }
                bullet.lifeTime--;
            }
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
     * @param bulletPool Пул з якого дістається снаряд
     * @param pos Позиція спавну
     */
    void createPlayerBullet(PlayersBullet[] bulletPool, Vector2 pos) {
        int i = 0;
        int max = bulletPool.length;
        while (i < max && bulletPool[i].active) i++;
        if (i < max){
            bulletPool[i].active = true;
            bulletPool[i].position.set(pos);
            bulletPool[i].sprite.teleport();
        }
    }

    void createEnemyBullet(EnemyBullet[] bulletPool, Vector2 pos, float angle) {
        int i = 0;
        int max = bulletPool.length;
        while (i < max && bulletPool[i].active) i++;
        if (i<max-1){
            bulletPool[i].active = true;
            bulletPool[i].speed.rotateRad(angle);
            bulletPool[i].acceleration.rotateRad(angle);
            bulletPool[i].sprite.angle = angle;
            bulletPool[i].position.set(pos);
            bulletPool[i].sprite.teleport();
        }
    }

    /**
     * Будь-які кулі гравця
     * @author SkyWarp
     */
    class PlayersBullet extends Bullet{
        final int damage;
        public PlayersBullet(Anm source, int textureID, int damage,float accelerationY, float speedY, float accelerationX, float speedX, int lifeTime) {
            super(source, textureID, accelerationY, speedY, accelerationX, speedX, lifeTime);
            this.damage = damage;
        }

        private void checkCollision() {
            //TODO Колізії з ворогами
            if (position.y > Game.BASE_WINDOW_HEIGHT) {
                toPool();
            }
        }

    }
    /**
     * Будь-які кулі ворогів
     * @author SkyWarp
     */
    class EnemyBullet extends Bullet{

        public EnemyBullet(Anm source, int textureID, float accelerationY, float speedY, float accelerationX, float speedX, int lifeTime) {
            super(source, textureID, accelerationY, speedY, accelerationX, speedX, lifeTime);
        }

        private void checkCollision() {
            //TODO Колізії з гравцем
            if (position.y < 0 || position.y > Game.BASE_WINDOW_HEIGHT || position.x < 0 || position.x > Game.BASE_WINDOW_WIDTH) {
                toPool();
            }
        }

    }

    abstract class Bullet {
        GraphicManager.AnmVirtualMachine sprite;
        Vector2 position;
        boolean active;
        final Vector2 defaultSpeed;
        final Vector2 defaultAcceleration;
        Vector2 speed;
        Vector2 acceleration;
        final int defaultLifeTime;
        int lifeTime;

        private Bullet(Anm source, int textureID, float accelerationY, float speedY, float accelerationX, float speedX, int lifeTime) {
            sprite = GraphicManager.global.new AnmVirtualMachine();
            sprite.loadAnm(source);
            sprite.loadSource(textureID);
            this.position = new Vector2();
            this.active = false;
            this.speed = new Vector2(speedX, speedY);
            this.defaultAcceleration = new Vector2(accelerationX, accelerationY);
            this.acceleration = new Vector2(accelerationX, accelerationY);
            this.defaultSpeed = new Vector2(speedX, speedY);
            this.defaultLifeTime = lifeTime;
            this.lifeTime = lifeTime;
        }

        protected void move() {
            position.add(speed);
            speed.add(acceleration);
        }

        /**
         * Переведення в неактивний стан та скидання змінних
         */
        protected void toPool() {
            active = false;
            speed.set(defaultSpeed);
            acceleration.set(defaultAcceleration);
            lifeTime = defaultLifeTime;
            sprite.teleport();
        }
    }
}
