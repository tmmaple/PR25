package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
import ua.tmmaple.pr25.util.PR25RuntimeException;
import ua.tmmaple.pr25.util.Tweener;

public class Enemy {
    public static final int FLAG_SPRITE_ROTATION = 1 << 1;
    public static final int FLAG_INVINCIBLE = 1 << 2;
    public static final int FLAG_BOSS = 1 << 3;

    GraphicManager.AnmVirtualMachine sprite;
    Vector2 position;
    Enemy parent;
    Array<Enemy> children;
    boolean active;
    TimelineTask timelineTask;
    Task[] asynchTasks;
    int health;
    private Gun[] guns;
    private int flags;

    enum MoveType {
        LINEAR, ORBITAL, NONE
    }
    MoveType moveType;
    private float speed;

    private Vector2 velocity;

    private Vector2 centre;
    private float xRadius;
    private float yRadius;
    private float currentAngle;

    private final Tweener.FloatTweener velocityTweener;
    private final Tweener.FloatTweener angleTweener;
    private final Tweener.FloatTweener xPositionTweener;
    private final Tweener.FloatTweener yPositionTweener;

    public Enemy(int amountOfGuns) {
        this.position = new Vector2();
        this.sprite = GraphicManager.global.new AnmVirtualMachine();
        this.guns = new Gun[amountOfGuns];
        this.velocityTweener = new Tweener.FloatTweener();
        this.angleTweener = new Tweener.FloatTweener();
        this.xPositionTweener = new Tweener.FloatTweener();
        this.yPositionTweener = new Tweener.FloatTweener();
        velocity = new Vector2();
        centre = new Vector2();
        moveType = MoveType.NONE;
        children = new Array<>();
        active = false;
        for (int i = 0; i < guns.length; i++) {
            guns[i] = new Gun(this);
        }
    }

    public void setSprite(Anm source, String sprite){
        this.sprite.loadAnm(source);
        this.sprite.loadScriptAndPlay(sprite);
    }

    public void setSpriteRotation(boolean allow) {
        if (allow)
            flags |= FLAG_SPRITE_ROTATION;
        else
            flags &= ~FLAG_SPRITE_ROTATION;
    }

    public void createChild(TimelineTask task, Task[] asynchTasks, float x, float y) {
        children.add(EnemyManager.global.createEnemy(task, asynchTasks, x, y, this));
    }

    public void changePosition(byte interpolation, float x, float y, short shiftTime){
        xPositionTweener.start(interpolation, position.x, x, shiftTime);
        yPositionTweener.start(interpolation, position.y, y, shiftTime);
    }

    public void changeSpeed(byte interpolation, float speed, short shiftTime){
        velocityTweener.start(interpolation, this.speed, speed, shiftTime);
    }

    public void changeAngle(byte interpolation, float angle, short shiftTime) {
        angleTweener.start(interpolation, velocity.angleRad(), angle, shiftTime);
    }

    public void changeVelocity(byte interpolation, float speed, float angle, short shiftTime) {
        velocityTweener.start(interpolation, this.speed, speed, shiftTime);
        angleTweener.start(interpolation, this.velocity.angleRad(), angle, shiftTime);
        moveType = MoveType.LINEAR;
    }

    public void moveLinearly(){
        this.moveType = MoveType.LINEAR;
    }

    public void rotate(byte interpolation, float angle, short shiftTime){
        float currentAngle = velocity.angleRad();
        angleTweener.start(interpolation, currentAngle, currentAngle+angle, shiftTime);
    }

    public void moveOrbitally(Vector2 centre, float xRadius, float yRadius, float startAngle){
        this.centre = centre;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = startAngle;
        this.moveType = MoveType.ORBITAL;
    }

    public void moveCircularly(Vector2 centre, float radius, float startAngle) {
        moveOrbitally(centre, radius, radius, startAngle);
    }

    public void moveOrbitally(float angle, float xRadius, float yRadius) {
        this.centre.set(position.x- xRadius *(float)Math.cos(angle), position.y - yRadius *(float)Math.sin(angle));
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = angle;
        this.moveType = MoveType.ORBITAL;
    }

    public void moveCircularly(float angle, float radius) {
        moveOrbitally(angle, radius, radius);
    }

    public void stopMovement() {
        this.moveType = MoveType.NONE;
    }

    private void setVelocity(float speed, float angle) {
        this.speed = speed;
        velocity.setAngleRad(angle);
        moveType = MoveType.LINEAR;
    }

