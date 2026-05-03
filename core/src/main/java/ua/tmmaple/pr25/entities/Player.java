package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

/**
 * Клас об'єкту гравця, зроблений як окрема система у Flow.
 * @author afiliushkin
 */
public class Player {
    private enum MovementDirection {
        NONE,
        UP,
        UP_LEFT,
        LEFT,
        DOWN_LEFT,
        DOWN,
        DOWN_RIGHT,
        RIGHT,
        UP_RIGHT,
    }

    private static final float Y_SPAWN_OFFSET = 32.0f;
    private static final float HORIZONTAL_BOUNDARIES = 24.0f;
    private static final float VERTICAL_BOUNDARIES = 26.0f;

    private static final float ORTHOGONAL_SPEED = 5.0f;
    private static final float DIAGONAL_SPEED = 3.53553390593f;
    private static final float FOCUS_SPEED_MULTIPLIER = 0.25f;

    private static final short GAME_OVER_COOLDOWN = 30;
    private static final short RESPAWN_INVINCIBILITY_COOLDOWN = 90;
    private static final short DEATH_BOMB_COOLDOWN = 12;

    private static final short GRAZE_SOUND_COOLDOWN = 5;

    private static final Vector2 UNFOCUSED_ORB_OFFSET = new Vector2(24.0f, 0.0f);
    private static final Vector2 FOCUSED_ORB_OFFSET = new Vector2(12.0f, 24.0f);

    public static Player global;

    public final Vector2 position;
    private final GraphicManager.AnmVirtualMachine parentVM;
    private final GraphicManager.AnmVirtualMachine spriteVM;
    private final GraphicManager.AnmVirtualMachine grazeVM;
    private final GraphicManager.AnmVirtualMachine rightOrbVM;
    private final GraphicManager.AnmVirtualMachine leftOrbVM;

    public final Polygon hitbox;
    public final Polygon grazeBox;

    private final Vector2 orbOffset;

    private short gameOverCooldown;
    private short invincibilityCooldown;
    private short deathBombCooldown;

    private int smallBulletCooldown;
    private int bigBulletCooldown;

    private int grazeSoundCooldown;

    private MovementDirection direction;

    private final RandomXS128 random;

    private static Flow.FlowNode<Player> updateNode;
    private static Flow.FlowNode<Player> drawNode;

    /**
     * Завантажує ресурси перед створенням.
     * @author afiliushkin
     */
    public static void load() {
        Assets.global.load(Anm.class,"game/plr.anm");
    }

    /**
     * Реєструє до списку оновлень та відмалювань.
     * @author afiliushkin
     */
    public static int register() {
        if (updateNode != null)
            return 0;
        updateNode = new Flow.FlowNode<>(global, Player::update, Player::added, Player::removed);
        drawNode = new Flow.FlowNode<>(global, Player::draw);
        Flow.global.addToUpdate(updateNode, 10);
        Flow.global.addToDraw(drawNode, 7);
        return 0;
    }

    /**
     * Видаляє зі списку оновлень та відмалювань.
     * @author afiliushkin
     */
    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    public Player() {
        position = new Vector2();
        parentVM = GraphicManager.global.new AnmVirtualMachine();
        spriteVM = GraphicManager.global.new AnmVirtualMachine();
        spriteVM.parent = parentVM;
        grazeVM = GraphicManager.global.new AnmVirtualMachine();
        rightOrbVM = GraphicManager.global.new AnmVirtualMachine();
        rightOrbVM.parent = spriteVM;
        leftOrbVM = GraphicManager.global.new AnmVirtualMachine();
        leftOrbVM.parent = spriteVM;
        orbOffset = new Vector2();
        hitbox = new Polygon(new float[] { -2.0f, -2.0f, -2.0f, 2.0f, 2.0f, 2.0f, 2.0f, -2.0f });
        grazeBox = new Polygon(new float[] { -16.0f, -16.0f, -16.0f, 16.0f, 16.0f, 16.0f, 16.0f, -16.0f });
        random = new RandomXS128();
    }

