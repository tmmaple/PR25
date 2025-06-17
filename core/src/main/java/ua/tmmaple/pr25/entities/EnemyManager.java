package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.task.TimelineTask;

/**
 * Менеджер ворогів.
 * @author SkyWarp
 */
public class EnemyManager {
    public static EnemyManager global;
    Enemy[] enemies;

    private static Flow.FlowNode<EnemyManager> updateNode;
    private static Flow.FlowNode<EnemyManager> drawNode;

    public EnemyManager(){
        enemies = new Enemy[240];
        for(int i = 0; i < enemies.length; i++){
            enemies[i] = new Enemy(6); //Скільки gun-ів має бути я не знаю тому 1
        }
    }

    /**
     * Реєструє у списки оновлення та малювання.
     * @author SkyWarp
     */
    public static void register(){
        updateNode = new Flow.FlowNode<>(global, EnemyManager::update);
        drawNode = new Flow.FlowNode<>(global, EnemyManager::draw);
        Flow.global.addToUpdate(updateNode,9);
        Flow.global.addToDraw(drawNode,5);
    }
    /**
     * Видаляє зі списків оновлення та малювання.
     * @author SkyWarp
     */
    public static void shutdown(){
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }
    /**
     * Створює ворога.
     * @author SkyWarp
     */
    public void createEnemy(TimelineTask task, float x, float y, int health){
        createEnemy(task, x, y, null, health);
    }
    /**
     * Створює ворога.
     * @author SkyWarp
     */
    Enemy createEnemy(TimelineTask task, float x, float y, Enemy parent, int health) {
        int i = 0;
        while (i < enemies.length && enemies[i].active) i++;
        if (i < enemies.length) {
            enemies[i].health = health;
            enemies[i].parent = parent;
            enemies[i].active = true;
            enemies[i].health = health;
            enemies[i].children.clear();
            enemies[i].position.set(x, y);
            enemies[i].timelineTask = task;
            enemies[i].setDeathVfx(VfxManager.Vfx.ENEMY_BLUE_DEATH);
            enemies[i].setCollision(true);
            enemies[i].setInvincible(false);
            enemies[i].setDeathSound("enmDeath.ogg");
            enemies[i].resetGraze();
            enemies[i].setIgnorePlayer(false);
            return enemies[i];
        }
        return null;
    }

    /**
     * Видаляє всіх ворогів. Вороги просто видаляються з екрану та повертаються до пулу.
     * @author uwuhasmile
     */
    public void clear() {
        for (int i = 0; i < enemies.length; ++i)
            if (enemies[i].active)
                enemies[i].destroy();
    }

    /**
     * Наносить шкоду всім ворогам у певному радіусі. В першу чергу використовується бомбою для знищення всіх ворогів поблизу.
     * @param a позиція, навколо якої треба нанести шкоду ворогам
     * @param radius радіус відносно a
     * @param damage кількість шкоди, нанесеної ворогам
     * @author uwuhasmile
     */
    public boolean damageAllInRadius(Vector2 a, float radius, int damage) {
        boolean result = false;
        for (int i = 0; i < enemies.length; ++i)
            if (enemies[i].active && enemies[i].viewportPosition().dst2(a) <= radius * radius) {
                enemies[i].damage(damage);
                result = true;
            }
        return result;
    }

    /**
     * Оновлює всіх ворогів.
     * @author SkyWarp
     */
    private int update() {
        if (!GameplayManager.global.canUpdate() || Player.global.isDeathBombing())
            return Flow.FLOW_RESULT_CONTINUE;
        for (Enemy enemy : enemies){
            if(enemy.active) enemy.update();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
    /**
     * Малює всіх ворогів.
     * @author SkyWarp
     */
    private int draw(){
        for (Enemy enemy : enemies){
            if (enemy.active) enemy.draw();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
