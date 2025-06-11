package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.util.Tweener;

public final class Background {
    public static Background global;

    private static Flow.FlowNode<Background> updateNode;
    private static Flow.FlowNode<Background> drawNode;

    private float cameraPosition;
    public float cameraVelocity;

    private float cameraMinPos;
    private float cameraMaxPos;
    private boolean loop;

    private final Tweener.FloatTweener positionTweener;
    private final Tweener.FloatTweener velocityTweener;

    private int cameraShakePower;
    private int cameraShakeCooldown;
    private final Vector2 cameraShakeOffset;

    private final GraphicManager.AnmVirtualMachine vm;

    public static int register() {
        updateNode = new Flow.FlowNode<>(global, Background::update);
        drawNode = new Flow.FlowNode<>(global, Background::draw);
        Flow.global.addToUpdate(updateNode, 7);
        Flow.global.addToDraw(drawNode, 5);
        return 0;
    }

    public static void shutdown() {
        Flow.global.cut(updateNode);
        Flow.global.cut(drawNode);
        updateNode = null;
        drawNode = null;
    }

    public Background() {
        vm = GraphicManager.global.new AnmVirtualMachine();
        positionTweener = new Tweener.FloatTweener();
        velocityTweener = new Tweener.FloatTweener();
        cameraShakeOffset = new Vector2();
    }

    public void load(Anm anm, String scriptName) {
        vm.loadAnm(anm);
        vm.loadScriptAndPlay(scriptName);
        cameraPosition = 0.0f;
        resetCameraLimits();
        cameraShakePower = 0;
        cameraShakeOffset.set(0.0f, 0.0f);
    }

    public void unload() {
        positionTweener.end();
        velocityTweener.end();
        vm.delete();
    }

    private int update() {
        if (!GameplayManager.global.canUpdate() || Player.global.deathbombing())
            return Flow.FLOW_RESULT_CONTINUE;
        cameraPosition += cameraVelocity;
        if (!loop)
            cameraPosition = MathUtils.clamp(cameraPosition, cameraMinPos, cameraMaxPos);
        else
            setCameraPosition((cameraPosition - cameraMinPos) % (cameraMaxPos - cameraMinPos) + cameraMinPos);
        if (positionTweener.isRunning()) {
            positionTweener.update();
            cameraPosition = positionTweener.value();
        }
        if (velocityTweener.isRunning()) {
            velocityTweener.update();
            cameraVelocity = velocityTweener.value();
        }
        if (cameraShakePower > 0) {
            if (cameraShakeCooldown == 0) {
                cameraShakeCooldown = 2;
                --cameraShakePower;
                cameraShakeOffset.set((1.0f - (float) Math.random() * 2.0f) * cameraShakePower * 4.0f, (1.0f - (float) Math.random() * 2.0f) * cameraShakePower * 4.0f);
            } else
                --cameraShakeCooldown;
        }
        vm.position.y = -cameraPosition;
        vm.position.x = GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f;
        vm.position.add(cameraShakeOffset);
        vm.execute();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    public void shakeCamera(int power) {
        cameraShakePower += power;
    }

    public void setCameraPosition(float cameraPosition) {
        this.cameraPosition = cameraPosition;
        vm.teleport();
    }

    public void moveCamera(short ticks, float to, byte interpolation) {
        positionTweener.start(interpolation, cameraPosition, to, ticks);
    }

    public void accelerateCamera(short ticks, float to, byte interpolation) {
        velocityTweener.start(interpolation, cameraVelocity, to, ticks);
    }

    public void setCameraLimits(float min, float max) {
        setCameraLimits(min, max, false);
    }

    public void setCameraLimits(float min, float max, boolean loop) {
        cameraMinPos = min;
        cameraMaxPos = max;
        this.loop = loop;
    }

    public void resetCameraLimits() {
        cameraMinPos = 0.0f;
        cameraMaxPos = Float.MAX_VALUE;
        loop = false;
    }

    private int draw() {
        vm.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
