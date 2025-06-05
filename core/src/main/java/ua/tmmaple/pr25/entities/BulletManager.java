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

    public BulletManager() {
        plrSmallBullets = new PlayersBullet[20];
        plrBigBullets = new PlayersBullet[10];
    }
    public static void register(){
        Flow.FlowNode<BulletManager> upd = new Flow.FlowNode<>(global, BulletManager::update, BulletManager::added);
        Flow.global.addToUpdate(upd, 2);
        Flow.FlowNode<BulletManager> draw = new Flow.FlowNode<>(global, BulletManager::draw);
        Flow.global.addToDraw(draw, 2);
    }
    private static int update(BulletManager bulletManager) {
        bulletManager.updateBullets();
        return Flow.FLOW_RESULT_CONTINUE;
    }
    private static int added(BulletManager bulletManager) {
        for (int i=0; i<bulletManager.plrSmallBullets.length; i++) {
            bulletManager.plrSmallBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), 14, 2, 0, 8, 0, 0, 100);
        }
        for (int i=0; i<bulletManager.plrBigBullets.length; i++) {
            bulletManager.plrBigBullets[i] = bulletManager.new PlayersBullet(Assets.global.get(Anm.class,"game/plr.anm"), 13, 5, 0, 5, 0, 0, 100);
        }
        return 0;
    }

    /**
     * Оновлює в усіх пулах ті кулі, які активні
     */
    private void updateBullets() {
        for (PlayersBullet bullet : plrSmallBullets) {
            if (bullet.active) {
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
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * @param bulletPool Пул з якого дістається снаряд
     * @param pos Позиція спавну
     */
    void createBullet(PlayersBullet[] bulletPool,Vector2 pos) {
        int i = 0;
        int max = bulletPool.length;
        while (i < max && bulletPool[i].active) i++;
        if (i<max-1){
            bulletPool[i].active = true;
            bulletPool[i].sprite.position.set(pos);
        }
    }

    /**
     * Будь-які кулі гравця
     * Мають швидкість і прискорення по X та Y, шкоду, час життя і текстуру
     * @author SkyWarp
     */
    class PlayersBullet {
        GraphicManager.AnmVirtualMachine sprite;
        boolean active;
        final int damage;
        final float accelerationY;
        final float defaultSpeedY;
        float speedY;
        final float accelerationX;
        final float defaultSpeedX;
        float speedX;
        final int defaultLifeTime;
        int lifeTime;
        public PlayersBullet(Anm source, int textureID, int damage,float accelerationY, float speedY, float accelerationX, float speedX, int lifeTime) {
            sprite = GraphicManager.global.new AnmVirtualMachine();
            sprite.loadAnm(source);
            sprite.loadSource(textureID);
            this.active = false;
            this.damage = damage;
            this.accelerationY = accelerationY;
            this.speedY = speedY;
            this.accelerationX = accelerationX;
            this.speedX = speedX;
            this.defaultSpeedY = speedY;
            this.defaultSpeedX = speedX;
            this.defaultLifeTime = lifeTime;
            this.lifeTime = lifeTime;
        }
        private void move() {
            sprite.position.y += speedY;
            speedY += accelerationY;
            sprite.position.x += speedX;
            speedX += accelerationX;
        }
        private void checkCollision() {
            //TODO Колізії з ворогами
            if (sprite.position.y > Game.BASE_WINDOW_HEIGHT) {
                toPool();
            }
        }

        /**
         * Переведення в неактивний стан та скидання змінних
         */
        private void toPool() {
            active = false;
            speedY = defaultSpeedY;
            speedX = defaultSpeedX;
            lifeTime = defaultLifeTime;
        }
    }
}
