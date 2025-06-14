package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.Text;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;
import ua.tmmaple.pr25.ui.MenuItem;

/**
 * Інтерфейс користувача під час активного ігрового процесу.
 * @author uwuhasmile
 */
public final class Hud {
    private static final Vector2 BASE = new Vector2(440.0f, 480.0f - 64.0f);
    private static final float VSPACE = 32.0f;

    public static Hud global;

    private static Flow.FlowNode<Hud> updateNode;
    private static Flow.FlowNode<Hud> drawNode;

    private Anm anm;

    private final Pickup[] pickups;
    private final TextManager.TextSettings pickupSettings;
    private int freePickup;

    private final GraphicManager.AnmVirtualMachine popupVm;
    private final TextManager.TextSettings popupSettings;
    private String popup;

    private final GraphicManager.AnmVirtualMachine pauseOverlayVm;
    private final TextManager.TextSettings pauseTitleSettings;
    private CharSequence pauseTitle;
    private final MenuItem resumeButton;
    private final MenuItem exitButton;

    private final GraphicManager.AnmVirtualMachine bordersVm;
    private final GraphicManager.AnmVirtualMachine bombVm;
    private final TextManager.TextSettings labelSettings;
    private final TextManager.TextSettings valueSettings;

    private long score;
    private long graze;
    private long bombCounter;
    private boolean canBomb;
    private int power;
    private boolean fullPower;

    private int gameState;

