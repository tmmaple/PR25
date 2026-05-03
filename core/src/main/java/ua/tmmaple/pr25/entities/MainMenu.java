package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.utils.Align;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.Text;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.audio.Bgm;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;
import ua.tmmaple.pr25.stages.Stage01;
import ua.tmmaple.pr25.ui.MenuItem;

/**
 * Головне меню гри, працює як окрема система.
 * @author afiliushkin
 */
public final class MainMenu {
    private static final float VSPACING = 26.0f;

    private enum MainMenuState {
        INTRO,
        MENU,
        EXITING,
    }

    public static MainMenu global;

    private static Flow.FlowNode<MainMenu> updateNode;
    private static Flow.FlowNode<MainMenu> drawNode;

    private Anm anm;
    private final GraphicManager.AnmVirtualMachine backgroundVm;
    private final GraphicManager.AnmVirtualMachine logoVm;
    private final GraphicManager.AnmVirtualMachine leafVm;
    private final GraphicManager.AnmVirtualMachine menuVm;

    private final TextManager.TextSettings hiScoreTextSettings;
    private Text hiScoreText;
    private final MenuItem startButton;
    private final MenuItem languageSelector;
    private final MenuItem windowSelector;
    private final MenuItem sfxVolumeSlider;
    private final MenuItem musicVolumeSlider;
    private final MenuItem exitButton;

    private MainMenuState state;
    private int next;
    private short ticksLeft;

