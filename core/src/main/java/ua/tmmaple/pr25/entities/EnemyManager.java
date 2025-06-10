package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;

public class EnemyManager {
    public static EnemyManager global;
    Enemy[] enemies;

    private static Flow.FlowNode<EnemyManager> updateNode;
    private static Flow.FlowNode<EnemyManager> drawNode;

    public EnemyManager(){
        enemies = new Enemy[32];
        for(int i = 0; i < enemies.length; i++){
            enemies[i] = new Enemy(1); //Скільки gun-ів має бути я не знаю тому 1
        }
    }
    public static void register(){
        updateNode = new Flow.FlowNode<>(global, EnemyManager::update);
        drawNode = new Flow.FlowNode<>(global, EnemyManager::draw);
        Flow.global.addToUpdate(updateNode,3);
        Flow.global.addToDraw(drawNode,3);
    }
    public static void shutdown(){
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }
    public void createEnemy(TimelineTask task, Task[] asynchTasks, float x, float y){
        int i = 0;
        while (i < enemies.length && !enemies[i].active) i++;
        if (i < enemies.length) {
            enemies[i].parent=null;
            enemies[i].children.removeRange(0, enemies[i].children.size-1);
            enemies[i].active = true;
            enemies[i].position.set(x, y);
            enemies[i].timelineTask = task;
            enemies[i].asynchTasks = asynchTasks;
        }
    }
    Enemy createEnemy(TimelineTask task, Task[] asynchTasks, float x, float y, Enemy parent){
        int i = 0;
        while (i < enemies.length && !enemies[i].active) i++;
        if (i < enemies.length) {
            enemies[i].parent = parent;
            enemies[i].active = true;
            enemies[i].children.removeRange(0, enemies[i].children.size-1);
            enemies[i].position.set(x, y);
            enemies[i].timelineTask = task;
            enemies[i].asynchTasks = asynchTasks;
        }
        return enemies[i];
    }
    private int update(){
        for (Enemy enemy : enemies){
            if(enemy.active) enemy.update();
        }
        return 0;
    }
    private int draw(){
        for (Enemy enemy : enemies){
            if (enemy.active) enemy.draw();
        }
        return 0;
    }
}