    public Hud() {
        pickups = new Pickup[32];
        for (int i = 0; i < pickups.length; ++i)
            pickups[i] = new Pickup();
        pickupSettings = TextManager.global.new TextSettings();
        pickupSettings.hAlign = Align.left;
        pickupSettings.setFont((byte) 5);
        popupVm = GraphicManager.global.new AnmVirtualMachine();
        popupSettings = TextManager.global.new TextSettings();
        popupSettings.hAlign = Align.center;
        popupSettings.parent = popupVm;
        popupSettings.targetWidth = 384;
        popupSettings.setFont((byte) 0);
        pauseOverlayVm = GraphicManager.global.new AnmVirtualMachine();
        pauseTitleSettings = TextManager.global.new TextSettings();
        pauseTitleSettings.hAlign = Align.center;
        pauseTitleSettings.setFont((byte) 2);
        pauseTitleSettings.targetWidth = 384;
        pauseTitleSettings.color.set(Color.YELLOW);
        pauseTitleSettings.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y + 280.0f);
        pauseTitleSettings.parent = pauseOverlayVm;
        resumeButton = new MenuItem()
            .parent(pauseOverlayVm)
            .focusedColor(0xFFFF66)
            .unfocusedColor(0xFFFFFFFF)
            .focusedFont(4)
            .unfocusedFont(4)
            .width(384)
            .align(Align.center)
            .position(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y + 220.0f);
        exitButton = new MenuItem()
            .parent(pauseOverlayVm)
            .focusedColor(0xFFFF66)
            .unfocusedColor(0xFFFFFF)
            .focusedFont(4)
            .unfocusedFont(4)
            .width(384)
            .align(Align.center)
            .position(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y + 190.0f)
            .up(resumeButton);
        resumeButton.down(exitButton);
        bordersVm = GraphicManager.global.new AnmVirtualMachine();
        bombVm = GraphicManager.global.new AnmVirtualMachine();
        labelSettings = TextManager.global.new TextSettings();
        labelSettings.hAlign = Align.left;
        labelSettings.setFont((byte) 4);
        labelSettings.targetWidth = 190;
        labelSettings.wrap = false;
        valueSettings = TextManager.global.new TextSettings();
        valueSettings.hAlign = Align.right;
        valueSettings.setFont((byte) 4);
        valueSettings.targetWidth = 190;
        valueSettings.wrap = false;
    }

    /**
     * Завантажує ресурси.
     * @author uwuhasmile
     */
    public static void load() {
        Assets.global.load(Anm.class, "ui/hud.anm");
    }

    /**
     * Реєструє в список оновлень та відмалювання.
     * @author uwuhasmile
     */
    public static void register() {
        if (updateNode != null) return;
        updateNode = new Flow.FlowNode<>(global, Hud::update, Hud::added, Hud::removed);
        drawNode = new Flow.FlowNode<>(global, Hud::draw);
        Flow.global.addToUpdate(updateNode, 4);
        Flow.global.addToDraw(drawNode, 3);
    }

    /**
     * Видаляє зі списків оновлення та відмалювання.
     * @author uwuhasmile
     */
    public static void shutdown() {
        if (updateNode == null) return;
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    /**
     * Показує виїжджаюче повідомлення.
     * @author uwuhasmile
     */
    public void popup(String text) {
        if (text == null)
            return;
        popupVm.delete();
        popupVm.loadAnm(anm);
        popupVm.loadScriptAndPlay("Popup");
        popupVm.execute();
        popup = text;
    }

    /**
     * Показує кількість при підборі очок.
     * @author uwuhasmile
     */
    public void pickup(Vector2 position, long points) {
        Pickup p = pickups[freePickup++];
        p.position.set(position);
        p.value = points;
        p.ticks = (short) 40;
        while (freePickup < pickups.length && pickups[freePickup].ticks > 0)
            ++freePickup;
        if (freePickup == pickups.length)
            freePickup = 0;
    }

    /**
     * Ініціалізація.
     * @author uwuhasmile
     */
    private int added() {
        for (int i = 0; i < pickups.length; ++i)
            pickups[i].ticks = 0;
        gameState = 0;
        freePickup = 0;
        anm = Assets.global.get(Anm.class, "ui/hud.anm");
        popupVm.delete();
        popupVm.position.set(GameplayManager.VIEWPORT_START_X, GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT - 96.0f);
        resumeButton.anm(anm, "MenuItem");
        exitButton.anm(anm, "MenuItem");
        bordersVm.loadAnm(anm);
        bordersVm.loadScriptAndPlay("Borders");
        bordersVm.position.set(0, 0);
        bombVm.loadAnm(anm);
        bombVm.loadScriptAndPlay("Bomb");
        bombVm.position.set(BASE.cpy().sub(0.0f, VSPACE * 7));
        return 0;
    }

    /**
     * Оновлення.
     * @author uwuhasmile
     */
    private int update() {
        updateInGame();
        if (gameState != GameplayManager.global.getPauseState()) {
            if (gameState == 0)
                showPauseOverlay();
            else
                hidePause();
            gameState = GameplayManager.global.getPauseState();
            if (gameState == 1)
                showPauseMenu();
            else if (gameState == 2)
                showCoinMenu();
        }
        pauseOverlayVm.execute();
        resumeButton.update();
        exitButton.update();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Показує оверлей паузи. Використовується як у, власне, меню паузи, так і в меню продовження після смерті.
     * @author uwuhasmile
     */
    private void showPauseOverlay() {
        pauseOverlayVm.loadAnm(anm);
        pauseOverlayVm.loadScriptAndPlay("PauseOverlay");
    }

    /**
     * Ховає паузу.
     * @author uwuhasmile
     */
    private void hidePause() {
        pauseOverlayVm.interrupt((byte) 1);
        resumeButton.unfocus();
        exitButton.unfocus();
    }

    /**
     * Показує меню паузи.
     * @author uwuhasmile
     */
    private void showPauseMenu() {
        pauseTitle = new Text("pause");
        resumeButton.makeButton(i -> GameplayManager.global.resume()).text(new Text("pauseResume")).focus();
        exitButton.makeButton(i -> God.global.toMainMenu()).text(new Text("pauseExit"));
    }

    /**
     * Показує меню монети (меню продовження гри після смерті).
     * @author uwuhasmile
     */
    private void showCoinMenu() {
        pauseTitle = new Text("coins", GameplayManager.global.getCoins());
        resumeButton.makeButton(i -> { GameplayManager.global.resume(); }).text(new Text("coinsContinue")).focus();
        exitButton.makeButton(null).text(new Text("coinsExit"));
    }

    /**
     * Відмальовує.
     * @author uwuhasmile
     */
    private int draw() {
        for (int i = 0; i < pickups.length; ++i) {
            Pickup p = pickups[i];
            if (p.ticks == 0) continue;
            pickupSettings.position.set(p.position);
            pickupSettings.draw(String.format("+%d", p.value));
        }
        if (popup != null)
            popupSettings.draw(God.global.getLocalizedString(popup, true));
        pauseOverlayVm.draw();
        pauseTitleSettings.draw(pauseTitle);
        resumeButton.draw();
        exitButton.draw();
        valueSettings.color.set(Color.WHITE);
        bordersVm.draw();
        Vector2 pos = BASE.cpy();
        labelSettings.position.set(pos);
        valueSettings.position.set(pos);
        labelSettings.draw(God.global.getLocalizedString("score", false));
        valueSettings.draw(String.format("%08d", score));
        pos.sub(0.0f, VSPACE);
        labelSettings.position.set(pos);
        valueSettings.position.set(pos);
        labelSettings.draw(God.global.getLocalizedString("power", false));
        if (fullPower) {
            valueSettings.color.set(Color.YELLOW);
            valueSettings.draw(God.global.getLocalizedString("powerMax", false));
            valueSettings.color.set(Color.WHITE);
        } else
            valueSettings.draw(String.format("%d", power));
        pos.sub(0.0f, VSPACE * 4);
        labelSettings.position.set(pos);
        valueSettings.position.set(pos);
        labelSettings.draw(God.global.getLocalizedString("graze", false));
        valueSettings.draw(String.format("%d", graze));
        pos.sub(0.0f, VSPACE * 2);
        valueSettings.position.set(pos);
        bombVm.draw();
        if (canBomb) {
            valueSettings.color.set(Color.YELLOW);
            valueSettings.draw(God.global.getLocalizedString("bomb", false));
        } else {
            valueSettings.draw(String.format("%d", bombCounter));
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Оновлює елементи, що показують ігрову інформацію.
     * Не оновлюється, якщо на паузі.
     * @author uwuhasmile
     */
    private void updateInGame() {
        if (!GameplayManager.global.canUpdate())
            return;

        if (God.global.inputState(God.INPUT_UI_DISCARD) == God.INPUT_STATE_PRESSED) {
            GameplayManager.global.pause();
            return;
        }
        for (int i = 0; i < pickups.length; ++i) {
            if (pickups[i].ticks == 0) continue;
            --pickups[i].ticks;
            if (pickups[i].ticks == 0) {
                freePickup = i;
                continue;
            }
            pickups[i].position.add(0.0f, 0.5f);
        }
        popupVm.execute();
        if (score < GameplayStats.global.getScore())
            score += 100;
        if (score > GameplayStats.global.getScore())
            score = GameplayStats.global.getScore();
        if (power != GameplayStats.global.getPower()) {
            if (!fullPower && GameplayStats.global.isFullPower()) {
                fullPower = true;
                popup("fullPower");
                Audio.global.playSound("fullPower.ogg", 1.0f);
            } else if (!GameplayStats.global.isFullPower())
                fullPower = false;
            power = GameplayStats.global.getPower();
        }
        graze = GameplayStats.global.getGraze();
        if (!canBomb && GameplayStats.global.canBomb()) {
            canBomb = true;
            bombVm.interrupt((byte) 2);
        } else if (canBomb && !GameplayStats.global.canBomb()) {
            canBomb = false;
            bombVm.interrupt((byte) 1);
        }
        bombCounter = GameplayStats.global.getBombCounter();
        bordersVm.execute();
        bombVm.execute();
    }

    /**
     * Очищує ресурси.
     * @author uwuhasmile
     */
    private int removed() {
        anm = null;
        bordersVm.delete();
        bombVm.delete();
        pauseOverlayVm.delete();
        popupVm.delete();
        resumeButton.destroy();
        exitButton.destroy();
        Assets.global.unload("ui/hud.anm");
        return 0;
    }

    /**
     * Текст, що показує кількість очок підібраного дропу.
     * @author uwuhasmile
     */
    private static final class Pickup {
        public final Vector2 position;
        public long value;
        public short ticks;

        public Pickup() {
            position = new Vector2();
        }
    }
}