    /**
     * Пробує нарахувати дотик при відповідному перетині гравця з кулями або ворогами.
     * Не зараховується у випадку, коли гровець в стані DeathBombing або помирає.
     * @author afiliushkin
     */
    public void graze() {
        if (isDeathBombing() || gameOverCooldown > 0)
            return;
        grazeVM.interrupt((byte) 1);
        GameplayStats.global.graze();
        if (grazeSoundCooldown == 0) {
            Audio.global.playSound("plrGraze.ogg", 1.0f);
            grazeSoundCooldown = GRAZE_SOUND_COOLDOWN;
        }
    }

    /**
     * Встановлює напрям руху гравця та змінює анімацію.
     * @author afiliushkin
     */
    private void setDirection(MovementDirection direction) {
        if (this.direction != direction) {
            this.direction = direction;
            switch (direction) {
                case NONE:
                case DOWN:
                case UP:
                    spriteVM.interrupt((byte) 1);
                    break;
                case UP_LEFT:
                case LEFT:
                case DOWN_LEFT:
                    spriteVM.interrupt((byte) 2);
                    break;
                case DOWN_RIGHT:
                case RIGHT:
                case UP_RIGHT:
                    spriteVM.interrupt((byte) 3);
                    break;
            }
        }
    }

    /**
     * Наносить шкоду гравцю, якщо в того нема тимчасового безсмертя.
     * Якщо є бомба, то запускає таймер для deathbombing, але звук все одно програється.
     * @author afiliushkin
     */
    public void damage() {
        if (gameOverCooldown > 0 || invincibilityCooldown > 0)
            return;
        Audio.global.playSound("plrDeath.ogg", 1.0f);
        if (GameplayStats.global.canBomb())
            deathBombCooldown = DEATH_BOMB_COOLDOWN;
        else
            kill();
    }

    /**
     * Вбиває гравця та запускає маленький таймер перед переходом до екрану програшу.
     * @author afiliushkin
     */
    private void kill() {
        VfxManager.global.spawnPlayerDeath(position);
        BulletManager.global.destroyEnemyBulletsInRadius(position, 640.0f);
        gameOverCooldown = GAME_OVER_COOLDOWN;
    }

    /**
     * Респавнить гравця на початковій позиції, із тимчасовим безсмертям.
     * @author afiliushkin
     */
    public void respawn() {
        makeInvincible(RESPAWN_INVINCIBILITY_COOLDOWN);
        position.set(GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f, GameplayManager.VIEWPORT_START_Y + Y_SPAWN_OFFSET);
        orbOffset.set(UNFOCUSED_ORB_OFFSET);
        deathBombCooldown = (short) 0;
    }

    /**
     * Тимчасово робить безсмертним.
     * @author afiliushkin
     */
    public void makeInvincible(short ticks) {
        invincibilityCooldown = ticks;
        parentVM.interrupt((byte) 2);
    }

