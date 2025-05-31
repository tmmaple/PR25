package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.util.PR25RuntimeException;
import ua.tmmaple.pr25.util.Tweener;

public final class GraphicManager {
    /* Таблиця розмірів інструкцій в байтах */
    public static final byte[] ANM_INSTRUCTION_SIZES = {
        3, 3, 3, 3, 3, 4, 7, 3, 7,
        4, 11, 11, 7, 7, 16, 16, 4,
        15, 7, 20, 12, 4, 4, 4, 4,
        11, 7, 11, 16, 12, 16, 4, 7, 4, 4, 11,
    };

    public static GraphicManager global;

    final SpriteBatch batch;
    private Viewport viewport;

    private float t;

    private boolean drawing;
    private Surface surface;

    private Color backgroundColor;

    public GraphicManager() {
        batch = new SpriteBatch();
        backgroundColor = Color.BLACK;
    }

    public void initialize() {
        viewport = new FitViewport(Game.BASE_WINDOW_WIDTH, Game.BASE_WINDOW_HEIGHT);
        viewport.apply();
        surface = null;
    }

    public void shutdown() {
        if (surface != null)
            surface.fbo.end();
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

    private static float getV(float midV, float halfHeight) {
        return midV - halfHeight;
    }

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

        private int flags;

        private short time;
        private int scriptStart;
        private int scriptEnd;
        private int pointer;
        private byte interrupt;

        private int previousPointer;
        private short previousTime;

        private float uScrolling;
        private float vScrolling;
        private int uvMode;

        private Color color;
        private float alpha;

        private Vector2 anmPosition;
        private float anmAngle;
        private Vector2 anmScale;
        private float angularSpeed;
        private int originMode;
        private int anchorMode;
        private Vector2 anchorOffset;

        private Vector2 lastAbsolutePosition;
        private float lastAbsoluteAngle;
        private Vector2 lastAbsoluteScale;

        private Anm anm;
        private TextureRegion region;

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

        public void draw() {
            if (!drawing) throw new PR25RuntimeException("GraphicManager is not drawing");
            if (!absoluteVisible()) return;
            if (region != null) {
                float flipX = (flags & ANM_FLAG_FLIP_X) != 0 ? -1.0f : 1.0f;
                float flipY = (flags & ANM_FLAG_FLIP_Y) != 0 ? -1.0f : 1.0f;
                Color c = absoluteColor();
                float a = absoluteAlpha();
                Vector2 pos = absolutePosition();
                float an = absoluteAngle();
                Vector2 off = absoluteAnchorOffset();
                Vector2 sc = absoluteScale();
                Texture.TextureWrap wrap;
                switch (uvMode) {
                    case ANM_UV_NONE: wrap = Texture.TextureWrap.ClampToEdge; break;
                    case ANM_UV_REPEAT: wrap = Texture.TextureWrap.Repeat; break;
                    case ANM_UV_MIRROR: wrap = Texture.TextureWrap.MirroredRepeat; break;
                    default: wrap = region.getTexture().getUWrap(); break;
                }
                region.getTexture().setWrap(wrap, wrap);
                if ((flags & ANM_FLAG_TELEPORT) != 0) {
                    flags &= ~ANM_FLAG_TELEPORT;
                    lastAbsolutePosition = pos;
                    lastAbsoluteAngle = an;
                    lastAbsoluteScale = sc;
                } else {
                    lastAbsolutePosition.lerp(pos, t);
                    lastAbsoluteAngle += (lastAbsoluteAngle - an) * t;
                    lastAbsoluteScale.lerp(sc, t);
                }
                batch.setColor(c.r, c.g, c.b, a);
                batch.draw(region,
                    lastAbsolutePosition.x, lastAbsolutePosition.y,
                    off.x, off.y,
                    region.getRegionWidth() * flipX, region.getRegionHeight() * flipY,
                    lastAbsoluteScale.x, lastAbsoluteScale.y, lastAbsoluteAngle
                );
            }
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

        public boolean absoluteVisible() {
            AnmVirtualMachine o = this;
            boolean result = true;
            while (o != null && result) {
                if ((o.flags & ANM_FLAG_VISIBLE) == 0) result = false;
                else if (o.alpha == 0.0f) result = false;
                else o = o.parent;
            }
            return result;
        }

        public Color absoluteColor() {
            AnmVirtualMachine o = this;
            Color result = Color.WHITE;
            while (o != null) {
                result.mul(o.color);
                o = o.parent;
            }
            return result;
        }

        public float absoluteAlpha() {
            AnmVirtualMachine o = this;
            float result = 1.0f;
            while (o != null) {
                result *= o.alpha;
                o = o.parent;
            }
            return result;
        }

        public Vector2 absolutePosition() {
            AnmVirtualMachine o = this;
            Vector2 result = new Vector2();
            while (o != null) {
                if (o.originMode == ANM_ORIGIN_PARENT) result.add(o.position);
                result.add(o.anmPosition);
                o = o.parent;
            }
            return result;
        }

        public float absoluteAngle() {
            AnmVirtualMachine o = this;
            float result = 0.0f;
            while (o != null) {
                if ((o.flags & ANM_FLAG_AUTOROTATE) != 0) result += o.angle;
                result += o.anmAngle;
                o = o.parent;
            }
            return result;
        }

        public Vector2 absoluteScale() {
            AnmVirtualMachine o = this;
            Vector2 result = new Vector2(1.0f, 1.0f);
            while (o != null) {
                if (o.originMode == ANM_ORIGIN_PARENT) result.scl(o.scale);
                result.scl(o.anmScale);
                o = o.parent;
            }
            return result;
        }

        public Vector2 absoluteAnchorOffset() {
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

        public int execute() {
            GraphicManager mgr = GraphicManager.this;
            if (anm == null)
                return 1;

            lastAbsolutePosition = absolutePosition();
            lastAbsoluteAngle = absoluteAngle();
            lastAbsoluteScale = absoluteScale();

            if (interrupt > 0) {
                previousPointer = pointer;
                previousTime = time;

                pointer = scriptStart;
                boolean found = false;
                while (!found && pointer < scriptEnd) {
                    while (pointer < scriptEnd && parseOpcode() != ANM_OP_INTERRUPT)
                        skip();
                    if (pointer >= scriptEnd)
                        break;
                    int position = pointer;
                    skipToArgs();
                    if (parseByte() == interrupt) {
                        pointer = position;
                        found = true;
                    }
                }
                if (found) {
                    skip();
                    time = parseTime();
                } else {
                    pointer = previousPointer;
                    time = previousTime;
                }
                interrupt = 0;
            }
            while (pointer >= scriptStart && pointer < scriptEnd && time >= parseTime()) {
                switch (parseOpcode()) {
                    case ANM_OP_NOP:
                    case ANM_OP_INTERRUPT: skip(); break;
                    case ANM_OP_DELETE: delete(); return 1;
                    case ANM_OP_STOP: anm = null; return 1;
                    case ANM_OP_PAUSE: pointer = -1; skip(); break;
                    case ANM_OP_HIDE_PAUSE: {
                        pointer = -1;
                        flags &= ~ANM_FLAG_VISIBLE;
                        skip();
                    } break;
                    case ANM_OP_SLEEP: {
                        skipToArgs();
                        time -= (short) parseInt();
                    } break;
                    case ANM_OP_RETURN: {
                        pointer = previousPointer;
                        time = previousTime;
                    } break;
                    case ANM_OP_JUMP: {
                        int pos = pointer;
                        skipToArgs();
                        int diff = parseInt();
                        pointer = pos;
                        pointer += diff;
                        time = parseTime();
                    } break;
                    case ANM_OP_SOURCE: {
                        skipToArgs();
                        loadSource(parseByte());
                    } break;
                    case ANM_OP_UV_POSITION: {
                        if (region == null) skip();
                        else {
                            skipToArgs();
                            float uDiff = region.getU2() - region.getU();
                            float vDiff = region.getV2() - region.getV();
                            float u = parseFloat();
                            float v = parseFloat();
                            region.setU(u);
                            region.setV(v);
                            region.setU2(uDiff);
                            region.setV2(vDiff);
                        }
                    } break;
                    case ANM_OP_UV_SCALE: {
                        if (region == null) skip();
                        else {
                            skipToArgs();
                            float halfWidth = parseFloat() * 0.5f;
                            float halfHeight = parseFloat() * 0.5f;
                            float midU = (region.getU() + region.getU2()) * 0.5f;
                            float midV = (region.getV() + region.getV2()) * 0.5f;
                            region.setU(midU - halfWidth);
                            region.setV(midV - halfHeight);
                            region.setU2(midU + halfWidth);
                            region.setV2(midV + halfHeight);
                        }
                    } break;
                    case ANM_OP_UV_SCROLLING_X: skipToArgs(); uScrolling = parseFloat(); break;
                    case ANM_OP_UV_SCROLLING_Y: skipToArgs(); vScrolling = parseFloat(); break;
                    case ANM_OP_UV_MOVE:
                    case ANM_OP_UV_RESCALE: skip(); break;
                    case ANM_OP_UV_MODE: {
                        if (region == null) skip();
                        else {
                            skipToArgs();
                            uvMode = parseByte();
                        }
                    } break;
                    case ANM_OP_COLOR: {
                        skipToArgs();
                        float r = parseFloat();
                        float g = parseFloat();
                        float b = parseFloat();
                        color.set(r, g, b, 1.0f);
                    } break;
                    case ANM_OP_ALPHA: {
                        skipToArgs();
                        alpha = parseFloat();
                    } break;
                    case ANM_OP_CHANGE_COLOR: {
                        skipToArgs();
                        int time = parseInt();
                        float r = parseFloat();
                        float g = parseFloat();
                        float b = parseFloat();
                        int type = parseByte();
                        colorInterpolator.start((byte) type, color.cpy(), new Color(r, g, b, 1.0f), (short) time);
                    } break;
                    case ANM_OP_FADE: {
                        skipToArgs();
                        int time = parseInt();
                        float a = parseFloat();
                        int type = parseByte();
                        alphaInterpolator.start((byte) type, alpha, a, (short) time);
                    } break;
                    case ANM_OP_BLENDING: {
                        skip();
                    } break;
                    case ANM_OP_VISIBLE: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~ANM_FLAG_VISIBLE;
                        else
                            flags |= ANM_FLAG_VISIBLE;
                    } break;
                    case ANM_OP_FLIP_X: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~ANM_FLAG_FLIP_X;
                        else
                            flags |= ANM_FLAG_FLIP_X;
                    } break;
                    case ANM_OP_FLIP_Y: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~ANM_FLAG_FLIP_Y;
                        else
                            flags |= ANM_FLAG_FLIP_Y;
                    } break;
                    case ANM_OP_POSITION: {
                        skipToArgs();
                        anmPosition.set(parseFloat(), parseFloat());
                    } break;
                    case ANM_OP_ANGLE: {
                        skipToArgs();
                        anmAngle = parseFloat();
                    } break;
                    case ANM_OP_SCALE: {
                        skipToArgs();
                        anmScale.set(parseFloat(), parseFloat());
                    } break;
                    case ANM_OP_MOVE: {
                        skipToArgs();
                        int time = parseInt();
                        float x = parseFloat();
                        float y = parseFloat();
                        int type = parseByte();
                        positionInterpolator.start((byte) type, anmPosition, new Vector2(x, y), (short) time);
                    } break;
                    case ANM_OP_ROTATE: {
                        skipToArgs();
                        int time = parseInt();
                        float a = parseFloat();
                        int type = parseByte();
                        angleInterpolator.start((byte) type, anmAngle, a, (short) time);
                    } break;
                    case ANM_OP_GROW: {
                        skipToArgs();
                        int time = parseInt();
                        float w = parseFloat();
                        float h = parseFloat();
                        int type = parseByte();
                        positionInterpolator.start((byte) type, anmScale, new Vector2(w, h), (short) time);
                    } break;
                    case ANM_OP_AUTOROTATE: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~ANM_FLAG_AUTOROTATE;
                        else
                            flags |= ANM_FLAG_AUTOROTATE;
                    } break;
                    case ANM_OP_ANGULAR_SPEED: {
                        skipToArgs();
                        angularSpeed = parseFloat();
                    } break;
                    case ANM_OP_ORIGIN_MODE: {
                        skipToArgs();
                        originMode = parseByte();
                    } break;
                    case ANM_OP_ANCHOR_MODE: {
                        skipToArgs();
                        anchorMode = parseByte();
                    } break;
                    case ANM_OP_ANCHOR_OFFSET: {
                        skipToArgs();
                        anchorOffset.set(parseFloat(), parseFloat());
                    } break;
                    default:
                        break;
                }
            }

