package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.task.TimelineTask;

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
    public static void register(){
        updateNode = new Flow.FlowNode<>(global, EnemyManager::update);
        drawNode = new Flow.FlowNode<>(global, EnemyManager::draw);
        Flow.global.addToUpdate(updateNode,9);
        Flow.global.addToDraw(drawNode,5);
    }
    public static void shutdown(){
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }
    public void createEnemy(TimelineTask task, float x, float y, int health){
        int i = 0;
        while (i < enemies.length && !enemies[i].active) i++;
        if (i < enemies.length) {
            enemies[i].parent=null;
            enemies[i].children.removeRange(0, enemies[i].children.size-1);
            enemies[i].active = true;
            enemies[i].health = health;
            enemies[i].position.set(x, y);
            enemies[i].timelineTask = task;
            enemies[i].setDeathVfx(VfxManager.Vfx.ENEMY_BLUE_DEATH);
            enemies[i].setCollision(true);
            enemies[i].setInvincible(false);
            enemies[i].setDeathSound("enmDeath.ogg");
            enemies[i].resetGraze();
            enemies[i].setIgnorePlayer(false);
        }
    }
    Enemy createEnemy(TimelineTask task, float x, float y, Enemy parent, int health) {
        int i = 0;
        while (i < enemies.length && enemies[i].active) i++;
        if (i < enemies.length) {
            enemies[i].health = 100;
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

    public void clear() {
        for (int i = 0; i < enemies.length; ++i)
            if (enemies[i].active)
                enemies[i].destroy();
    }

    public boolean killAll() {
        boolean result = false;
        for (int i = 0; i < enemies.length; ++i)
            if (enemies[i].active) {
                enemies[i].destroy();
                result = true;
            }
        return result;
    }

    public boolean damageAllInRadius(Vector2 a, float radius, int damage) {
        boolean result = false;
        for (int i = 0; i < enemies.length; ++i)
            if (enemies[i].active && enemies[i].viewportPosition().dst2(a) <= radius * radius) {
                enemies[i].damage(damage);
                result = true;
            }
        return result;
    }

    private int update() {
        if (!GameplayManager.global.canUpdate() || Player.global.deathbombing())
            return Flow.FLOW_RESULT_CONTINUE;
        for (Enemy enemy : enemies){
            if(enemy.active) enemy.update();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
    private int draw(){
        for (Enemy enemy : enemies){
            if (enemy.active) enemy.draw();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