    /**
     * Головна логіка оновлення гравця, виконується 60 разів на секунду.
     * Тут виконується рух та стрільба, а також рахунок таймерів.
     * @author afiliushkin
     */
    private int update() {
        if (grazeSoundCooldown > 0)
            --grazeSoundCooldown;
        if (!GameplayManager.global.canUpdate())
            return Flow.FLOW_RESULT_CONTINUE;
        if (gameOverCooldown > 0) {
            --gameOverCooldown;
            if (gameOverCooldown == 0) {
                GameplayManager.global.gameOver();
            }
            return Flow.FLOW_RESULT_CONTINUE;
        }
        boolean bomb = God.global.inputState(God.INPUT_BOMB) == God.INPUT_STATE_JUST_PRESSED && canFire();
        if (bomb && GameplayStats.global.canBomb()) {
            if (deathBombCooldown > 0)
                deathBombCooldown = 0;
            BombManager.global.use();
            GameplayStats.global.bombUsed();
        }
        if (deathBombCooldown > 0) {
            --deathBombCooldown;
            if (deathBombCooldown == 0)
                kill();
            return Flow.FLOW_RESULT_CONTINUE;
        }
        boolean up = God.global.inputState(God.INPUT_MOVE_UP) == God.INPUT_STATE_PRESSED;
        boolean left = God.global.inputState(God.INPUT_MOVE_LEFT) == God.INPUT_STATE_PRESSED;
        boolean down = God.global.inputState(God.INPUT_MOVE_DOWN) == God.INPUT_STATE_PRESSED;
        boolean right = God.global.inputState(God.INPUT_MOVE_RIGHT) == God.INPUT_STATE_PRESSED;
        boolean focus = God.global.inputState(God.INPUT_FOCUS) == God.INPUT_STATE_PRESSED;
        boolean shooting = God.global.inputState(God.INPUT_FIRE) == God.INPUT_STATE_PRESSED;
        if ((!up && !left && !down && !right) || (up && down || right && left))
            setDirection(MovementDirection.NONE);
        else if (up) {
            if (left)
                setDirection(MovementDirection.UP_LEFT);
            else if (right)
                setDirection(MovementDirection.UP_RIGHT);
            else
                setDirection(MovementDirection.UP);
        } else if (down) {
            if (left)
                setDirection(MovementDirection.DOWN_LEFT);
            else if (right)
                setDirection(MovementDirection.DOWN_RIGHT);
            else
                setDirection(MovementDirection.DOWN);
        } else if (left)
            setDirection(MovementDirection.LEFT);
        else if (right)
            setDirection(MovementDirection.RIGHT);
        switch (direction) {
            case UP:
                position.y += ORTHOGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f);
                break;
            case UP_LEFT:
                position.add(-DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f), DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f));
                break;
            case LEFT:
                position.x -= ORTHOGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f);
                break;
            case DOWN_LEFT:
                position.add(-DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f), -DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f));
                break;
            case DOWN:
                position.y -= ORTHOGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f);
                break;
            case DOWN_RIGHT:
                position.add(DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f), -DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f));
                break;
            case RIGHT:
                position.x += ORTHOGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f);
                break;
            case UP_RIGHT:
                position.add(DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f), DIAGONAL_SPEED * (focus ? FOCUS_SPEED_MULTIPLIER : 1.0f));
                break;
        }
        position.x = MathUtils.clamp(position.x,
            GameplayManager.VIEWPORT_START_X + HORIZONTAL_BOUNDARIES,
            GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH - HORIZONTAL_BOUNDARIES);
        position.y = MathUtils.clamp(position.y,
            GameplayManager.VIEWPORT_START_Y + VERTICAL_BOUNDARIES,
            GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT - VERTICAL_BOUNDARIES);
        if (focus) {
            if (orbOffset.dst2(FOCUSED_ORB_OFFSET) < 0.01f)
                orbOffset.set(FOCUSED_ORB_OFFSET);
            else
                orbOffset.lerp(FOCUSED_ORB_OFFSET, 0.25f);
        } else if (orbOffset.dst2(UNFOCUSED_ORB_OFFSET) < 0.01f)
            orbOffset.set(UNFOCUSED_ORB_OFFSET);
        else
            orbOffset.lerp(UNFOCUSED_ORB_OFFSET, 0.25f);
        int power = GameplayStats.global.getPower();
        if (shooting && canFire()) {
            if (smallBulletCooldown == 0)
                smallBulletCooldown = 4;
            if (bigBulletCooldown <= 0) {
                if (power < 20)
                    bigBulletCooldown = -1;
                else if (power < 40)
                    bigBulletCooldown = 60;
                else if (power < 60)
                    bigBulletCooldown = 40;
                else if (power < 100)
                    bigBulletCooldown = 20;
                else
                    bigBulletCooldown = 15;
            }
        } else
            bigBulletCooldown = 0;
        if (smallBulletCooldown > 0) {
            --smallBulletCooldown;
            if (smallBulletCooldown == 0) {
                Audio.global.playSound("plrFire.ogg", 1.0f);
                BulletManager.global.createSmallPlayerBullet(position.cpy().add(0.0f, 16.0f));
                if (power >= 10) {
                    BulletManager.global.createSmallPlayerBullet(position.cpy().add(orbOffset));
                    BulletManager.global.createSmallPlayerBullet(position.cpy().add(new Vector2(-orbOffset.x, orbOffset.y)));
                }
            }
        }
        if (bigBulletCooldown > 0) {
            --bigBulletCooldown;
            if (bigBulletCooldown == 0) {
                if (power < 60) {
                    BulletManager.global.createBigPlayerBullet(position.cpy().add(orbOffset));
                    BulletManager.global.createBigPlayerBullet(position.cpy().add(new Vector2(-orbOffset.x, orbOffset.y)));
                } else {
                    for (int i = 0; i < 3; ++i) {
                        float randomX = random.nextFloat(-15.0f, 15.0f);
                        float randomY = random.nextFloat(-15.0f, 15.0f);
                        BulletManager.global.createBigPlayerBullet(position.cpy().add(orbOffset.cpy().add(randomX, randomY)));
                        randomX = random.nextFloat(-15.0f, 15.0f);
                        randomY = random.nextFloat(-15.0f, 15.0f);
                        BulletManager.global.createBigPlayerBullet(position.cpy().add(new Vector2(-orbOffset.x, orbOffset.y).add(randomX, randomY)));
                    }
                }
            }
        }
        if (invincibilityCooldown > 0) {
            --invincibilityCooldown;
            if (invincibilityCooldown == 0)
                parentVM.interrupt((byte) 1);
        }
        hitbox.setPosition(position.x, position.y);
        grazeBox.setPosition(position.x, position.y);
        parentVM.execute();
        spriteVM.execute();
        grazeVM.execute();
        leftOrbVM.execute();
        rightOrbVM.execute();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * @return true, якщо гравець має змогу застосувати бомбу після отримання шкоди.
     * @author afiliushkin
     */
    public boolean isDeathBombing() {
        return deathBombCooldown > 0;
    }

    /**
     * Відмальовує всі спрайти гравця на екран.
     * @author afiliushkin
     */
    private int draw() {
        if (gameOverCooldown > 0)
            return Flow.FLOW_RESULT_CONTINUE;
        spriteVM.position.set(position);
        grazeVM.position.set(position);
        leftOrbVM.position.set(new Vector2(-orbOffset.x, orbOffset.y));
        rightOrbVM.position.set(new Vector2(orbOffset.x, orbOffset.y));
        if (GameplayStats.global.getPower() >= 10) {
            leftOrbVM.draw();
            rightOrbVM.draw();
        }
        spriteVM.draw();
        grazeVM.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Ініціалізація після додавання до списків.
     * @author afiliushkin
     */
    private int added() {
        Anm anm = Assets.global.get(Anm.class, "game/plr.anm");
        parentVM.loadAnm(anm);
        parentVM.loadScriptAndPlay("Player");
        spriteVM.loadAnm(anm);
        spriteVM.loadScriptAndPlay("PlayerSprite");
        grazeVM.loadAnm(anm);
        grazeVM.loadScriptAndPlay("GrazeBox");
        leftOrbVM.loadAnm(anm);
        leftOrbVM.loadScriptAndPlay("Orb");
        rightOrbVM.loadAnm(anm);
        rightOrbVM.loadScriptAndPlay("Orb");
        gameOverCooldown = 0;
        deathBombCooldown = 0;
        invincibilityCooldown = 0;
        smallBulletCooldown = 0;
        bigBulletCooldown = 0;
        return 0;
    }

    /**
     * Видалення ресурсів після видалення зі списків.
     * @author afiliushkin
     */
    private int removed() {
        parentVM.delete();
        spriteVM.delete();
        grazeVM.delete();
        leftOrbVM.delete();
        rightOrbVM.delete();
        Assets.global.unload("game/plr.anm");
        return 0;
    }

    /**
     * @return чи може гравець стріляти
     * @author afiliushkin
     */
    private boolean canFire() {
        return !BombManager.global.isInUse() && StageManager.global.isActive();
    }
}