            if (region != null) {
                region.scroll(uScrolling, vScrolling);
            }
            if (colorInterpolator.isRunning()) {
                colorInterpolator.update();
                color = colorInterpolator.value();
            }
            if (alphaInterpolator.isRunning()) {
                alphaInterpolator.update();
                alpha = alphaInterpolator.value();
            }
            if (positionInterpolator.isRunning()) {
                positionInterpolator.update();
                anmPosition = positionInterpolator.value();
            }
            if (angleInterpolator.isRunning()) {
                angleInterpolator.update();
                anmAngle = angleInterpolator.value();
            } else
                anmAngle += angularSpeed;
            if (scaleInterpolator.isRunning()) {
                scaleInterpolator.update();
                anmScale = scaleInterpolator.value();
            }

            ++time;
            return 0;
        }

        private void skip() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int opcode = anm.bytecode.get(pointer);
            if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
            pointer += ANM_INSTRUCTION_SIZES[opcode];
        }

        private void skipToArgs() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            pointer += 3;
        }

        private short parseTime() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            return anm.bytecode.getShort(pointer + 1);
        }

        private int parseOpcode() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int opcode = anm.bytecode.get(pointer);
            if (opcode < 0 || opcode >= ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
            return anm.bytecode.get(pointer);
        }

        private float parseFloat() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            float f = anm.bytecode.getFloat(pointer);
            pointer += 4;
            return f;
        }

        private int parseInt() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int i = anm.bytecode.getInt(pointer);
            pointer += 4;
            return i;
        }

        private int parseByte() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            return anm.bytecode.get(pointer++);
        }
    }
}
