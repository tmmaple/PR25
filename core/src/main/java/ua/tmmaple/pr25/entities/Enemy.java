package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
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
    private float velocity;

    private Vector2 linearMoveVector;

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
        linearMoveVector = new Vector2();
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

    public void changeVelocity(byte interpolation, float velocity, short shiftTime){
        velocityTweener.start(interpolation, this.velocity, velocity, shiftTime);
    }

    public void changeAngle(byte interpolation, float angle, short shiftTime){
        angleTweener.start(interpolation, linearMoveVector.angleRad(), angle, shiftTime);
    }

    public void setLinearMove(byte interpolation, float velocity, float angle, short shiftTime){
        velocityTweener.start(interpolation, this.velocity, velocity, shiftTime);
        angleTweener.start(interpolation, linearMoveVector.angleRad(), angle, shiftTime);
        moveType = MoveType.LINEAR;
    }

    public void setLinearMove(){
        this.moveType = MoveType.LINEAR;
    }

    public void rotate(byte interpolation, float angle, short shiftTime){
        float currentAngle = linearMoveVector.angleRad();
        angleTweener.start(interpolation, currentAngle, currentAngle+angle, shiftTime);
    }

    public void setOrbitalMove(Vector2 centre, float xRadius, float yRadius, float startAngle){
        this.centre = centre;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = startAngle;
        this.moveType = MoveType.ORBITAL;
    }

    public void setRoundMove(Vector2 centre, float radius, float startAngle) {
        setOrbitalMove(centre, radius, radius, startAngle);
    }

    public void setOrbitalMove(float angle, float xRadius, float yRadius){
        this.centre.set(position.x- xRadius *(float)Math.cos(angle), position.y - yRadius *(float)Math.sin(angle));
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = angle;
        this.moveType = MoveType.ORBITAL;
    }

    public void setRoundMove(float angle, float radius) {
        setOrbitalMove(angle, radius, radius);
    }

    public void stopMovement() {
        this.moveType = MoveType.NONE;
    }

    public void setAngle(float angle) {
        this.linearMoveVector.setAngleRad(angle);
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    void update() {
        if (velocityTweener.isRunning()) {
            velocityTweener.update();
            velocity = velocityTweener.value();
            linearMoveVector.scl(velocityTweener.value()/velocity);
        }
        if (angleTweener.isRunning()) {
            angleTweener.update();
            linearMoveVector.set(velocity*(float)Math.cos(angleTweener.value()), velocity*(float)Math.sin(angleTweener.value()));
        }
        if (xPositionTweener.isRunning()) {
            xPositionTweener.update();
            position.x = xPositionTweener.value();
        }
        if (yPositionTweener.isRunning()) {
            yPositionTweener.update();
            position.y = yPositionTweener.value();
        }
        if(moveType == MoveType.LINEAR) position.add(linearMoveVector);
        if(moveType == MoveType.ORBITAL) {
            position.set(centre.x+xRadius*(float)Math.cos(currentAngle), centre.y+yRadius*(float)Math.sin(currentAngle));
            currentAngle += velocity/(xRadius+yRadius/2);
        }
        if (health <= 0) {
            active = false;
            for (Enemy child : children) {
                child.active = false;
            }
        }
        if ((flags & FLAG_SPRITE_ROTATION) != 0)
            sprite.angle = linearMoveVector.angleRad();
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
