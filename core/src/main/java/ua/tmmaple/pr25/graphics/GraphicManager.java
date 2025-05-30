package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class GraphicManager {

    /* Таблиця розмірів інструкцій в байтах */
    public static final byte[] ANM_INSTRUCTION_SIZES = {
        3, 3, 3, 3, 3, 4, 7, 3, 7,
        4, 11, 11, 7, 7, 16, 16, 4,
        15, 7, 20, 12, 4, 4, 4, 4,
        11, 7, 11, 16, 12, 16, 4, 7, 4, 4, 11,
    };

    public static GraphicManager global;

    private SpriteBatch batch;
    private Viewport viewport;

    private float t;

    private boolean drawing;
    private Surface surface;

    private Color backgroundColor;

    public GraphicManager() {
        backgroundColor = Color.BLACK;
    }

    public static void initialize() {
        global.batch = new SpriteBatch();
        global.viewport = new FitViewport(Game.BASE_WINDOW_WIDTH, Game.BASE_WINDOW_HEIGHT);
        global.viewport.apply();
        global.surface = null;
    }

    public static void shutdown() {
        if (global.surface != null)
            global.surface.fbo.end();
    }

    public void begin() {
        if (drawing) throw new PR25RuntimeException("GraphicManager already begun");
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        batch.begin();
        drawing = true;
    }

    public void end() {
        if (!drawing) throw new PR25RuntimeException("GraphicManager did not begin");
        batch.end();
        drawing = false;
    }

    public void update(float t) {
        this.t = t;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public void nextSurface(Surface surface) {
        if (!drawing) throw new PR25RuntimeException("GraphicManager is not drawing");
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

    public void draw(AnmVirtualMachine o) {
        if (!o.absoluteVisible()) return;
        if (o.font != null && o.text != null && o.text.length() > 0)
            o.font.draw(batch, o.text, 60.0f, 60.0f);
        if (o.region != null) {
            float flipX = (o.flags & AnmVirtualMachine.ANM_FLAG_FLIP_X) != 0 ? -1.0f : 1.0f;
            float flipY = (o.flags & AnmVirtualMachine.ANM_FLAG_FLIP_Y) != 0 ? -1.0f : 1.0f;
            Color c = o.absoluteColor();
            float a = o.absoluteAlpha();
            Vector2 pos = o.absolutePosition();
            float an = o.absoluteAngle();
            Vector2 off = o.absoluteAnchorOffset();
            Vector2 sc = o.absoluteScale();
            if ((o.flags & AnmVirtualMachine.ANM_FLAG_TELEPORT) != 0) {
                o.flags &= ~AnmVirtualMachine.ANM_FLAG_TELEPORT;
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

    public int execute(AnmVirtualMachine o) {
        if (o.anm == null)
            return 1;

        o.lastAbsolutePosition = o.absolutePosition();
        o.lastAbsoluteAngle = o.absoluteAngle();
        o.lastAbsoluteScale = o.absoluteScale();

        if (o.interrupt > 0) {
            o.previousPointer = o.pointer;
            o.previousTime = o.time;

            o.pointer = o.scriptStart;
            boolean found = false;
            while (!found && o.pointer < o.scriptEnd) {
                while (o.pointer < o.scriptEnd && parseOpcode(o) != AnmVirtualMachine.ANM_OP_INTERRUPT)
                    skip(o);
                if (o.pointer >= o.scriptEnd)
                    break;
                int position = o.pointer;
                skip(o, 3);
                if (parseByte(o) == o.interrupt) {
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
                case AnmVirtualMachine.ANM_OP_NOP:
                case AnmVirtualMachine.ANM_OP_INTERRUPT: skip(o); break;
                case AnmVirtualMachine.ANM_OP_DELETE: o.delete(); return 1;
                case AnmVirtualMachine.ANM_OP_STOP: o.anm = null; return 1;
                case AnmVirtualMachine.ANM_OP_PAUSE: o.pointer = -1; skip(o); break;
                case AnmVirtualMachine.ANM_OP_HIDE_PAUSE: {
                    o.pointer = -1;
                    o.flags &= ~AnmVirtualMachine.ANM_FLAG_VISIBLE;
                    skip(o);
                } break;
                case AnmVirtualMachine.ANM_OP_SLEEP: {
                    skip(o, 3);
                    o.time -= (short) parseInt(o);
                } break;
                case AnmVirtualMachine.ANM_OP_RETURN: {
                    o.pointer = o.previousPointer;
                    o.time = o.previousTime;
                } break;
                case AnmVirtualMachine.ANM_OP_JUMP: {
                    int pos = o.pointer;
                    skip(o, 3);
                    int diff = parseInt(o);
                    o.pointer = pos;
                    o.pointer += diff;
                    o.time = parseTime(o);
                } break;
                case AnmVirtualMachine.ANM_OP_SOURCE: {
                    skip(o, 3);
                    o.loadSource(parseByte(o));
                } break;
                case AnmVirtualMachine.ANM_OP_UV_POSITION: {
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
                case AnmVirtualMachine.ANM_OP_UV_SCALE: {
                    if (o.region == null) skip(o);
                    else {
                        skip(o, 3);
                        float halfWidth = parseFloat(o) * 0.5f;
                        float halfHeight = parseFloat(o) * 0.5f;
                        float midU = (o.region.getU() + o.region.getU2()) * 0.5f;
                        float midV = (o.region.getV() + o.region.getV2()) * 0.5f;
                        o.region.setU(midU - halfWidth);
                        o.region.setV(getV(midV, halfHeight));
                        o.region.setU2(midU + halfWidth);
                        o.region.setV2(midV + halfHeight);
                    }
                } break;
                case AnmVirtualMachine.ANM_OP_UV_SCROLLING_X: skip(o, 3); o.uScrolling = parseFloat(o); break;
                case AnmVirtualMachine.ANM_OP_UV_SCROLLING_Y: skip(o, 3); o.vScrolling = parseFloat(o); break;
                case AnmVirtualMachine.ANM_OP_UV_MOVE:
                case AnmVirtualMachine.ANM_OP_UV_RESCALE: skip(o); break;
                case AnmVirtualMachine.ANM_OP_UV_MODE: {
                    if (o.region == null) skip(o);
                    else {
                        skip(o, 3);
                        o.uvMode = parseByte(o);
                    }
                } break;
                case AnmVirtualMachine.ANM_OP_COLOR: {
                    skip(o, 3);
                    float r = parseFloat(o);
                    float g = parseFloat(o);
                    float b = parseFloat(o);
                    o.color.set(r, g, b, 1.0f);
                } break;
                case AnmVirtualMachine.ANM_OP_ALPHA: {
                    skip(o, 3);
                    o.alpha = parseFloat(o);
                } break;
                case AnmVirtualMachine.ANM_OP_CHANGE_COLOR: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float r = parseFloat(o);
                    float g = parseFloat(o);
                    float b = parseFloat(o);
                    int type = parseByte(o);
                    o.colorInterpolator.start((byte) type, o.color.cpy(), new Color(r, g, b, 1.0f), (short) time);
                } break;
                case AnmVirtualMachine.ANM_OP_FADE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float a = parseFloat(o);
                    int type = parseByte(o);
                    o.alphaInterpolator.start((byte) type, o.alpha, a, (short) time);
                } break;
                case AnmVirtualMachine.ANM_OP_BLENDING: {
                    skip(o);
                } break;
                case AnmVirtualMachine.ANM_OP_VISIBLE: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmVirtualMachine.ANM_FLAG_VISIBLE;
                    else
                        o.flags |= AnmVirtualMachine.ANM_FLAG_VISIBLE;
                } break;
                case AnmVirtualMachine.ANM_OP_FLIP_X: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmVirtualMachine.ANM_FLAG_FLIP_X;
                    else
                        o.flags |= AnmVirtualMachine.ANM_FLAG_FLIP_X;
                } break;
                case AnmVirtualMachine.ANM_OP_FLIP_Y: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmVirtualMachine.ANM_FLAG_FLIP_Y;
                    else
                        o.flags |= AnmVirtualMachine.ANM_FLAG_FLIP_Y;
                } break;
                case AnmVirtualMachine.ANM_OP_POSITION: {
                    skip(o, 3);
                    o.anmPosition.set(parseFloat(o), parseFloat(o));
                } break;
                case AnmVirtualMachine.ANM_OP_ANGLE: {
                    skip(o, 3);
                    o.anmAngle = parseFloat(o);
                } break;
                case AnmVirtualMachine.ANM_OP_SCALE: {
                    skip(o, 3);
                    o.anmScale.set(parseFloat(o), parseFloat(o));
                } break;
                case AnmVirtualMachine.ANM_OP_MOVE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float x = parseFloat(o);
                    float y = parseFloat(o);
                    int type = parseByte(o);
                    o.positionInterpolator.start((byte) type, o.anmPosition, new Vector2(x, y), (short) time);
                } break;
                case AnmVirtualMachine.ANM_OP_ROTATE: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float a = parseFloat(o);
                    int type = parseByte(o);
                    o.angleInterpolator.start((byte) type, o.anmAngle, a, (short) time);
                } break;
                case AnmVirtualMachine.ANM_OP_GROW: {
                    skip(o, 3);
                    int time = parseInt(o);
                    float w = parseFloat(o);
                    float h = parseFloat(o);
                    int type = parseByte(o);
                    o.positionInterpolator.start((byte) type, o.anmScale, new Vector2(w, h), (short) time);
                } break;
                case AnmVirtualMachine.ANM_OP_AUTOROTATE: {
                    skip(o, 3);
                    int value = parseByte(o);
                    if (value == 0)
                        o.flags &= ~AnmVirtualMachine.ANM_FLAG_AUTOROTATE;
                    else
                        o.flags |= AnmVirtualMachine.ANM_FLAG_AUTOROTATE;
                } break;
                case AnmVirtualMachine.ANM_OP_ANGULAR_SPEED: {
                    skip(o, 3);
                    o.angularSpeed = parseFloat(o);
                } break;
                case AnmVirtualMachine.ANM_OP_ORIGIN_MODE: {
                    skip(o, 3);
                    o.originMode = parseByte(o);
                } break;
                case AnmVirtualMachine.ANM_OP_ANCHOR_MODE: {
                    skip(o, 3);
                    o.anchorMode = parseByte(o);
                } break;
                case AnmVirtualMachine.ANM_OP_ANCHOR_OFFSET: {
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

    private static float getV(float midV, float halfHeight) {
        return midV - halfHeight;
    }

    private int parseByte(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        return o.anm.bytecode.get(o.pointer++);
    }

    private short parseShort(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        short s = o.anm.bytecode.getShort(o.pointer);
        o.pointer += 2;
        return s;
    }

    private int parseInt(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int i = o.anm.bytecode.getInt(o.pointer);
        o.pointer += 4;
        return i;
    }

    private float parseFloat(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        float f = o.anm.bytecode.getFloat(o.pointer);
        o.pointer += 4;
        return f;
    }

    private int parseOpcode(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int opcode = o.anm.bytecode.get(o.pointer);
        if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
        return o.anm.bytecode.get(o.pointer);
    }

    private short parseTime(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        return o.anm.bytecode.getShort(o.pointer + 1);
    }

    private void skip(AnmVirtualMachine o, int n) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        o.pointer += n;
    }

    private void skip(AnmVirtualMachine o) {
        if (o.anm == null) throw new PR25RuntimeException("No ANM script");
        if (o.pointer >= o.scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
        int opcode = o.anm.bytecode.get(o.pointer);
        if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
        o.pointer += ANM_INSTRUCTION_SIZES[opcode];
    }
}