    public void setAngle(float angle) {
        this.velocity.setAngleRad(angle);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void initGun(int idx) {
        if (idx < 0 || idx >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", idx));
        guns[idx].init();
    }

    public void setGunBulletType(int gun, Gun.BulletType type) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].bulletType = type;
    }

    public void setGunAim(int gun, Gun.Aim aim) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].aim = aim;
    }

    public void setGunOffset(int gun, Gun.OffsetMode mode, float x, float y) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].offsetMode = mode;
        guns[gun].offset.set(x, y);
    }

    public void setGunFireSound(int gun, String sound) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].fireSound = sound;
    }

    public void setGunCount(int gun, int countA, int countB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].countA = countA;
        guns[gun].countB = countB;
    }

    public void setGunSpeed(int gun, float speedA, float speedB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].speedA = speedA;
        guns[gun].speedB = speedB;
    }

    public void setGunAcceleration(int gun, float accelerationA, float accelerationB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].accelerationA = accelerationA;
        guns[gun].accelerationB = accelerationB;
    }

    public void setGunAngle(int gun, float angleA, float angleB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angleA = angleA;
        guns[gun].angleB = angleB;
    }

    public void setGunAngularSpeed(int gun, float angularSpeedA, float angularSpeedB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angularSpeedA = angularSpeedA;
        guns[gun].angularSpeedB = angularSpeedB;
    }

    public void setGunAngularAcceleration(int gun, float angularAccelerationA, float angularAccelerationB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angularAccelerationA = angularAccelerationA;
        guns[gun].angularAccelerationB = angularAccelerationB;
    }

    public void setGunRadius(int gun, float radiusA, float radiusB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].radiusA = radiusA;
        guns[gun].radiusB = radiusB;
    }

    public void setGunRepeating(int gun, int times) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].repeat = (short) times;
    }

    public void setGunRepeatInterval(int gun, int interval) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].interval = (short) interval;
    }

    public void setGunDelay(int gun, int delay) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].delay = (short) delay;
    }

    public void turnGunOn(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].start();
    }

    public void turnGunOff(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].stop();
    }

    public void adjustGunAimAtPlayer(int gun, Vector2 offset) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAimAtPlayer(offset);
    }


    public void adjustGunAimAt(int gun, Vector2 position) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAimAt(position);
    }

    public void adjustGunVelocity(int gun, float angle, float speed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustVelocity(angle, speed);
    }

    public void adjustGunSpeed(int gun, float speed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustSpeed(speed);
    }

    public void adjustGunAngle(int gun, float angle) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngle(angle);
    }

    public void adjustGunAngularSpeed(int gun, float angularSpeed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngularSpeed(angularSpeed);
    }

    public void adjustGunAngularAcceleration(int gun, float angularAcceleration) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngularAcceleration(angularAcceleration);
    }

    public void adjustGunType(int gun, Gun.BulletType bulletType) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustType(bulletType);
    }

    public void destroyGunBullets(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].destroyAll();
    }

    void update() {
        if (velocityTweener.isRunning()) {
            velocityTweener.update();
            speed = velocityTweener.value();
            velocity.scl(velocityTweener.value()/ speed);
        }
        if (angleTweener.isRunning()) {
            angleTweener.update();
            velocity.set(speed *(float)Math.cos(angleTweener.value()), speed *(float)Math.sin(angleTweener.value()));
        }
        if (xPositionTweener.isRunning()) {
            xPositionTweener.update();
            position.x = xPositionTweener.value();
        }
        if (yPositionTweener.isRunning()) {
            yPositionTweener.update();
            position.y = yPositionTweener.value();
        }
        if(moveType == MoveType.LINEAR) position.add(velocity);
        if(moveType == MoveType.ORBITAL) {
            position.set(centre.x+xRadius*(float)Math.cos(currentAngle), centre.y+yRadius*(float)Math.sin(currentAngle));
            currentAngle += speed /(xRadius+yRadius/2);
        }
        if (health <= 0) {
            active = false;
            for (Enemy child : children) {
                child.active = false;
            }
        }
        if ((flags & FLAG_SPRITE_ROTATION) != 0) {
            if (moveType == MoveType.ORBITAL)
                sprite.angle = currentAngle;
            else
                sprite.angle = velocity.angleRad();
        }
        if (timelineTask.execute(this)) active = false;
        for (Task task : asynchTasks) task.execute(this);
        sprite.execute();
        for (Gun gun: guns) gun.update();
    }

    void draw() {
        sprite.position.set(position);
        sprite.draw();
    }
}
