package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
import ua.tmmaple.pr25.util.PR25RuntimeException;
import ua.tmmaple.pr25.util.Tweener;

public class Enemy {
    public static final int FLAG_SPRITE_ROTATION = 1 << 1;
    public static final int FLAG_INVINCIBLE = 1 << 2;
    public static final int FLAG_IGNORE_PLAYER = 1 << 3;
    public static final int FLAG_NO_COLLISION = 1 << 4;
    public static final int FLAG_BOSS = 1 << 5;
    public static final int FLAG_GRAZED = 1 << 6;

    GraphicManager.AnmVirtualMachine sprite;
    private String deathSound;
    private VfxManager.Vfx deathVfx;
    private String bossName;
    Vector2 position;
    Enemy parent;
    Array<Enemy> children;
    boolean active;
    TimelineTask timelineTask;
    Array<Task> asyncTasks;
    int health;
    public final Polygon hitbox;
    private final Gun[] guns;
    private int flags;
    private int scoreDrop;
    private int powerDrop;

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
    private final Tweener.Vector2Tweener positionTweener;

    public Enemy(int amountOfGuns) {
        this.position = new Vector2();
        this.sprite = GraphicManager.global.new AnmVirtualMachine();
        this.guns = new Gun[amountOfGuns];
        this.velocityTweener = new Tweener.FloatTweener();
        this.angleTweener = new Tweener.FloatTweener();
        this.positionTweener = new Tweener.Vector2Tweener();
        hitbox = new Polygon(new float[8]);
        velocity = new Vector2();
        centre = new Vector2();
        moveType = MoveType.NONE;
        children = new Array<>();
        asyncTasks = new Array<>();
        active = false;
        for (int i = 0; i < guns.length; i++) {
            guns[i] = new Gun(this);
        }
    }

    public void setSprite(Anm source, String sprite) {
        this.sprite.loadAnm(source);
        this.sprite.loadScriptAndPlay(sprite);
    }

    public void setHitbox(float width, float height) {
        hitbox.setVertices(new float[] { -width * 0.5f, -height * 0.5f, -width * 0.5f, height * 0.5f, width * 0.5f, height * 0.5f, width * 0.5f, -height * 0.5f });
    }

    public void setSpriteRotation(boolean allow) {
        if (allow)
            flags |= FLAG_SPRITE_ROTATION;
        else
            flags &= ~FLAG_SPRITE_ROTATION;
    }

    public void setDeathSound(String deathSound) {
        this.deathSound = deathSound;
    }

    public void setDeathVfx(VfxManager.Vfx deathVfx) {
        if (deathVfx == null)
            this.deathVfx = VfxManager.Vfx.NONE;
        else
            this.deathVfx = deathVfx;
    }

    public void setScoreDrop(int scoreDrop) {
        this.scoreDrop = scoreDrop;
    }

    public void setPowerDrop(int powerDrop) {
        this.powerDrop = powerDrop;
    }

    public void setDrop(int scoreDrop, int powerDrop) {
        this.scoreDrop = scoreDrop;
        this.powerDrop = powerDrop;
    }

    public void createChildRelative(TimelineTask task, float x, float y, int health) {
        children.add(EnemyManager.global.createEnemy(task, x, y, this, health));
    }

    private void removeChild(Enemy child) {
        children.removeValue(child, true);
    }