    public MainMenu() {
        backgroundVm = GraphicManager.global.new AnmVirtualMachine();
        logoVm = GraphicManager.global.new AnmVirtualMachine();
        leafVm = GraphicManager.global.new AnmVirtualMachine();
        menuVm = GraphicManager.global.new AnmVirtualMachine();
        hiScoreTextSettings = TextManager.global.new TextSettings();
        hiScoreTextSettings.parent = logoVm;
        hiScoreTextSettings.position.set(-320.0f, -130.0f);
        hiScoreTextSettings.hAlign = Align.center;
        hiScoreTextSettings.targetWidth = 640.0f;
        hiScoreTextSettings.setFont((byte) 4);
        startButton = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, 0.0f)
            .focusedColor(0xFF6E22)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640)
            .text(new Text("start"));
        languageSelector = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, -VSPACING)
            .focusedColor(0xFFFF00)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640)
            .text(new Text("language"));
        windowSelector = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, -VSPACING * 2.0f)
            .focusedColor(0xFFFF00)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640);
        sfxVolumeSlider = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, -VSPACING * 3.0f)
            .focusedColor(0xFFFF00)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640);
        musicVolumeSlider = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, -VSPACING * 4.0f)
            .focusedColor(0xFFFF00)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640);
        exitButton = new MenuItem()
            .parent(menuVm)
            .align(Align.center)
            .position(0.0f, -VSPACING * 5.0f)
            .focusedColor(0xFF5555)
            .unfocusedColor(0xFFFFFF)
            .focusedFont((byte) 4)
            .unfocusedFont((byte) 4)
            .width(640)
            .text(new Text("exit"));
        startButton.down(languageSelector);
        languageSelector.up(startButton);
        languageSelector.down(windowSelector);
        windowSelector.up(languageSelector);
        windowSelector.down(sfxVolumeSlider);
        sfxVolumeSlider.up(windowSelector);
        sfxVolumeSlider.down(musicVolumeSlider);
        musicVolumeSlider.up(sfxVolumeSlider);
        musicVolumeSlider.down(exitButton);
        exitButton.up(musicVolumeSlider);
    }

    /**
     * Завантаження ресурсів.
     * @author afiliushkin
     */
    public static void load() {
        Assets.global.load(Anm.class, "ui/mainMenu.anm");
        Assets.global.load(Bgm.class, "bgm/theme.bgm");
    }

    /**
     * Реєструє у списку оновлення та відмалювання.
     * @author afiliushkin
     */
    public static void register() {
        if (updateNode != null) return;
        updateNode = new Flow.FlowNode<>(global, MainMenu::update, MainMenu::added, MainMenu::removed);
        drawNode = new Flow.FlowNode<>(global, MainMenu::draw);
        Flow.global.addToUpdate(updateNode, 3);
        Flow.global.addToDraw(drawNode, 1);
    }

    /**
     * Видаляє зі списку оновлення та відмалювання.
     * @author afiliushkin
     */
    public static void shutdown() {
        if (updateNode == null) return;
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    /**
     * Ініціалізує всі графічні елементи та кнопки.
     * @author afiliushkin
     */
    private int added() {
        next = 0;
        anm = Assets.global.get(Anm.class, "ui/mainMenu.anm");
        startButton.makeButton(this::onStart).anm(anm, "MenuItem");
        languageSelector.makeIntSlider(this::onLanguageChange, God.global.language(), 1, 0, 1, true).anm(anm, "MenuItem");
        windowSelector.makeIntSlider(this::onScalingChange, God.global.windowScale(), 1, 0, 6, true).anm(anm, "MenuItem");
        sfxVolumeSlider.makeFloatSlider(this::onSfxVolumeChange, God.global.sfxVolume(), 0.1f, 0.0f, 1.0f, false).anm(anm, "MenuItem");
        musicVolumeSlider.makeFloatSlider(this::onMusicVolumeChange, God.global.musicVolume(), 0.1f, 0.0f, 1.0f, false).anm(anm, "MenuItem");
        exitButton.makeButton(this::onExit).anm(anm, "MenuItem");
        backgroundVm.loadAnm(anm);
        backgroundVm.loadScriptAndPlay("Background");
        logoVm.loadAnm(anm);
        logoVm.loadScriptAndPlay("Logo");
        leafVm.loadAnm(anm);
        leafVm.loadScriptAndPlay("Leaf");
        menuVm.loadAnm(anm);
        menuVm.loadScriptAndPlay("Menu");
        hiScoreText = new Text("menuHiScore", String.format("%08d", God.global.hiScore()));
        state = MainMenuState.INTRO;
        ticksLeft = 100;
        return 0;
    }

    /**
     * Оновлює графічні елементи та кнопки.
     * @author afiliushkin
     */
    private int update() {
        menuVm.execute();
        startButton.update();
        languageSelector.update();
        windowSelector.update();
        sfxVolumeSlider.update();
        musicVolumeSlider.update();
        exitButton.update();
        backgroundVm.execute();
        logoVm.execute();
        leafVm.execute();
        if (state == MainMenuState.INTRO) {
            if (ticksLeft > 0) {
                --ticksLeft;
                if (ticksLeft == 0) {
                    Audio.global.playMusic("bgm/theme.bgm", true);
                    state = MainMenuState.MENU;
                    startButton.focus();
                }
            }
        } else if (state == MainMenuState.EXITING) {
            if (ticksLeft > 0) {
                --ticksLeft;
                if (ticksLeft == 0) {
                    if (next == 0)
                        God.global.startGame(new Stage01());
                    else if (next == 1)
                        God.global.exit();
                }
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Малює головне меню на екран.
     * @author afiliushkin
     */
    private int draw() {
        backgroundVm.draw();
        logoVm.draw();
        leafVm.draw();
        startButton.draw();
        languageSelector.draw();
        windowSelector.draw();
        sfxVolumeSlider.draw();
        musicVolumeSlider.draw();
        exitButton.draw();
        hiScoreTextSettings.draw(hiScoreText);
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Запускає гру з першої стадії через кнопку.
     * @author afiliushkin
     */
    private void onStart(MenuItem button) {
        next(0);
    }

    /**
     * Змінює мову гри через слайдер.
     * @author afiliushkin
     */
    private void onLanguageChange(MenuItem slider, int value) {
        God.global.setLanguage(value);
    }

    /**
     * Змінює розмір вікна гри через слайдер.
     * @author afiliushkin
     */
    private void onScalingChange(MenuItem slider, int value) {
        God.global.setWindowMode(value);
        slider.text(new Text("window", value));
    }

    /**
     * Змінює гучність звуків через слайдер.
     * @author afiliushkin
     */
    private void onSfxVolumeChange(MenuItem slider, float value) {
        God.global.setSfxVolume(value);
        slider.text(new Text("sfx", value));
    }

    /**
     * Змінює гучність музики через слайдер.
     * @author afiliushkin
     */
    private void onMusicVolumeChange(MenuItem slider, float value) {
        God.global.setMusicVolume(value);
        slider.text(new Text("music", value));
    }

    /**
     * Виходить з гри через кнопку.
     * @author afiliushkin
     */
    private void onExit(MenuItem button) {
        next(1);
    }

    /**
     * Запускає процес переходу до іншого екрану, а також запускає відповідні анімації.
     * @author afiliushkin
     */
    private void next(int next) {
        startButton.unfocus();
        languageSelector.unfocus();
        windowSelector.unfocus();
        sfxVolumeSlider.unfocus();
        musicVolumeSlider.unfocus();
        exitButton.unfocus();
        menuVm.interrupt((byte) 1);
        backgroundVm.interrupt((byte) 1);
        logoVm.interrupt((byte) 1);
        leafVm.interrupt((byte) 1);
        this.next = next;
        ticksLeft = 40;
        state = MainMenuState.EXITING;
        Audio.global.fadeMusic(2.0f);
    }

    /**
     * Видаляє ресурси.
     * @author afiliushkin
     */
    private int removed() {
        anm = null;
        startButton.destroy();
        languageSelector.destroy();
        windowSelector.destroy();
        sfxVolumeSlider.destroy();
        musicVolumeSlider.destroy();
        exitButton.destroy();
        menuVm.delete();
        backgroundVm.delete();
        logoVm.delete();
        leafVm.delete();
        Assets.global.unload("ui/mainMenu.anm");
        Assets.global.unload("bgm/theme.bgm");
        return 0;
    }
}
