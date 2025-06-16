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
 * Екран результатів. Показується або після проходження гри, або після програшу.
 * @author uwuhasmile
 */
public final class Results {
    private static final float VSPACING = 26.0f;

    private enum ResultsState {
        INTRO,
        WAITING,
        EXITING,
    }

    public static Results global;

    private static Flow.FlowNode<Results> updateNode;
    private static Flow.FlowNode<Results> drawNode;

    private Anm anm;

    private final GraphicManager.AnmVirtualMachine backgroundVm;

    private final TextManager.TextSettings titleSettings;
    private final Text titleText;
    private final GraphicManager.AnmVirtualMachine statsVm;
    private final TextManager.TextSettings statNameSettings;
    private final TextManager.TextSettings statValueSettings;
    private final Text scoreText;
    private final Text bombsText;
    private final Text coinsText;
    private final GraphicManager.AnmVirtualMachine backToMenuVm;
    private final MenuItem backToMenuButton;

    private long score;
    private int bombs;
    private int coins;

    private ResultsState state;
    private short ticksLeft;

    public Results() {
        backgroundVm = GraphicManager.global.new AnmVirtualMachine();
        titleSettings = TextManager.global.new TextSettings();
        titleSettings.setFont((byte) 0);
        titleSettings.color.set(Color.YELLOW);
        titleSettings.position.set(60.0f, 400.0f);
        titleSettings.targetWidth = 400;
        titleText = new Text("result");
        statsVm = GraphicManager.global.new AnmVirtualMachine();
        statNameSettings = TextManager.global.new TextSettings();
        statValueSettings = TextManager.global.new TextSettings();
        statNameSettings.parent = statsVm;
        statNameSettings.setFont((byte) 4);
        statNameSettings.color.set(Color.WHITE);
        statNameSettings.hAlign = Align.left;
        statNameSettings.targetWidth = 380;
        statValueSettings.parent = statsVm;
        statValueSettings.setFont((byte) 4);
        statValueSettings.color.set(Color.YELLOW);
        statValueSettings.hAlign = Align.right;
        statValueSettings.targetWidth = 380;
        scoreText = new Text("resultScore");
        bombsText = new Text("resultsBombs");
        coinsText = new Text("resultCoins");
        backToMenuVm = GraphicManager.global.new AnmVirtualMachine();
        backToMenuButton = new MenuItem().parent(backToMenuVm)
            .focusedFont((byte) 4).unfocusedFont((byte) 4)
            .focusedColor(Color.rgb888(Color.PINK)).unfocusedColor(Color.rgb888(Color.PINK))
            .text(new Text("resultsBackToMenu"))
            .align(Align.center)
            .width(500);
    }

    /**
     * Завантажує ресурси.
     * @author uwuhasmile
     */
    public static void load() {
        Assets.global.load(Anm.class, "ui/results.anm");
    }

    /**
     * Реєструє в списки оновлення та малювання.
     * @author uwuhasmile
     */
    public static void register() {
        if (updateNode != null) return;
        updateNode = new Flow.FlowNode<>(global, Results::update, Results::added, Results::removed);
        drawNode = new Flow.FlowNode<>(global, Results::draw);
        Flow.global.addToUpdate(updateNode, 3);
        Flow.global.addToDraw(drawNode, 1);
    }

    /**
     * Видаляє зі списків оновлення та малювання.
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
     * Ініціалізує віртуальні машини та текст.
     * @author uwuhasmile
     */
    private int added() {
        Audio.global.stopMusic();
        anm = Assets.global.get(Anm.class, "ui/results.anm");
        backgroundVm.loadAnm(anm);
        backgroundVm.loadScriptAndPlay("Background");
        statsVm.loadAnm(anm);
        statsVm.loadScriptAndPlay("Stats");
        backToMenuVm.loadAnm(anm);
        backToMenuVm.loadScriptAndPlay("BackToMenu");
        backToMenuButton.makeButton(this::onBackToMenu);
        score = GameplayStats.global.getScore();
        bombs = GameplayStats.global.getBombsUsed();
        coins = GameplayStats.global.getCoinsUsed();
        state = ResultsState.INTRO;
        ticksLeft = 25;
        return 0;
    }

    /**
     * Оновлює віртуальні машини та ввід від гравця.
     * @author uwuhasmile
     */
    private int update() {
        if (state == ResultsState.INTRO) {
            if (ticksLeft > 0) {
                --ticksLeft;
                if (ticksLeft == 0) {
                    state = ResultsState.WAITING;
                    backToMenuButton.focus();
                }
            }
        } else if (state == ResultsState.EXITING) {
            if (ticksLeft > 0) {
                --ticksLeft;
                if (ticksLeft == 0) {
                    state = null;
                    God.global.toMainMenu();
                }
            }
        }
        backToMenuButton.update();
        backgroundVm.execute();
        statsVm.execute();
        backToMenuVm.execute();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Малює результати на екран.
     * @author uwuhasmile
     */
    private int draw() {
        backgroundVm.draw();
        titleSettings.draw(titleText);
        Vector2 pos = new Vector2();
        statNameSettings.position.set(pos);
        statNameSettings.draw(scoreText);
        statValueSettings.position.set(pos);
        statValueSettings.draw(String.format("%08d", score));
        pos.y -= VSPACING;
        statNameSettings.position.set(pos);
        statNameSettings.draw(bombsText);
        statValueSettings.position.set(pos);
        statValueSettings.draw(String.valueOf(bombs));
        pos.y -= VSPACING;
        statNameSettings.position.set(pos);
        statNameSettings.draw(coinsText);
        statValueSettings.position.set(pos);
        statValueSettings.draw(String.valueOf(coins));
        backToMenuButton.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Повертає в меню за натиском кнопки.
     * @author uwuhasmile
     */
    private void onBackToMenu(MenuItem button) {
        backToMenuVm.interrupt((byte) 1);
        state = ResultsState.EXITING;
        ticksLeft = 20;
        backToMenuButton.unfocus();
    }

    /**
     * Очищує ресурси та видаляє віртуальні машини.
     * @author uwuhasmile
     */
    private int removed() {
        backgroundVm.delete();
        statsVm.delete();
        backToMenuVm.delete();
        backToMenuButton.destroy();
        anm = null;
        Assets.global.unload("ui/results.anm");
        return 0;
    }
}
