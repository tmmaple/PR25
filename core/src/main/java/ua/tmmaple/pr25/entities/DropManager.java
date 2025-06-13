package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

public class DropManager {
    private static final int POWER_DROP = 1;
    private static final int SCORE_DROP = 500;

    public static DropManager global;

    private ScoreDrop[] scoreDropPool;
    private PowerDrop[] powerDropPool;

    private static Flow.FlowNode<DropManager> updateNode;
    private static Flow.FlowNode<DropManager> drawNode;

    private short dropSoundCooldown;

    public DropManager() {
        scoreDropPool = new ScoreDrop[64];
        powerDropPool = new PowerDrop[64];
    }
    public static void register(){
        updateNode = new Flow.FlowNode<>(global, DropManager::update);
        drawNode = new Flow.FlowNode<>(global, DropManager::draw);
        for (int i = 0; i < 64; i++) {
            DropManager.global.scoreDropPool[i] = DropManager.global.new ScoreDrop(Assets.global.get(Anm.class,"game/plr.anm"), "SmallBullet"); //Тимчасово
            DropManager.global.powerDropPool[i] = DropManager.global.new PowerDrop(Assets.global.get(Anm.class,"game/plr.anm"), "BigBullet"); //Поки нема спрайта дропів
        }
        Flow.global.addToUpdate(updateNode, 7);
        Flow.global.addToDraw(drawNode, 4);
    }
    public static void shutdown(){
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
    }
    public void createScoreDrop(Vector2 pos) {
        int i = 0;
        while (i < scoreDropPool.length && scoreDropPool[i].active) i++;
        if (i < scoreDropPool.length) {
            scoreDropPool[i].active = true;
            scoreDropPool[i].position.set(pos);
            scoreDropPool[i].speed = scoreDropPool[i].defaultSpeed;
        }
    }
    public void createPowerDrop(Vector2 pos) {
        int i = 0;
        while (i < powerDropPool.length && powerDropPool[i].active) i++;
        if (i < powerDropPool.length) {
            powerDropPool[i].active = true;
            powerDropPool[i].position.set(pos);
            powerDropPool[i].speed = powerDropPool[i].defaultSpeed;
        }
    }
    private int update() {
        if (dropSoundCooldown > 0)
            --dropSoundCooldown;
        for (ScoreDrop scoreDrop : scoreDropPool) {
            if (scoreDrop.active){
                scoreDrop.sprite.execute();
                if (Intersector.intersectPolygons(Player.global.grazeBox, scoreDrop.hitbox, null)){
                    GameplayStats.global.score(SCORE_DROP);
                    Hud.global.pickup(scoreDrop.position, SCORE_DROP);
                    if (dropSoundCooldown == 0) {
                        Audio.global.playSound("drop.ogg", 1.0f);
                        dropSoundCooldown = 5;
                    }
                    scoreDrop.active = false;
                }
                scoreDrop.move();
                if (scoreDrop.position.y < 0) scoreDrop.active = false;
            }
        }
        for (PowerDrop powerDrop : powerDropPool) {
            if (powerDrop.active){
                powerDrop.sprite.execute();
                if (Intersector.intersectPolygons(Player.global.grazeBox, powerDrop.hitbox, null)){
                    powerDrop.active = false;
                    Hud.global.pickup(powerDrop.position, POWER_DROP);
                    if (dropSoundCooldown == 0) {
                        Audio.global.playSound("drop.ogg", 1.0f);
                        dropSoundCooldown = 5;
                    }
                    GameplayStats.global.power(POWER_DROP);
                }
                powerDrop.move();
                if (powerDrop.position.y < 0) powerDrop.active = false;
            }
        }
        return 0;
    }
    private int draw() {
        for (ScoreDrop scoreDrop : scoreDropPool) {
            if (scoreDrop.active){
                scoreDrop.draw();
            }
        }
        for (PowerDrop powerDrop : powerDropPool) {
            if (powerDrop.active){
                powerDrop.draw();
            }
        }
        return 0;
    }

    abstract class Drop {
        GraphicManager.AnmVirtualMachine sprite;
        Vector2 position;
        Polygon hitbox;
        float speed;
        float defaultSpeed;
        private float speedLimit;
        protected float acceleration;
        boolean active;

        private Drop(Anm source, String script, float speed, float acceleration) {
            //TODO спрайти дропу
            sprite = GraphicManager.global.new AnmVirtualMachine();
            sprite.loadAnm(source);
            sprite.loadScriptAndPlay(script);
            sprite.scale.scl(2); //Для тесту, щоб відрізняти від реальних куль, прибрати коли буде спрайт
            position = new Vector2();
            hitbox = new Polygon(new float[] {-2, -2, -2, 2, 2, 2, 2, -2}); //Поміняти розмір коли буде готовий спрайт
            active = false;
            speedLimit = -7.0f;
            this.defaultSpeed = speed;
            this.acceleration = acceleration;
        }
        protected void move() {
            position.y += speed;
            if (speed > speedLimit) speed += acceleration;
            hitbox.setPosition(position.x, position.y);
        }
        protected void draw() {
            sprite.position.set(position);
            sprite.draw();
        }
    }

    class ScoreDrop extends Drop {
        private ScoreDrop(Anm source, String script) {
            super(source, script, 2.0f, -0.1f);
        }
    }

    class PowerDrop extends Drop {
        private PowerDrop(Anm source, String script) {
            super(source, script, 2.0f, -0.1f);
        }
    }
}
