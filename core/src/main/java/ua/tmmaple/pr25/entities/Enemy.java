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

/**
 * Ворог.
 * @author SkyWarp
 */
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

    /**
     * Встановлює спрайт та анімацію ворогу.
     * @param source ANM-ресурс
     * @param sprite скрипт в ANM-ресурсі
     * @author SkyWarp
     */
    public void setSprite(Anm source, String sprite) {
        this.sprite.loadAnm(source);
        this.sprite.loadScriptAndPlay(sprite);
    }

    /**
     * @param width бажана ширина хітбоксу
     * @param height бажана висота хітбоксу
     * @author SkyWarp
     */
    public void setHitbox(float width, float height) {
        hitbox.setVertices(new float[] { -width * 0.5f, -height * 0.5f, -width * 0.5f, height * 0.5f, width * 0.5f, height * 0.5f, width * 0.5f, -height * 0.5f });
    }

    /**
     * @param allow чи дозволити поворот спрайту
     * @author uwuhasmile
     */
    public void setSpriteRotation(boolean allow) {
        if (allow)
            flags |= FLAG_SPRITE_ROTATION;
        else
            flags &= ~FLAG_SPRITE_ROTATION;
    }

    /**
     * @param deathSound звук смерті цього ворога
     * @author uwuhasmile
     */
    public void setDeathSound(String deathSound) {
        this.deathSound = deathSound;
    }

    /**
     * @param deathVfx візуальний ефект при смерті ворога
     * @author uwuhasmile
     */
    public void setDeathVfx(VfxManager.Vfx deathVfx) {
        if (deathVfx == null)
            this.deathVfx = VfxManager.Vfx.NONE;
        else
            this.deathVfx = deathVfx;
    }

    /**
     * @param scoreDrop кількість дропів очок, які мають випасти при смерті
     * @author SkyWarp
     */
    public void setScoreDrop(int scoreDrop) {
        this.scoreDrop = scoreDrop;
    }

    /**
     * @param powerDrop кількість дропів потужності, які мають випасти при смерті
     * @author SkyWarp
     */
    public void setPowerDrop(int powerDrop) {
        this.powerDrop = powerDrop;
    }

    /**
     * @param scoreDrop кількість дропів очок, які мають випасти при смерті
     * @param powerDrop кількість дропів потужності, які мають випасти при смерті
     * @author SkyWarp
     */
    public void setDrop(int scoreDrop, int powerDrop) {
        this.scoreDrop = scoreDrop;
        this.powerDrop = powerDrop;
    }

    /**
     * Створює дочірнього ворога в позиції відносно цього ворога.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно цього ворога
     * @param y вертикальна позиція відносно цього ворога
     * @param health початкове здоров'я ворога
     * @author SkyWarp
     */
    public void createChildRelative(TimelineTask task, float x, float y, int health) {
        children.add(EnemyManager.global.createEnemy(task, x, y, this, health));
    }

    /**
     * Видаляє дочірнього ворога зі списку
     * @param child дочірній ворог
     * @author SkyWarp
     */
    private void removeChild(Enemy child) {
        children.removeValue(child, true);
    }

    /**
     * Створює дочірнього ворога в позиції відносно центру зверху ігрового поля.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно центру ігрового поля
     * @param y вертикальна позиція відносно верху ігрового поля
     * @param health початкове здоров'я ворога
     */
    public void createChildAbsolute(TimelineTask task, float x, float y, int health) {
        Vector2 abs = absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, this, health));
    }

    /**
     * Додає асинхронне завдання, що виконується паралельно головному.
     * @author SkyWarp
     */
    public void addAsyncTask(Task task) {
        asyncTasks.add(task);
    }

    /**
     * Створює сестринського (тобто дочірнього до батьківського ворога поточному) ворога відносно цього ворога.
     * Якщо батьківський ворог не заданий, то сестринський ворог не створюється.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно цього ворога
     * @param y вертикальна позиція відносно цього ворога
     * @param health початкове здоров'я ворога
     * @author SkyWarp
     */
    public void createSiblingRelative(TimelineTask task, float x, float y, int health) {
        if (parent == null)
            return;
        parent.children.add(EnemyManager.global.createEnemy(task, position.x + x, position.y + y, parent, health));
    }

    /**
     * Створює сестринського (тобто дочірнього до батьківського ворога поточному) ворога відносно центру верху ігрового поля.
     * Якщо батьківський ворог не заданий, то сестринський ворог не створюється.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно центру ігрового поля
     * @param y вертикальна позиція відносно верху ігрового поля
     * @param health початкове здоров'я ворога
     * @author SkyWarp
     */
    public void createSiblingAbsolute(TimelineTask task, float x, float y, int health) {
        if (parent == null)
            return;
        Vector2 abs = parent.absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, parent, health));
    }

    /**
     * Створює сестринського (тобто дочірнього до батьківського ворога поточному) ворога відносно цього ворога.
     * Якщо батьківський ворог не заданий, то сестринський ворог не створюється.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно цього ворога
     * @param y вертикальна позиція відносно цього ворога
     * @author SkyWarp
     */
    public void createSiblingRelative(TimelineTask task, float x, float y) {
        if (parent == null)
            return;
        parent.children.add(EnemyManager.global.createEnemy(task, position.x + x, position.y + y, parent, this.health));
    }

    /**
     * Створює сестринського (тобто дочірнього до батьківського ворога поточному) ворога відносно центру верху ігрового поля.
     * Якщо батьківський ворог не заданий, то сестринський ворог не створюється.
     * @param task завдання, яке ворог має виконати протягом свого життя
     * @param x горизонтальна позиція відносно центру ігрового поля
     * @param y вертикальна позиція відносно верху ігрового поля
     * @author SkyWarp
     */
    public void createSiblingAbsolute(TimelineTask task, float x, float y) {
        if (parent == null)
            return;
        Vector2 abs = parent.absolutePosition();
        children.add(EnemyManager.global.createEnemy(task, x - abs.x, y - abs.y, parent, this.health));
    }

    /**
     * Рухає ворога з поточної до іншої точки протягом певного часу.
     * @param interpolation тип згладжування, з яким змінюється позиція:
     *      <ul>
     *      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *      </ul>
     * @param x горизонтальна позиція відносно батьківського ворога
     * @param y ветикальна позиція відносно батьківського ворога
     * @param ticks кількість тіків, за який має змінитись позиція
     * @author SkyWarp
     */
    public void changePosition(byte interpolation, float x, float y, int ticks){
        positionTweener.start(interpolation, position.cpy(), new Vector2(x, y), (short) ticks);
    }

    /**
     * Змінює лінійну швидкість ворога протягом певного часу.
     * @param interpolation тип згладжування, з яким змінюється швидкість:
     *      <ul>
     *      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *      </ul>
     * @param speed лінійна швидкість ворога
     * @param ticks кількість тіків, за який має змінитись швмдкість
     * @author SkyWarp
     */
    public void changeSpeed(byte interpolation, float speed, int ticks) {
        velocityTweener.start(interpolation, this.speed, speed, (short) ticks);
    }

    /**
     * Змінює напрям ворога протягом певного часу.
     * @param interpolation тип згладжування, з яким змінюється напрям:
     *      <ul>
     *      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *      </ul>
     * @param angle кут напряму в радіанах
     * @param ticks кількість тіків, за який має змінитись швидкість
     * @author SkyWarp
     */
    public void rotate(byte interpolation, float angle, int ticks) {
        angleTweener.start(interpolation, velocity.angleRad(), angle, (short) ticks);
    }

    /**
     * Змінює швидкість ворога в полярних координатах протягом певного часу.
     * @param interpolation тип згладжування, з яким змінюється швидкість:
     *      <ul>
     *      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *      </ul>
     * @param speed лінійна швидкість
     * @param angle кут напряму в радіанах
     * @param ticks кількість тіків, за який має змінитись швидкість
     * @author SkyWarp
     */
    public void changeVelocity(byte interpolation, float speed, float angle, int ticks) {
        velocityTweener.start(interpolation, this.speed, speed, (short) ticks);
        angleTweener.start(interpolation, this.velocity.angleRad(), angle, (short) ticks);
        moveType = MoveType.LINEAR;
    }

    /**
     * Переходить в режим лінійного руху.
     * @author SkyWarp
     */
    public void moveLinearly() {
        this.moveType = MoveType.LINEAR;
    }

    /**
     * Переходить в режим руху по еліпсу.
     * @param centre центр еліпса
     * @param xRadius горизонтальний радіус еліпса
     * @param yRadius вертикальний радіус еліпса
     * @param startAngle початковий кут ворога на еліпсі
     * @author SkyWarp
     */
    public void moveOrbitally(Vector2 centre, float xRadius, float yRadius, float startAngle) {
        this.centre = centre;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = startAngle;
        this.moveType = MoveType.ORBITAL;
    }

    /**
     * Переходить в режим руху по колу.
     * @param centre центр кола
     * @param radius радіус кола
     * @param startAngle початковий кут ворога на колі
     * @author SkyWarp
     */
    public void moveCircularly(Vector2 centre, float radius, float startAngle) {
        moveOrbitally(centre, radius, radius, startAngle);
    }

    /**
     * Переходить в режим руху по еліпсу, визначаючи центр за поточною позицією ворога.
     * @param angle початковий кут
     * @param xRadius горизонтальний радіус еліпса
     * @param yRadius вертикальний радіус еліпса
     * @author SkyWarp
     */
    public void moveOrbitally(float angle, float xRadius, float yRadius) {
        this.centre.set(position.x- xRadius *(float)Math.cos(angle), position.y - yRadius *(float)Math.sin(angle));
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = angle;
        this.moveType = MoveType.ORBITAL;
    }

    /**
     * Переходить в режим руху по колу, визначаючи центр за поточною позицією ворога.
     * @param angle початковий кут
     * @param radius радіус кута
     * @author SkyWarp
     */
    public void moveCircularly(float angle, float radius) {
        moveOrbitally(angle, radius, radius);
    }

    /**
     * Повністю зупиняє рух ворога.
     * @author SkyWarp
     */
    public void stopMovement() {
        this.moveType = MoveType.NONE;
    }

    /**
     * Встановлює швидкість в полярних коориданатах.
     * @param angle кут напряму в радіанах
     * @param speed лінійна швидкість
     * @author uwuhasmile
     */
    public void setVelocity(float angle, float speed) {
        velocity.set(1.0f, 0.0f).setAngleRad(angle).scl(speed);
        this.speed = speed;
        moveType = MoveType.LINEAR;
    }

    /**
     * @param angle кут напряму в радіанах
     * @author uwuhasmile
     */
    public void setAngle(float angle) {
        this.velocity.setAngleRad(angle);
    }

    /**
     * @param speed лінійна швидкість
     * @author uwuhasmile
     */
    public void setSpeed(float speed) {
        velocity.nor().scl(speed);
        this.speed = speed;
    }

    /**
     * Встановлює позицію відносно батьківського ворога.
     * @param x горизонтальна позиція
     * @param y вертикальна позиція
     * @author uwuhasmile
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * @param collision чи має колізія бути увімкнена
     * @author uwuhasmile
     */
    public void setCollision(boolean collision) {
        if (collision)
            flags &= ~FLAG_NO_COLLISION;
        else
            flags |= FLAG_NO_COLLISION;
    }

    /**
     * @param immortal чи має ворог бути безсмертним
     * @author uwuhasmile
     */
    public void setInvincible(boolean immortal) {
        if (immortal)
            flags |= FLAG_INVINCIBLE;
        else
            flags &= ~FLAG_INVINCIBLE;
    }

    /**
     * Робить ворога босом.
     * @param name ім'я ворога, що має показуватись на шкалі здоров'я
     * @author uwuhasmile
     */
    public void makeBoss(String name) {
        flags |= FLAG_BOSS;
        bossName = name;
    }

    /**
     * Ініціалізує "gun" для конфігурації майбутніх куль.
     * @param idx ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void initGun(int idx) {
        if (idx < 0 || idx >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", idx));
        guns[idx].init();
    }

    /**
     * Встановлює тип куль для певного "gun".
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param type тип куль, впливає на спрайт та розмір колізії
     * @author uwuhasmile
     */
    public void setGunBulletType(int gun, Gun.BulletType type) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].bulletType = type;
    }

    /**
     * Встановлює тип прицілювання для певного "gun".
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param aim тип прицілювання, впливає на інтерпретацію значень у "gun". Див. {@link ua.tmmaple.pr25.entities.Gun.Aim}
     * @author uwuhasmile
     */
    public void setGunAim(int gun, Gun.Aim aim) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].aim = aim;
    }

    /**
     * Встановлює позицію gun'у в певному режимі.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param mode режим розміщення, визначає, відносно чого позиція. Див. {@link ua.tmmaple.pr25.entities.Gun.OffsetMode}
     * @param x горизонтальна позиція
     * @param y вертикальна позиція
     * @author uwuhasmile
     */
    public void setGunOffset(int gun, Gun.OffsetMode mode, float x, float y) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].offsetMode = mode;
        guns[gun].offset.set(x, y);
    }

    /**
     * Встановлює звук стрільби кулями цього gun.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param sound назва звуку, з розширенням .ogg або .wav
     * @author uwuhasmile
     */
    public void setGunFireSound(int gun, String sound) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].fireSound = sound;
    }

    /**
     * Встановлює кількість нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunCount(int gun, int countA, int countB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].countA = countA;
        guns[gun].countB = countB;
    }

    /**
     * Встановлює швидкість нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunSpeed(int gun, float speedA, float speedB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].speedA = speedA;
        guns[gun].speedB = speedB;
    }

    /**
     * Встановлює лінійне прискорення нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunAcceleration(int gun, float accelerationA, float accelerationB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].accelerationA = accelerationA;
        guns[gun].accelerationB = accelerationB;
    }

    /**
     * Встановлює напрям нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunAngle(int gun, float angleA, float angleB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angleA = angleA;
        guns[gun].angleB = angleB;
    }

    /**
     * Встановлює швидкість повороту нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunAngularSpeed(int gun, float angularSpeedA, float angularSpeedB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angularSpeedA = angularSpeedA;
        guns[gun].angularSpeedB = angularSpeedB;
    }

    /**
     * Встановлює прискорення повороту нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunAngularAcceleration(int gun, float angularAccelerationA, float angularAccelerationB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].angularAccelerationA = angularAccelerationA;
        guns[gun].angularAccelerationB = angularAccelerationB;
    }

    /**
     * Встановлює радіус стрільби нових куль.
     * Інтерпретується залежно від встановленого {@link ua.tmmaple.pr25.entities.Gun.Aim}.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void setGunRadius(int gun, float radiusA, float radiusB) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].radiusA = radiusA;
        guns[gun].radiusB = radiusB;
    }

    /**
     * Встановлює кількість повторних вистрілів gun'у.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param times кількість повторів, якщо 0, то нескінченно
     * @author uwuhasmile
     */
    public void setGunRepeating(int gun, int times) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].repeat = (short) times;
    }

    /**
     * Встановлює інтервали між вистрілами gun'у.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param interval кількість тіків між пострілами
     * @author uwuhasmile
     */
    public void setGunRepeatInterval(int gun, int interval) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].interval = (short) interval;
    }

    /**
     * Встановлює затримку перед початком стрільби gun'у.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param delay кількість тіків перед першим пострілом
     * @author uwuhasmile
     */
    public void setGunDelay(int gun, int delay) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].delay = (short) delay;
    }

    /**
     * Починає стрільбу з певного gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void turnGunOn(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].start();
    }

    /**
     * Закінчує стрільбу з певного gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void turnGunOff(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].stop();
    }

    /**
     * Спрямовує вже випущені кулі gun'а в сторону гравця.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunAimAtPlayer(int gun, float offsetX, float offsetY) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAimAtPlayer(new Vector2(offsetX, offsetY));
    }

    /**
     * Спрямовує вже випущені кулі gun'а в сторону певної точки.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunAimAt(int gun, Vector2 position) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAimAt(position);
    }

    /**
     * Змінює швидкість вже випущених куль gun'а в полярних координатах.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunVelocity(int gun, float angle, float speed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustVelocity(angle, speed);
    }

    /**
     * Змінює лінійну швидкість вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunSpeed(int gun, float speed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustSpeed(speed);
    }

    /**
     * Змінює напрямок вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunAngle(int gun, float angle) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngle(angle);
    }

    /**
     * Змінює швидкість повороту вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunAngularSpeed(int gun, float angularSpeed) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngularSpeed(angularSpeed);
    }

    /**
     * Змінює прискорення повороту вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunAngularAcceleration(int gun, float angularAcceleration) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustAngularAcceleration(angularAcceleration);
    }

    /**
     * Змінює тип вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void adjustGunBulletType(int gun, Gun.BulletType bulletType) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].adjustType(bulletType);
    }

    /**
     * Знищує всі вже випущені кулі gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @author uwuhasmile
     */
    public void destroyGunBullets(int gun) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].destroyAll(true);
    }

    /**
     * Змінює тип вже випущених куль gun'а.
     * @param gun ідентифікатор gun'а, в межах [0, 6).
     * @param vfx чи використовувати візуальні ефекти
     * @author uwuhasmile
     */
    public void destroyGunBullets(int gun, boolean vfx) {
        if (gun < 0 || gun >= guns.length)
            throw new PR25RuntimeException(String.format("Invalid gun index %d", gun));
        guns[gun].destroyAll(vfx);
    }

    /**
     * @return ім'я ворога як боса, або null, якщо не бос
     * @author uwuhasmile
     */
    public String getName() {
        if ((flags & FLAG_BOSS) == 0) return null;
        if (bossName == null) return null;
        return God.global.getLocalizedString(bossName, true);
    }

    /**
     * Встановлює, чи буде ворог наносити гравцю шкоду за умови перетину.
     * @author uwuhasmile
     */
    public void setIgnorePlayer(boolean ignore) {
        if (ignore)
            flags |= FLAG_IGNORE_PLAYER;
        else
            flags &= ~FLAG_IGNORE_PLAYER;
    }

    /**
     * Скидає дотик, щоб гравець знову міг отримати очки за нього.
     * @author uwuhasmile
     */
    public void resetGraze() {
        flags &= ~FLAG_GRAZED;
    }

    /**
     * Знищує ворога
     * @author uwuhasmile
     */
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
        timelineTask = null;
        asyncTasks.clear();
    }

    /**
     * Оновлює рух та gun'и.
     * @author SkyWarp
     */
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
        if (health == 0 && (flags & FLAG_INVINCIBLE) == 0) {
            if (deathSound != null && !BombManager.global.isInUse())
                Audio.global.playSound(deathSound, 1.0f);
            VfxManager.global.spawn(deathVfx, viewportPosition());
            GameplayStats.global.score(100);
            Hud.global.pickup(viewportPosition(), 100);
            for (int i=0; i<scoreDrop; i++) DropManager.global.spawnStar(viewportPosition().add(14.0f * (i * 0.5f - i), i * 7.0f + 5.0f));
            scoreDrop = 0;
            for (int i=0; i<powerDrop; i++)
                DropManager.global.spawnPower(viewportPosition().add(14.0f * (i * 0.5f - i), i * 7.0f));
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

    /**
     * Наносить шкоду ворогу. Якщо після цього в нього нуль здоров'я та не встановлене безсмертя, то ворог гине.
     * @author uwuhasmile
     */
    public void damage(int damage) {
        if ((flags & FLAG_NO_COLLISION) != 0)
            return;
        VfxManager.global.spawnDust(viewportPosition(), 0.0f, 6, 0.5f, 2.0f);
        if (!canGetHit()) return;
        health -= damage;
        if (health < 0)
            health = 0;
    }

    /**
     * @return чи може ворог взагалі перетинатись з чимось
     * @author uwuhasmile
     */
    public boolean hasCollision() {
        return (flags & FLAG_NO_COLLISION) == 0;
    }

    /**
     * @return чи може гравець нанести шкоду ворогу
     * @author uwuhasmile
     */
    private boolean canGetHit() {
        return (flags & FLAG_NO_COLLISION) == 0 && (flags & FLAG_INVINCIBLE) == 0;
    }

    /**
     * Малює ворога на екрані
     * @author SkyWarp
     */
    void draw() {
        sprite.draw();
    }

    /**
     * @return абсолютна позиція ворога відносно ігрового поля
     * @author uwuhasmile
     */
    public Vector2 absolutePosition() {
        Vector2 result = new Vector2();
        Enemy e = this;
        while (e != null) {
            result.add(e.position);
            e = e.parent;
        }
        return result;
    }

    /**
     * @return абсолютна позиція ворога відносно вікна гри
     * @author uwuhasmile
     */
    public Vector2 viewportPosition() {
        return absolutePosition().add(GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f, GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT);
    }
}
