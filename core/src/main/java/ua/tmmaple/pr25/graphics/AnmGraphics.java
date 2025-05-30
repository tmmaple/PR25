package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.util.Interpolator;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class AnmGraphics {
    public static final int ANM_FLAG_VISIBLE = 1 << 0;
    public static final int ANM_FLAG_FLIP_X = 1 << 1;
    public static final int ANM_FLAG_FLIP_Y = 1 << 2;
    public static final int ANM_FLAG_AUTOROTATE = 1 << 3;
    public static final int ANM_FLAG_TELEPORT = 1 << 4;

    // Interpolator.Vector2Interpolator uvPositionInterpolator;
    // Interpolator.Vector2Interpolator uvScaleInterpolator;
    final Interpolator.ColorInterpolator colorInterpolator;
    final Interpolator.FloatInterpolator alphaInterpolator;
    final Interpolator.Vector2Interpolator positionInterpolator;
    final Interpolator.FloatInterpolator angleInterpolator;
    final Interpolator.Vector2Interpolator scaleInterpolator;

    int flags;

    short time;
    int scriptStart;
    int scriptEnd;
    int pointer;
    byte interrupt;

    int previousPointer;
    short previousTime;

    AnmGraphics parent;

    public Vector2 position;
    public float angle;
    public Vector2 scale;

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

    CharSequence text;

    Anm anm;
    BitmapFont font;
    TextureRegion region;

    public AnmGraphics() {
        position = new Vector2();
        angle = 0.0f;
        scale = new Vector2(1.0f, 1.0f);

        color = new Color();
        anmPosition = new Vector2();
        anmScale = new Vector2();
        anchorOffset = new Vector2();

        colorInterpolator = new Interpolator.ColorInterpolator();
        alphaInterpolator = new Interpolator.FloatInterpolator();
        positionInterpolator = new Interpolator.Vector2Interpolator();
        angleInterpolator = new Interpolator.FloatInterpolator();
        scaleInterpolator = new Interpolator.Vector2Interpolator();
    }

    public void setParent(AnmGraphics parent) {
        this.parent = parent;
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
        originMode = AnmVM.ORIGIN_PARENT;
        anchorMode = AnmVM.ANCHOR_CENTER;
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

    public void loadFont(BitmapFont font) {
        this.font = font;
    }

    public void clearFont() {
        this.font = null;
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
        AnmGraphics o = this;
        boolean result = true;
        while (o != null && result) {
            if ((o.flags & ANM_FLAG_VISIBLE) == 0) result = false;
            else if (o.alpha == 0.0f) result = false;
            else o = o.parent;
        }
        return result;
    }

    Color absoluteColor() {
        AnmGraphics o = this;
        Color result = Color.WHITE;
        while (o != null) {
            result.mul(o.color);
            o = o.parent;
        }
        return result;
    }

    float absoluteAlpha() {
        AnmGraphics o = this;
        float result = 1.0f;
        while (o != null) {
            result *= o.alpha;
            o = o.parent;
        }
        return result;
    }

    Vector2 absolutePosition() {
        AnmGraphics o = this;
        Vector2 result = new Vector2();
        while (o != null) {
            if (o.originMode == AnmVM.ORIGIN_PARENT) result.add(o.position);
            result.add(o.anmPosition);
            o = o.parent;
        }
        return result;
    }

    float absoluteAngle() {
        AnmGraphics o = this;
        float result = 0.0f;
        while (o != null) {
            if ((o.flags & ANM_FLAG_AUTOROTATE) != 0) result += o.angle;
            result += o.anmAngle;
            o = o.parent;
        }
        return result;
    }

    Vector2 absoluteScale() {
        AnmGraphics o = this;
        Vector2 result = new Vector2(1.0f, 1.0f);
        while (o != null) {
            if (o.originMode == AnmVM.ORIGIN_PARENT) result.scl(o.scale);
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
            case AnmVM.ANCHOR_TOP_LEFT: result.set(0.0f, 0.0f); break;
            case AnmVM.ANCHOR_TOP_MIDDLE: result.set(-width * 0.5f, 0.0f); break;
            case AnmVM.ANCHOR_TOP_RIGHT: result.set(-width, 0.0f); break;
            case AnmVM.ANCHOR_MIDDLE_LEFT: result.set(0.0f, -height * 0.5f); break;
            case AnmVM.ANCHOR_CENTER: result.set(-width * 0.5f, -height * 0.5f); break;
            case AnmVM.ANCHOR_MIDDLE_RIGHT: result.set(-width, -height * 0.5f); break;
            case AnmVM.ANCHOR_BOTTOM_LEFT: result.set(0.0f, -height); break;
            case AnmVM.ANCHOR_BOTTOM_MIDDLE: result.set(-width * 0.5f, -height); break;
            case AnmVM.ANCHOR_BOTTOM_RIGHT: result.set(-width, -height); break;
        }
        return result;
    }
}