    public void createChildAbsolute(TimelineTask task, float x, float y, int health) {
        Vector2 abs = absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, this, health));
    }

    public void addAsyncTask(Task task) {
        asyncTasks.add(task);
    }

    public void createSiblingRelative(TimelineTask task, float x, float y, int health) {
        if (parent == null)
            return;
        parent.children.add(EnemyManager.global.createEnemy(task, position.x + x, position.y + y, parent, health));
    }

    public void createSiblingAbsolute(TimelineTask task, float x, float y, int health) {
        if (parent == null)
            return;
        Vector2 abs = parent.absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, parent, health));
    }

    public void createSiblingRelative(TimelineTask task, float x, float y) {
        if (parent == null)
            return;
        parent.children.add(EnemyManager.global.createEnemy(task, position.x + x, position.y + y, parent, this.health));
    }

    public void createSiblingAbsolute(TimelineTask task, float x, float y) {
        if (parent == null)
            return;
        Vector2 abs = parent.absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, parent, this.health));
    }

    public void changePosition(byte interpolation, float x, float y, int ticks){
        positionTweener.start(interpolation, position.cpy(), new Vector2(x, y), (short) ticks);
    }

    public void changeSpeed(byte interpolation, float speed, int ticks) {
        velocityTweener.start(interpolation, this.speed, speed, (short) ticks);
    }

    public void changeAngle(byte interpolation, float angle, int ticks) {
        angleTweener.start(interpolation, velocity.angleRad(), angle, (short) ticks);
    }

    public void changeVelocity(byte interpolation, float speed, float angle, int ticks) {
        velocityTweener.start(interpolation, this.speed, speed, (short) ticks);
        angleTweener.start(interpolation, this.velocity.angleRad(), angle, (short) ticks);
        moveType = MoveType.LINEAR;
    }

    public void moveLinearly() {
        this.moveType = MoveType.LINEAR;
    }

    public void rotate(byte interpolation, float angle, int ticks) {
        float currentAngle = velocity.angleRad();
        angleTweener.start(interpolation, currentAngle, currentAngle+angle, (short) ticks);
    }

    public void moveOrbitally(Vector2 centre, float xRadius, float yRadius, float startAngle) {
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

    public void setVelocity(float angle, float speed) {
        velocity.set(1.0f, 0.0f).setAngleRad(angle).scl(speed);
        this.speed = speed;
        moveType = MoveType.LINEAR;
    }

    public void setAngle(float angle) {
        this.velocity.setAngleRad(angle);
    }

    public void setSpeed(float speed) {
        velocity.nor().scl(speed);
        this.speed = speed;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setCollision(boolean collision) {
        if (collision)
            flags &= ~FLAG_NO_COLLISION;
        else
            flags |= FLAG_NO_COLLISION;
    }

    public void setInvincible(boolean immortal) {
        if (immortal)
            flags |= FLAG_INVINCIBLE;
        else
            flags &= ~FLAG_INVINCIBLE;
    }

    public void makeBoss(String name) {
        flags |= FLAG_BOSS;
        bossName = name;
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

    public void adjustGunAimAtPlayer(int gun, float offsetX, float offsetY) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAimAtPlayer(new Vector2(offsetX, offsetY));
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

    public void adjustGunBulletType(int gun, Gun.BulletType bulletType) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustType(bulletType);
    }

    public void destroyGunBullets(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].destroyAll(true);
    }

    public void destroyGunBullets(int gun, boolean vfx) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].destroyAll(vfx);
    }

    public String getName() {
        if ((flags & FLAG_BOSS) == 0) return null;
        if (bossName == null) return null;
        return God.global.getLocalizedString(bossName, true);
    }

    public void setIgnorePlayer(boolean ignore) {
        if (ignore)
            flags |= FLAG_IGNORE_PLAYER;
        else
            flags &= ~FLAG_IGNORE_PLAYER;
    }

    public void resetGraze() {
        flags &= ~FLAG_GRAZED;
    }

    public void destroy() {
        positionTweener.end();
        angleTweener.end();
        velocityTweener.end();
        active = false;
        sprite.delete();
        if (parent != null)
            parent.removeChild(this);
        for (Enemy child : children)
            child.destroy();
        asyncTasks.clear();
    }

    void update() {
        if (velocityTweener.isRunning()) {
            velocityTweener.update();
            speed = velocityTweener.value();
            velocity.nor().scl(speed);
        }
        if (angleTweener.isRunning()) {
            angleTweener.update();
            velocity.setAngleRad(angleTweener.value());
        }
        if (positionTweener.isRunning()) {
            positionTweener.update();
            position.set(positionTweener.value());
        }
        if(moveType == MoveType.LINEAR) position.add(velocity);
        if(moveType == MoveType.ORBITAL) {
            position.set(centre.x+xRadius*(float)Math.cos(currentAngle), centre.y+yRadius*(float)Math.sin(currentAngle));
            currentAngle += speed /(xRadius+yRadius/2);
        }
        if (health <= 0) {
            if (deathSound != null && !BombManager.global.isInUse())
                Audio.global.playSound(deathSound, 1.0f);
            VfxManager.global.spawn(deathVfx, viewportPosition());
            GameplayStats.global.score(100);
            Hud.global.pickup(viewportPosition(), 100);
            for (int i=0; i<scoreDrop; i++) DropManager.global.createScoreDrop(viewportPosition().add(14.0f * (i * 0.5f - i), i * 7.0f + 5.0f));
            scoreDrop = 0;
            for (int i=0; i<powerDrop; i++)
                DropManager.global.createPowerDrop(viewportPosition().add(14.0f * (i * 0.5f - i), i * 7.0f));
            powerDrop = 0;
            destroy();
            return;
        }
        if ((flags & FLAG_SPRITE_ROTATION) != 0) {
            if (moveType == MoveType.ORBITAL)
                sprite.angle = currentAngle + MathUtils.HALF_PI;
            else
                sprite.angle = velocity.angleRad();
        }
        if (timelineTask.execute(this) && asyncTasks.size == 0 && children.size == 0) {
            destroy();
            return;
        }
        for (Task task : asyncTasks) if (task.execute(this)) asyncTasks.removeValue(task, true);
        if (hasCollision()) {
            hitbox.setPosition(viewportPosition().x, viewportPosition().y);
            if ((flags & FLAG_IGNORE_PLAYER) == 0) {
                if (Intersector.intersectPolygons(Player.global.hitbox,  hitbox, null)) {
                    Player.global.damage();
                } else if (Intersector.intersectPolygons(Player.global.grazeBox, hitbox, null) && (flags & FLAG_GRAZED) == 0) {
                    flags |= FLAG_GRAZED;
                    Player.global.graze();
                }
            }
        }
        sprite.position.set(viewportPosition());
        sprite.execute();
        for (Gun gun: guns) gun.update();
    }

    public void damage(int damage) {
        if ((flags & FLAG_NO_COLLISION) != 0)
            return;
        VfxManager.global.spawnDust(viewportPosition(), 0.0f, 6, 0.5f, 2.0f);
        if (!canGetHit()) return;
        health -= damage;
        if (health < 0)
            damage = 0;
    }

    public boolean hasCollision() {
        return (flags & FLAG_NO_COLLISION) == 0;
    }

    private boolean canGetHit() {
        return (flags & FLAG_NO_COLLISION) == 0 && (flags & FLAG_INVINCIBLE) == 0;
    }

    void draw() {
        sprite.draw();
    }

    public Vector2 absolutePosition() {
        Vector2 result = new Vector2();
        Enemy e = this;
        while (e != null) {
            result.add(e.position);
            e = e.parent;
        }
        return result;
    }

    public Vector2 viewportPosition() {
        return absolutePosition().add(GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f, GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT);
    }
}
