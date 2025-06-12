package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;

public final class Hud {
    private static final Vector2 BASE = new Vector2(440.0f, 480.0f - 64.0f);
    private static final float VSPACE = 32.0f;

    public static Hud global;

    private static Flow.FlowNode<Hud> updateNode;
    private static Flow.FlowNode<Hud> drawNode;

    private final GraphicManager.AnmVirtualMachine bordersVm;
    private final GraphicManager.AnmVirtualMachine bombVm;
    private final TextManager.TextSettings labelSettings;
    private final TextManager.TextSettings valueSettings;

    private final GraphicManager.AnmVirtualMachine pauseOverlayVm;

    private long score;
    private long graze;
    private long bombCounter;
    private boolean canBomb;
    private int power;

    public Hud() {
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
        pauseOverlayVm = GraphicManager.global.new AnmVirtualMachine();
    }

    public static void load() {
        Assets.global.load(Anm.class, "ui/hud.anm");
    }

    public static void register() {
        if (updateNode != null) return;
        updateNode = new Flow.FlowNode<>(global, Hud::update, Hud::added, Hud::removed);
        drawNode = new Flow.FlowNode<>(global, Hud::draw);
        Flow.global.addToUpdate(updateNode, 4);
        Flow.global.addToDraw(drawNode, 3);
    }

    public static void shutdown() {
        if (updateNode == null) return;
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    private int added() {
        Anm anm = Assets.global.get(Anm.class, "ui/hud.anm");
        bordersVm.loadAnm(anm);
        bordersVm.loadScriptAndPlay("Borders");
        bordersVm.position.set(0, 0);
        bombVm.loadAnm(anm);
        bombVm.loadScriptAndPlay("Bomb");
        bombVm.position.set(BASE.cpy().sub(0.0f, VSPACE * 7));
        return 0;
    }

    private int update() {
        updateInGame();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int draw() {
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

    private void updateInGame() {
        if (!GameplayManager.global.canUpdate())
            return;

        if (score < GameplayStats.global.getScore())
            score += 100;
        if (score > GameplayStats.global.getScore())
            score = GameplayStats.global.getScore();
        graze = GameplayStats.global.getGraze();
        if (!canBomb && GameplayStats.global.canBomb()) {
            canBomb = true;
            bombVm.interrupt((byte) 2);
        } else if (canBomb && !GameplayStats.global.canBomb()) {
            canBomb = false;
            bombVm.interrupt((byte) 1);
        }
        if (power != GameplayStats.global.getPower())
            power = GameplayStats.global.getPower();
        bombCounter = GameplayStats.global.getBombCounter();
        bordersVm.execute();
        bombVm.execute();
    }

    private int removed() {
        bordersVm.delete();
        bombVm.delete();
        return 0;
    }
}
