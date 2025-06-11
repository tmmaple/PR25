package ua.tmmaple.pr25.entities;

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
        Flow.global.addToUpdate(updateNode,3);
        Flow.global.addToDraw(drawNode,7);
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
            return enemies[i];
        }
        return null;
    }

    private int update(){
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
