package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.util.Tweener;

/**
 * Фон під час ігрового процесу
 * @author uwuhasmile
 */
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

    /**
     * Реєструє в списки оновлення та відмалювання.
     * @author uwuhasmile
     */
    public static int register() {
        updateNode = new Flow.FlowNode<>(global, Background::update);
        drawNode = new Flow.FlowNode<>(global, Background::draw);
        Flow.global.addToUpdate(updateNode, 5);
        Flow.global.addToDraw(drawNode, 9);
        return 0;
    }

    /**
     * Видаляє зі списків оновлення та відмалювання.
     * @author uwuhasmile
     */
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


    /**
     * Трясе камеру.
     * @param power сила трясіння. Накопичується.
     * @author uwuhasmile
     */
    public void shakeCamera(int power) {
        cameraShakePower += power;
    }

    /**
     * Встановлює позицію камери.
     * @author uwuhasmile
     */
    public void setCameraPosition(float cameraPosition) {
        this.cameraPosition = cameraPosition;
        vm.teleport();
    }

    /**
     * Рухає камеру з поточної до іншої точки протягом певного проміжку часу.
     * @param ticks проміжок часу, за який камера дійде до точки
     * @param to точка, до якої має рухатись камера
     * @param interpolation тип згладжування, з яким рухається камера:
     *                      <ul>
     *                      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *                      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *                      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *                      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *                      </ul>
     * @author uwuhasmile
     */
    public void moveCamera(short ticks, float to, byte interpolation) {
        positionTweener.start(interpolation, cameraPosition, to, ticks);
    }

    /**
     * Змінює швидкість камери за певний проміжок часу.
     * @param ticks проміжок часу, за який камера має змінити швидкість
     * @param to швидкість, якої має досягти камера
     * @param interpolation тип згладжування, з яким змінюється швидкість:
     *                      <ul>
     *                      <li><code>INTERPOLATION_LINEAR</code> - лінійний рух</li>
     *                      <li><code>INTERPOLATION_EASE_IN</code> - починає повільно, закінчує швидко</li>
     *                      <li><code>INTERPOLATION_EASE_OUT</code> - починає швидко, закінчує повільно</li>
     *                      <li><code>INTERPOLATION_EASE_IN_OUT</code> - починає повільно, прискорюється, потім сповільнюється</li>
     *                      </ul>
     * @author uwuhasmile
     */
    public void accelerateCamera(short ticks, float to, byte interpolation) {
        velocityTweener.start(interpolation, cameraVelocity, to, ticks);
    }

    /**
     * Встановлює межі, в яких камера може рухатись.
     * @param min найнижча точка
     * @param max найвища точка
     * @author uwuhasmile
     */
    public void setCameraLimits(float min, float max) {
        setCameraLimits(min, max, false);
    }

    /**
     * Встановлює межі, в яких камера може рухатись.
     * @param min найнижча точка
     * @param max найвища точка
     * @param loop визначає, чи має камера телепортуватись до протилежної межі до досягненої
     * @author uwuhasmile
     */
    public void setCameraLimits(float min, float max, boolean loop) {
        cameraMinPos = min;
        cameraMaxPos = max;
        this.loop = loop;
    }

    /**
     * Скидає межі камери.
     * @author uwuhasmile
     */
    public void resetCameraLimits() {
        cameraMinPos = 0.0f;
        cameraMaxPos = Float.MAX_VALUE;
        loop = false;
    }

    /**
     * Завантажує зображення фону.
     * @param anm ANM-ресурс з анімаціями та зображенням
     * @param scriptName скрипт анімації
     */
    public void load(Anm anm, String scriptName) {
        vm.loadAnm(anm);
        vm.loadScriptAndPlay(scriptName);
        cameraPosition = 0.0f;
        resetCameraLimits();
        cameraShakePower = 0;
        cameraShakeOffset.set(0.0f, 0.0f);
    }

    /**
     * Вивантажує поточний фон та зупиняє всі твінери.
     * @author uwuhasmile
     */
    public void unload() {
        positionTweener.end();
        velocityTweener.end();
        cameraShakePower = 0;
        vm.delete();
    }

    /**
     * Оновлює фон, рухаючи його та оновлюючи трясіння камери, інтерполяції, і т.д.
     * @author uwuhasmile
     */
    private int update() {
        if (!GameplayManager.global.canUpdate() || Player.global.isDeathBombing())
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
        cameraShakeOffset.set((1.0f - (float) Math.random() * 2.0f) * cameraShakePower * 4.0f, (1.0f - (float) Math.random() * 2.0f) * cameraShakePower * 4.0f);
        if (cameraShakePower > 0) {
            if (cameraShakeCooldown == 0) {
                cameraShakeCooldown = 2;
                --cameraShakePower;
            } else
                --cameraShakeCooldown;
        }
        vm.position.y = -cameraPosition;
        vm.position.x = GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f;
        vm.position.add(cameraShakeOffset);
        vm.execute();
        return Flow.FLOW_RESULT_CONTINUE;
    }

    /**
     * Відмальовує фон на екрані.
     * @author uwuhasmile
     */
    private int draw() {
        vm.draw();
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
