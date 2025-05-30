package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class AnmVM {
    /* Системні інструкції */
    public static final byte OP_NOP = 0x00;
    public static final byte OP_DELETE = 0x01;
    public static final byte OP_STOP = 0x02;
    public static final byte OP_PAUSE = 0x03;
    public static final byte OP_HIDE_PAUSE = 0x04;
    public static final byte OP_INTERRUPT = 0x05;
    public static final byte OP_SLEEP = 0x06;
    public static final byte OP_RETURN = 0x07;
    public static final byte OP_JUMP = 0x08;

    /* Інструкції налаштування текстури */
    public static final byte OP_SOURCE = 0x09;
    public static final byte OP_UV_POSITION = 0x0A;
    public static final byte OP_UV_SCALE = 0x0B;
    public static final byte OP_UV_SCROLLING_X = 0x0C;
    public static final byte OP_UV_SCROLLING_Y = 0x0D;
    public static final byte OP_UV_MOVE = 0x0E;
    public static final byte OP_UV_RESCALE = 0x0F;
    public static final byte OP_UV_MODE = 0x10;

    /* Інструкції налаштування рендерингу */
    public static final byte OP_COLOR = 0x11;
    public static final byte OP_ALPHA = 0x12;
    public static final byte OP_CHANGE_COLOR = 0x13;
    public static final byte OP_FADE = 0x14;
    public static final byte OP_BLENDING = 0x15;
    public static final byte OP_VISIBLE = 0x16;
    public static final byte OP_FLIP_X = 0x17;
    public static final byte OP_FLIP_Y = 0x18;

    /* Інструкції переміщення */
    public static final byte OP_POSITION = 0x19;
    public static final byte OP_ANGLE = 0x1A;
    public static final byte OP_SCALE = 0x1B;
    public static final byte OP_MOVE = 0x1C;
    public static final byte OP_ROTATE = 0x1D;
    public static final byte OP_GROW = 0x1E;
    public static final byte OP_AUTOROTATE = 0x1F;
    public static final byte OP_ANGULAR_SPEED = 0x20;
    public static final byte OP_ORIGIN_MODE = 0x21;
    public static final byte OP_ANCHOR_MODE = 0x22;
    public static final byte OP_ANCHOR_OFFSET = 0x23;

    /* Константи */
    public static final byte UV_NONE = 0;
    public static final byte UV_REPEAT = 1;
    public static final byte UV_MIRROR = 2;
    public static final byte ORIGIN_PARENT = 0;
    public static final byte ORIGIN_SURFACE = 1;
    public static final byte ANCHOR_TOP_LEFT = 0;
    public static final byte ANCHOR_TOP_MIDDLE = 1;
    public static final byte ANCHOR_TOP_RIGHT = 2;
    public static final byte ANCHOR_MIDDLE_LEFT = 3;
    public static final byte ANCHOR_CENTER = 4;
    public static final byte ANCHOR_MIDDLE_RIGHT = 5;
    public static final byte ANCHOR_BOTTOM_LEFT = 6;
    public static final byte ANCHOR_BOTTOM_MIDDLE = 7;
    public static final byte ANCHOR_BOTTOM_RIGHT = 8;

    /* Таблиця розмірів інструкцій в байтах */
    public static final byte[] ANM_INSTRUCTION_SIZES = {
        3, 3, 3, 3, 3, 4, 7, 3, 7,
        4, 11, 11, 7, 7, 16, 16, 4,
        15, 7, 20, 12, 4, 4, 4, 4,
        11, 7, 11, 16, 12, 16, 4, 7, 4, 4, 11,
    };

    private static AnmVM instance;

    private SpriteBatch batch;
    private Viewport viewport;

    private float delta;
    private float t;

    private boolean drawing;
    private Surface surface;

    private Color backgroundColor;

    public AnmVM() {
        backgroundColor = new Color(0.05f, 0.05f, 0.05f, 1.0f);
    }

    public static void initialize(AnmVM instance) {
        if (AnmVM.instance != null) throw new PR25RuntimeException("AnmVM already initialized");

        AnmVM.instance = instance;
        instance.batch = new SpriteBatch();
        instance.viewport = new FitViewport(Game.BASE_WINDOW_WIDTH, Game.BASE_WINDOW_HEIGHT);
        instance.viewport.apply();
        instance.surface = null;
    }

    public static void shutdown() {
        if (instance.surface != null)
            instance.surface.fbo.end();
    }

    public void begin() {
        if (drawing) throw new PR25RuntimeException("AnmVM already begun");
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        batch.begin();
        drawing = true;
    }

    public void end() {
        if (!drawing) throw new PR25RuntimeException("AnmVM did not begin");
        batch.end();
        drawing = false;
    }

    public void update(float delta, float t) {
        this.delta = delta;
        this.t = t;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    public static AnmVM get() {
        return instance;
    }

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public void nextSurface(Surface surface) {
        if (!drawing) throw new PR25RuntimeException("AnmVM is not drawing");
        if (this.surface == null) {
            this.surface = surface;
            surface.fbo.begin();
            return;
        }
        Texture texture = this.surface.fbo.getColorBufferTexture();
        Surface temp = this.surface;
        temp.fbo.end();
        this.surface = surface;
        if (surface != null)
            surface.fbo.begin();
        batch.draw(texture, temp.x, temp.y, temp.width, temp.height);
    }

    public void draw(AnmGraphics o) {
        if (!o.absoluteVisible()) return;
        if (o.font != null && o.text != null && o.text.length() > 0)
            o.font.draw(batch, o.text, 60.0f, 60.0f);
        if (o.region != null) {
            float flipX = (o.flags & AnmGraphics.ANM_FLAG_FLIP_X) != 0 ? -1.0f : 1.0f;
            float flipY = (o.flags & AnmGraphics.ANM_FLAG_FLIP_Y) != 0 ? -1.0f : 1.0f;
            Color c = o.absoluteColor();
            float a = o.absoluteAlpha();
            Vector2 pos = o.absolutePosition();
            float an = o.absoluteAngle();
            Vector2 off = o.absoluteAnchorOffset();
            Vector2 sc = o.absoluteScale();
            if ((o.flags & AnmGraphics.ANM_FLAG_TELEPORT) != 0) {
                o.flags &= ~AnmGraphics.ANM_FLAG_TELEPORT;
                o.lastAbsolutePosition = pos;
                o.lastAbsoluteAngle = an;
                o.lastAbsoluteScale = sc;
            } else {
                o.lastAbsolutePosition.lerp(pos, t);
                o.lastAbsoluteAngle += (o.lastAbsoluteAngle - an) * t;
                o.lastAbsoluteScale.lerp(sc, t);
            }
            batch.setColor(c.r, c.g, c.b, a);
            batch.draw(o.region,
                o.lastAbsolutePosition.x, o.lastAbsolutePosition.y,
                off.x, off.y,
                o.region.getRegionWidth() * flipX, o.region.getRegionHeight() * flipY,
                o.lastAbsoluteScale.x, o.lastAbsoluteScale.y, o.lastAbsoluteAngle
            );
        }
    }

    public int execute(AnmGraphics o) {
        if (o.anm == null)
            return 1;

        if (o.interrupt > 0) {
            o.previousPointer = o.pointer;
            o.previousTime = o.time;

            o.pointer = o.scriptStart;
            boolean found = false;
            while (!found && o.pointer < o.scriptEnd) {
                while (o.pointer < o.scriptEnd && parseOpcode(o) != OP_INTERRUPT)
                    skip(o);
                if (o.pointer >= o.scriptEnd)
                    break;
                int position = o.pointer;
                skip(o, 3);
                if (parseByte(o) == o.interrupt - 1) {
                    o.pointer = position;
                    found = true;
                }
            }
            if (found) {
                skip(o);
                o.time = parseTime(o);
            } else {
                o.pointer = o.previousPointer;
                o.time = o.previousTime;
            }
            o.interrupt = 0;
        }
        while (o.pointer >= o.scriptStart && o.pointer < o.scriptEnd && o.time >= parseTime(o)) {
            switch (parseOpcode(o)) {
                case OP_NOP:
                case OP_INTERRUPT: skip(o); break;
                case OP_DELETE: o.delete(); return 1;
                case OP_STOP: o.anm = null; return 1;
                case OP_PAUSE: o.pointer = -1; skip(o); break;
                case OP_HIDE_PAUSE: {
                    o.pointer = -1;
                    o.flags &= ~AnmGraphics.ANM_FLAG_VISIBLE;
                    skip(o);
                } break;
                case OP_SLEEP: {
                    skip(o, 3);
                    o.time -= (short) parseInt(o);
                } break;
                case OP_RETURN: {
                    o.pointer = o.previousPointer;
                    o.time = o.previousTime;
                } break;
                case OP_JUMP: {
                    int pos = o.pointer;
                    skip(o, 3);
                    int diff = parseInt(o);
                    o.pointer = pos;
                    o.pointer += diff;
                    o.time = parseTime(o);
                } break;
                case OP_SOURCE: {
                    skip(o, 3);
                    o.loadSource(parseByte(o));
                } break;
                case OP_UV_POSITION: {
                    if (o.region == null) skip(o);
                    else {
                        skip(o, 3);
                        float uDiff = o.region.getU2() - o.region.getU();
                        float vDiff = o.region.getV2() - o.region.getV();
                        float u = parseFloat(o);
                        float v = parseFloat(o);
                        o.region.setU(u);
                        o.region.setV(v);
                        o.region.setU2(uDiff);
                        o.region.setV2(vDiff);
                    }
                } break;
                case OP_UV_SCALE: {
                    if (o.region == null) skip(o);
                    else {
                        skip(o, 3);
                        float halfWidth = parseFloat(o) * 0.5f;
                        float halfHeight = parseFloat(o) * 0.5f;
                        float midU = (o.region.getU() + o.region.getU2()) * 0.5f;
                        float midV = (o.region.getV() + o.region.getV2()) * 0.5f;
                    }
                } break;
                case OP_UV_SCROLLING_X: skip(o, 3); o.uScrolling = parseFloat(o); break;
                case OP_UV_SCROLLING_Y: skip(o, 3); o.vScrolling = parseFloat(o); break;
                case OP_UV_MOVE:
                case OP_UV_RESCALE: skip(o); break;
                case OP_UV_MODE: {
                    if (o.region == null) skip(o);
                    else {
                        skip(o, 3);
                        o.uvMode = parseByte(o);
                    }
                } break;
                case OP_COLOR: {
                    skip(o, 3);
                    float r = parseFloat(o);
                    float g = parseFloat(o);
                    float b = parseFloat(o);
                    o.color.set(r, g, b, 1.0f);
                } break;
                case OP_ALPHA: {
                    skip(o, 3);
                    o.alpha = parseFloat(o);
                } break;
                case OP_CHANGE_COLOR: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float r = parseFloat(o);
                    float g = parseFloat(o);
                    float b = parseFloat(o);
                    int type = parseByte(o);
                    o.colorInterpolator.start((byte) type, o.color.cpy(), new Color(r, g, b, 1.0f), (short) time);
                } break;
                case OP_FADE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float a = parseFloat(o);
                    int type = parseByte(o);
                    o.alphaInterpolator.start((byte) type, o.alpha, a, (short) time);
                } break;
                case OP_BLENDING: {
                    skip(o);
                } break;
                case OP_VISIBLE: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmGraphics.ANM_FLAG_VISIBLE;
                    else
                        o.flags |= AnmGraphics.ANM_FLAG_VISIBLE;
                } break;
                case OP_FLIP_X: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmGraphics.ANM_FLAG_FLIP_X;
                    else
                        o.flags |= AnmGraphics.ANM_FLAG_FLIP_X;
                } break;
                case OP_FLIP_Y: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmGraphics.ANM_FLAG_FLIP_Y;
                    else
                        o.flags |= AnmGraphics.ANM_FLAG_FLIP_Y;
                } break;
                case OP_POSITION: {
                    skip(o, 3);
                    o.anmPosition.set(parseFloat(o), parseFloat(o));
                } break;
                case OP_ANGLE: {
                    skip(o, 3);
                    o.anmAngle = parseFloat(o);
                } break;
                case OP_SCALE: {
                    skip(o, 3);
                    o.anmScale.set(parseFloat(o), parseFloat(o));
                } break;
                case OP_MOVE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float x = parseFloat(o);
                    float y = parseFloat(o);
                    int type = parseByte(o);
                    o.positionInterpolator.start((byte) type, o.anmPosition, new Vector2(x, y), (short) time);
                } break;
                case OP_ROTATE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float a = parseFloat(o);
                    int type = parseByte(o);
                    o.angleInterpolator.start((byte) type, o.anmAngle, a, (short) time);
                } break;
                case OP_GROW: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float w = parseFloat(o);
                    float h = parseFloat(o);
                    int type = parseByte(o);
                    o.positionInterpolator.start((byte) type, o.anmScale, new Vector2(w, h), (short) time);
                } break;
                case OP_AUTOROTATE: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmGraphics.ANM_FLAG_AUTOROTATE;
                    else
                        o.flags |= AnmGraphics.ANM_FLAG_AUTOROTATE;
                } break;
                case OP_ANGULAR_SPEED: {
                    skip(o, 3);
                    o.angularSpeed = parseFloat(o);
                } break;
                case OP_ORIGIN_MODE: {
                    skip(o, 3);
                    o.originMode = parseByte(o);
                } break;
                case OP_ANCHOR_MODE: {
                    skip(o, 3);
                    o.anchorMode = parseByte(o);
                } break;
                case OP_ANCHOR_OFFSET: {
                    skip(o, 3);
                    o.anchorOffset.set(parseFloat(o), parseFloat(o));
                } break;
                default:
                    break;
            }
        }

        if (o.region != null) {
            o.region.scroll(o.uScrolling, o.vScrolling);
        }
        if (o.colorInterpolator.isRunning()) {
            o.colorInterpolator.update();
            o.color = o.colorInterpolator.value();
        }
        if (o.alphaInterpolator.isRunning()) {
            o.alphaInterpolator.update();
            o.alpha = o.alphaInterpolator.value();
        }
        if (o.positionInterpolator.isRunning()) {
            o.positionInterpolator.update();
            o.anmPosition = o.positionInterpolator.value();
        }
        if (o.angleInterpolator.isRunning()) {
            o.angleInterpolator.update();
            o.anmAngle = o.angleInterpolator.value();
        } else
            o.anmAngle += o.angularSpeed;
        if (o.scaleInterpolator.isRunning()) {
            o.scaleInterpolator.update();
            o.anmScale = o.scaleInterpolator.value();
        }

        ++o.time;
        return 0;
    }

    private int parseByte(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        return o.anm.bytecode.get(o.pointer++);
    }

    private short parseShort(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        short s = o.anm.bytecode.getShort(o.pointer);
        o.pointer += 2;
        return s;
    }

    private int parseInt(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int i = o.anm.bytecode.getInt(o.pointer);
        o.pointer += 4;
        return i;
    }

    private float parseFloat(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        float f = o.anm.bytecode.getFloat(o.pointer);
        o.pointer += 4;
        return f;
    }

    private int parseOpcode(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int opcode = o.anm.bytecode.get(o.pointer);
        if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
        return o.anm.bytecode.get(o.pointer);
    }

    private short parseTime(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        return o.anm.bytecode.getShort(o.pointer + 1);
    }

    private void skip(AnmGraphics o, int n) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        o.pointer += n;
    }

    private void skip(AnmGraphics o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int opcode = o.anm.bytecode.get(o.pointer);
        if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
        o.pointer += ANM_INSTRUCTION_SIZES[opcode];
    }
}
