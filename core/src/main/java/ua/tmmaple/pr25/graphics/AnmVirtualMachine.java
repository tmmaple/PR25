package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.util.Tweener;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class AnmVirtualMachine {
    public static final int ANM_FLAG_VISIBLE = 1 << 0;
    public static final int ANM_FLAG_FLIP_X = 1 << 1;
    public static final int ANM_FLAG_FLIP_Y = 1 << 2;
    public static final int ANM_FLAG_AUTOROTATE = 1 << 3;
    public static final int ANM_FLAG_TELEPORT = 1 << 4;

    /* Системні інструкції */
    public static final byte ANM_OP_NOP = 0x00;
    public static final byte ANM_OP_DELETE = 0x01;
    public static final byte ANM_OP_STOP = 0x02;
    public static final byte ANM_OP_PAUSE = 0x03;
    public static final byte ANM_OP_HIDE_PAUSE = 0x04;
    public static final byte ANM_OP_INTERRUPT = 0x05;
    public static final byte ANM_OP_SLEEP = 0x06;
    public static final byte ANM_OP_RETURN = 0x07;
    public static final byte ANM_OP_JUMP = 0x08;

    /* Інструкції налаштування текстури */
    public static final byte ANM_OP_SOURCE = 0x09;
    public static final byte ANM_OP_UV_POSITION = 0x0A;
    public static final byte ANM_OP_UV_SCALE = 0x0B;
    public static final byte ANM_OP_UV_SCROLLING_X = 0x0C;
    public static final byte ANM_OP_UV_SCROLLING_Y = 0x0D;
    public static final byte ANM_OP_UV_MOVE = 0x0E;
    public static final byte ANM_OP_UV_RESCALE = 0x0F;
    public static final byte ANM_OP_UV_MODE = 0x10;

    /* Інструкції налаштування рендерингу */
    public static final byte ANM_OP_COLOR = 0x11;
    public static final byte ANM_OP_ALPHA = 0x12;
    public static final byte ANM_OP_CHANGE_COLOR = 0x13;
    public static final byte ANM_OP_FADE = 0x14;
    public static final byte ANM_OP_BLENDING = 0x15;
    public static final byte ANM_OP_VISIBLE = 0x16;
    public static final byte ANM_OP_FLIP_X = 0x17;
    public static final byte ANM_OP_FLIP_Y = 0x18;

    /* Інструкції переміщення */
    public static final byte ANM_OP_POSITION = 0x19;
    public static final byte ANM_OP_ANGLE = 0x1A;
    public static final byte ANM_OP_SCALE = 0x1B;
    public static final byte ANM_OP_MOVE = 0x1C;
    public static final byte ANM_OP_ROTATE = 0x1D;
    public static final byte ANM_OP_GROW = 0x1E;
    public static final byte ANM_OP_AUTOROTATE = 0x1F;
    public static final byte ANM_OP_ANGULAR_SPEED = 0x20;
    public static final byte ANM_OP_ORIGIN_MODE = 0x21;
    public static final byte ANM_OP_ANCHOR_MODE = 0x22;
    public static final byte ANM_OP_ANCHOR_OFFSET = 0x23;

    /* Константи */
    public static final byte ANM_UV_NONE = 0;
    public static final byte ANM_UV_REPEAT = 1;
    public static final byte ANM_UV_MIRROR = 2;
    public static final byte ANM_ORIGIN_PARENT = 0;
    public static final byte ANM_ORIGIN_SURFACE = 1;
    public static final byte ANM_ANCHOR_TOP_LEFT = 0;
    public static final byte ANM_ANCHOR_TOP_MIDDLE = 1;
    public static final byte ANM_ANCHOR_TOP_RIGHT = 2;
    public static final byte ANM_ANCHOR_MIDDLE_LEFT = 3;
    public static final byte ANM_ANCHOR_CENTER = 4;
    public static final byte ANM_ANCHOR_MIDDLE_RIGHT = 5;
    public static final byte ANM_ANCHOR_BOTTOM_LEFT = 6;
    public static final byte ANM_ANCHOR_BOTTOM_MIDDLE = 7;
    public static final byte ANM_ANCHOR_BOTTOM_RIGHT = 8;

    // Tweener.Vector2Tweener uvPositionInterpolator;
    // Tweener.Vector2Tweener uvScaleInterpolator;
    final Tweener.ColorTweener colorInterpolator;
    final Tweener.FloatTweener alphaInterpolator;
    final Tweener.Vector2Tweener positionInterpolator;
    final Tweener.FloatTweener angleInterpolator;
    final Tweener.Vector2Tweener scaleInterpolator;

    public AnmVirtualMachine parent;

    public Vector2 position;
    public float angle;
    public Vector2 scale;

    int flags;

    short time;
    int scriptStart;
    int scriptEnd;
    int pointer;
    byte interrupt;

    int previousPointer;
    short previousTime;

    float uScrolling;
    float vScrolling;
    int uvMode;

    Color color;
    float alpha;

    Vector2 anmPosition;
    float anmAngle;
    Vector2 anmScale;
    float angularSpeed;
    int originMode;
    int anchorMode;
    Vector2 anchorOffset;

    Vector2 lastAbsolutePosition;
    float lastAbsoluteAngle;
    Vector2 lastAbsoluteScale;

    Anm anm;
    TextureRegion region;

    public AnmVirtualMachine() {
        position = new Vector2();
        angle = 0.0f;
        scale = new Vector2(1.0f, 1.0f);

        color = new Color();
        anmPosition = new Vector2();
        anmScale = new Vector2();
        anchorOffset = new Vector2();

        colorInterpolator = new Tweener.ColorTweener();
        alphaInterpolator = new Tweener.FloatTweener();
        positionInterpolator = new Tweener.Vector2Tweener();
        angleInterpolator = new Tweener.FloatTweener();
        scaleInterpolator = new Tweener.Vector2Tweener();
    }

    public void loadAnm(Anm anm) {
        this.anm = anm;
    }

    public void loadScript(String script) {
        if (anm == null) throw new PR25RuntimeException("ANM is null, no scripts");
        Anm.AnmScript anmScript = anm.getScript(script);
        time = -1;
        scriptStart = anmScript.start;
        scriptEnd = anmScript.end;
        pointer = anmScript.start;
        interrupt = 0;
        previousPointer = scriptStart;
        previousTime = -1;

        flags = ANM_FLAG_VISIBLE | ANM_FLAG_TELEPORT;
        uScrolling = 0.0f;
        vScrolling = 0.0f;
        color.set(Color.WHITE);
        alpha = 1.0f;
        anmPosition.set(0.0f, 0.0f);
        anmAngle = 0.0f;
        anmScale.set(1.0f, 1.0f);
        angularSpeed = 0.0f;
        originMode = ANM_ORIGIN_PARENT;
        anchorMode = ANM_ANCHOR_CENTER;
        anchorOffset.set(0.0f, 0.0f);

        lastAbsolutePosition = new Vector2(position);
        lastAbsoluteAngle = 0.0f;
        lastAbsoluteScale = new Vector2(scale);
    }

    public void loadSource(int id) {
        if (anm == null) throw new PR25RuntimeException("ANM is null, no sources");
        region = anm.getSource(id);
    }

    public void loadTexture(Texture texture) {
        this.region = new TextureRegion(texture);
    }

    public void loadTexture(TextureRegion region) {
        this.region = new TextureRegion(region);
    }

    public void interrupt(byte interrupt) {
        this.interrupt = interrupt;
    }

    public void resetInterrupt() {
        interrupt = 0;
    }

    public void teleport() {
        flags |= ANM_FLAG_TELEPORT;
    }

    public void delete() {
        // uvPositionInterpolator.end();
        // uvScaleInterpolator.end();
        colorInterpolator.end();
        alphaInterpolator.end();
        positionInterpolator.end();
        angleInterpolator.end();
        scaleInterpolator.end();
        flags = 0;
        time = -1;
        scriptStart = 0;
        scriptEnd = 0;
        pointer = 0;
        interrupt = 0;
        anm = null;
        region = null;
    }

    boolean absoluteVisible() {
        AnmVirtualMachine o = this;
        boolean result = true;
        while (o != null && result) {
            if ((o.flags & ANM_FLAG_VISIBLE) == 0) result = false;
            else if (o.alpha == 0.0f) result = false;
            else o = o.parent;
        }
        return result;
    }

    Color absoluteColor() {
        AnmVirtualMachine o = this;
        Color result = Color.WHITE;
        while (o != null) {
            result.mul(o.color);
            o = o.parent;
        }
        return result;
    }

    float absoluteAlpha() {
        AnmVirtualMachine o = this;
        float result = 1.0f;
        while (o != null) {
            result *= o.alpha;
            o = o.parent;
        }
        return result;
    }

    Vector2 absolutePosition() {
        AnmVirtualMachine o = this;
        Vector2 result = new Vector2();
        while (o != null) {
            if (o.originMode == ANM_ORIGIN_PARENT) result.add(o.position);
            result.add(o.anmPosition);
            o = o.parent;
        }
        return result;
    }

    float absoluteAngle() {
        AnmVirtualMachine o = this;
        float result = 0.0f;
        while (o != null) {
            if ((o.flags & ANM_FLAG_AUTOROTATE) != 0) result += o.angle;
            result += o.anmAngle;
            o = o.parent;
        }
        return result;
    }

    Vector2 absoluteScale() {
        AnmVirtualMachine o = this;
        Vector2 result = new Vector2(1.0f, 1.0f);
        while (o != null) {
            if (o.originMode == ANM_ORIGIN_PARENT) result.scl(o.scale);
            result.scl(o.anmScale);
            o = o.parent;
        }
        return result;
    }

    Vector2 absoluteAnchorOffset() {
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        Vector2 result = anchorOffset.cpy();
        switch (anchorMode) {
            case ANM_ANCHOR_TOP_LEFT: result.set(0.0f, 0.0f); break;
            case ANM_ANCHOR_TOP_MIDDLE: result.set(-width * 0.5f, 0.0f); break;
            case ANM_ANCHOR_TOP_RIGHT: result.set(-width, 0.0f); break;
            case ANM_ANCHOR_MIDDLE_LEFT: result.set(0.0f, -height * 0.5f); break;
            case ANM_ANCHOR_CENTER: result.set(-width * 0.5f, -height * 0.5f); break;
            case ANM_ANCHOR_MIDDLE_RIGHT: result.set(-width, -height * 0.5f); break;
            case ANM_ANCHOR_BOTTOM_LEFT: result.set(0.0f, -height); break;
            case ANM_ANCHOR_BOTTOM_MIDDLE: result.set(-width * 0.5f, -height); break;
            case ANM_ANCHOR_BOTTOM_RIGHT: result.set(-width, -height); break;
        }
        return result;
    }
}
