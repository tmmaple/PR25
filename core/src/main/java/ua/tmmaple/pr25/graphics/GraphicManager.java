package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.util.PR25RuntimeException;
import ua.tmmaple.pr25.util.Tweener;

/**
 * Керує малюванням графіки.
 * @author uwuhasmile
 */
public final class GraphicManager {
    public static GraphicManager global;

    final SpriteBatch batch;
    public final Viewport viewport;

    private float t;

    private boolean drawing;
    private Surface surface;

    private Color backgroundColor;

    public GraphicManager() {
        batch = new SpriteBatch();
        backgroundColor = Color.BLACK;
        viewport = new FitViewport(Game.BASE_WINDOW_WIDTH, Game.BASE_WINDOW_HEIGHT);
    }

    /**
     * Ініціалізує та вмикає вьюпорт.
     * @author uwuhasmile
     */
    public void initialize() {
        viewport.apply();
    }

    /**
     * Вимикає.
     * Якщо використовується якась поверхня, то вона завершує своє малювання.
     * @author uwuhasmile
     */
    public void shutdown() {
        if (drawing) end();
    }

    /**
     * Переходить в режим малювання.
     * Виклик обов'язковий для того, щоб можна було почати малювати на екран.
     * @throws PR25RuntimeException якщо вже в режимі малювання
     * @author uwuhasmile
     */
    public void begin() {
        if (drawing) throw new PR25RuntimeException("GraphicManager already begun");
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        batch.begin();
        drawing = true;
    }

    /**
     * Виходить з режиму малювання.
     * @throws PR25RuntimeException якщо не в режимі малювання
     * @author uwuhasmile
     */
    public void end() {
        if (!drawing) throw new PR25RuntimeException("GraphicManager did not begin");
        if (surface != null)
            surface.end();
        batch.end();
        drawing = false;
    }

    /**
     * Оновлює внутрішнє значення альфи для плавного переходу між різними тіками.
     * Потрібно, бо тіки необов'язково синхронізовані з кількістю кадрів в секунду.
     * @author uwuhasmile
     */
    public void update(float t) {
        this.t = t;
    }

    /**
     * Встановлює колір фону.
     * @author uwuhasmile
     */
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    /**
     * Поверхня, на яку відбувається відмалювання.
     * Розмір поверхні може відрізнятись від розміру вьюпорта.
     * @author uwuhasmile
     */
    public final class Surface {
        private final FrameBuffer fbo;

        public Surface(Pixmap.Format format, int width, int height) {
            fbo = new FrameBuffer(format, width, height, false);
        }

        /**
         * Застосовує поверхню до менеджера.
         * Після виклику, все буде відмальовуватись на цю поверхню.
         * @throws PR25RuntimeException якщо не в режимі малювання, або ця поверхня вже використовується
         * @author uwuhasmile
         */
        public void use() {
            if (!drawing) throw new PR25RuntimeException("Can't use a surface before drawing");
            if (surface != null) {
                if (surface == this) throw new PR25RuntimeException("This surface is already used");
                surface.end();
            }
            fbo.begin();
            surface = this;
        }

        /**
         * Застосовує поверхню до менеджера, але при цьому одразу відмальовує минулу поверхню.
         * Після виклику, все буде відмальовуватись на цю поверхню.
         * @throws PR25RuntimeException якщо не в режимі малювання, або ця поверхня вже використовується
         * @author uwuhasmile
         */
        public void chain(float x, float y, float width, float height) {
            if (!drawing) throw new PR25RuntimeException("Can't use a surface before drawing");
            if (surface != null) {
                if (surface == this) throw new PR25RuntimeException("This surface is already used");
                surface.end();
            }
            fbo.begin();
            if (surface != null) surface.draw(x, y, width, height);
            surface = this;
        }

        /**
         * Закінчує використання поверхні.
         * Після виклику, все буде малюватись у вьюпорт.
         * @throws PR25RuntimeException якщо поверхня і так не використовується
         * @author uwuhasmile
         */
        public void end() {
            if (surface != this) throw new PR25RuntimeException("Can't end surface that is unused");
            fbo.end();
            surface = null;
        }

        /**
         * @return Текстура з всім, що було відмальовано.
         * @author uwuhasmile
         */
        public TextureRegion get() {
            if (!drawing) throw new PR25RuntimeException("Can't use a surface before drawing");
            TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
            region.flip(false, true);
            return region;
        }

        /**
         * Малює поверхню в певній позиції та розмірі.
         * Поверхня може відмалюватись на саму себе.
         * @author uwuhasmile
         */
        public void draw(float x, float y, float width, float height) {
            if (!drawing) throw new PR25RuntimeException("Can't use a surface before drawing");
            batch.draw(get(), x, y, width, height);
        }

        /**
         * Видаляє поверхню.
         * @author uwuhasmile
         */
        public void dispose() {
            fbo.dispose();
            if (surface == this)
                surface = null;
        }
    }

    /**
     * Графічний об'єкт з власним виконуваним кодом.
     * @author uwuhasmile
     */
    public final class AnmVirtualMachine {
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

        /**
         * Відмальовує об'єкт на екран.
         * Зазвичай, позиція буде інтерпольована між двома тіками.
         * Проте, якщо об'єкт був позначений як той, що телепортувався, то відмалювання буде одразу в позиції.
         * @throws PR25RuntimeException якщо не в режимі малювання
         * @author uwuhasmile
         */
        public void draw() {
            if (!drawing) throw new PR25RuntimeException("GraphicManager is not drawing");
            if (!absoluteVisible()) return;
            if (region != null) {
                float flipX = (flags & Anm.ANM_FLAG_FLIP_X) != 0 ? -1.0f : 1.0f;
                float flipY = (flags & Anm.ANM_FLAG_FLIP_Y) != 0 ? -1.0f : 1.0f;
                Color c = absoluteColor();
                float a = absoluteAlpha();
                Vector2 pos = absolutePosition();
                float an = absoluteAngle();
                Vector2 off = absoluteAnchorOffset();
                Vector2 sc = absoluteScale();
                Texture.TextureWrap wrap;
                switch (uvMode) {
                    case Anm.ANM_UV_NONE: wrap = Texture.TextureWrap.ClampToEdge; break;
                    case Anm.ANM_UV_REPEAT: wrap = Texture.TextureWrap.Repeat; break;
                    case Anm.ANM_UV_MIRROR: wrap = Texture.TextureWrap.MirroredRepeat; break;
                    default: wrap = region.getTexture().getUWrap(); break;
                }
                region.getTexture().setWrap(wrap, wrap);
                if ((flags & Anm.ANM_FLAG_TELEPORT) != 0) {
                    flags &= ~Anm.ANM_FLAG_TELEPORT;
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

        /**
         * Виконує скрипт, призначений до об'єкта та оновлює анімації інтерполяції.
         * Виконання відбувається доти, поки не дійде або до інструкції з більшою часовою відміткою, або до останньої інструкції.
         * При доходженні до останньої інструкції, скрипт скидається.
         * @throws PR25RuntimeException якщо в режимі малювання
         * @author uwuhasmile
         */
        public int execute() {
            if (drawing) throw new PR25RuntimeException("Can't execute script when drawing");
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
                    while (pointer < scriptEnd && parseOpcode() != Anm.ANM_OP_INTERRUPT)
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
                    case Anm.ANM_OP_NOP:
                    case Anm.ANM_OP_INTERRUPT: skip(); break;
                    case Anm.ANM_OP_DELETE: delete(); return 1;
                    case Anm.ANM_OP_STOP: anm = null; return 1;
                    case Anm.ANM_OP_PAUSE: pointer = -1; skip(); break;
                    case Anm.ANM_OP_HIDE_PAUSE: {
                        pointer = -1;
                        flags &= ~Anm.ANM_FLAG_VISIBLE;
                        skip();
                    } break;
                    case Anm.ANM_OP_SLEEP: {
                        skipToArgs();
                        time -= (short) parseInt();
                    } break;
                    case Anm.ANM_OP_RETURN: {
                        pointer = previousPointer;
                        time = previousTime;
                    } break;
                    case Anm.ANM_OP_JUMP: {
                        int pos = pointer;
                        skipToArgs();
                        int diff = parseInt();
                        pointer = pos;
                        pointer += diff;
                        time = parseTime();
                    } break;
                    case Anm.ANM_OP_SOURCE: {
                        skipToArgs();
                        loadSource(parseByte());
                    } break;
                    case Anm.ANM_OP_UV_POSITION: {
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
                    case Anm.ANM_OP_UV_SCALE: {
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
                    case Anm.ANM_OP_UV_SCROLLING_X: skipToArgs(); uScrolling = parseFloat(); break;
                    case Anm.ANM_OP_UV_SCROLLING_Y: skipToArgs(); vScrolling = parseFloat(); break;
                    case Anm.ANM_OP_UV_MOVE:
                    case Anm.ANM_OP_UV_RESCALE: skip(); break;
                    case Anm.ANM_OP_UV_MODE: {
                        if (region == null) skip();
                        else {
                            skipToArgs();
                            uvMode = parseByte();
                        }
                    } break;
                    case Anm.ANM_OP_COLOR: {
                        skipToArgs();
                        float r = parseFloat();
                        float g = parseFloat();
                        float b = parseFloat();
                        color.set(r, g, b, 1.0f);
                    } break;
                    case Anm.ANM_OP_ALPHA: {
                        skipToArgs();
                        alpha = parseFloat();
                    } break;
                    case Anm.ANM_OP_CHANGE_COLOR: {
                        skipToArgs();
                        int time = parseInt();
                        float r = parseFloat();
                        float g = parseFloat();
                        float b = parseFloat();
                        int type = parseByte();
                        colorInterpolator.start((byte) type, color.cpy(), new Color(r, g, b, 1.0f), (short) time);
                    } break;
                    case Anm.ANM_OP_FADE: {
                        skipToArgs();
                        int time = parseInt();
                        float a = parseFloat();
                        int type = parseByte();
                        alphaInterpolator.start((byte) type, alpha, a, (short) time);
                    } break;
                    case Anm.ANM_OP_BLENDING: {
                        skip();
                    } break;
                    case Anm.ANM_OP_VISIBLE: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~Anm.ANM_FLAG_VISIBLE;
                        else
                            flags |= Anm.ANM_FLAG_VISIBLE;
                    } break;
                    case Anm.ANM_OP_FLIP_X: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~Anm.ANM_FLAG_FLIP_X;
                        else
                            flags |= Anm.ANM_FLAG_FLIP_X;
                    } break;
                    case Anm.ANM_OP_FLIP_Y: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~Anm.ANM_FLAG_FLIP_Y;
                        else
                            flags |= Anm.ANM_FLAG_FLIP_Y;
                    } break;
                    case Anm.ANM_OP_POSITION: {
                        skipToArgs();
                        anmPosition.set(parseFloat(), parseFloat());
                    } break;
                    case Anm.ANM_OP_ANGLE: {
                        skipToArgs();
                        anmAngle = parseFloat();
                    } break;
                    case Anm.ANM_OP_SCALE: {
                        skipToArgs();
                        anmScale.set(parseFloat(), parseFloat());
                    } break;
                    case Anm.ANM_OP_MOVE: {
                        skipToArgs();
                        int time = parseInt();
                        float x = parseFloat();
                        float y = parseFloat();
                        int type = parseByte();
                        positionInterpolator.start((byte) type, anmPosition, new Vector2(x, y), (short) time);
                    } break;
                    case Anm.ANM_OP_ROTATE: {
                        skipToArgs();
                        int time = parseInt();
                        float a = parseFloat();
                        int type = parseByte();
                        angleInterpolator.start((byte) type, anmAngle, a, (short) time);
                    } break;
                    case Anm.ANM_OP_GROW: {
                        skipToArgs();
                        int time = parseInt();
                        float w = parseFloat();
                        float h = parseFloat();
                        int type = parseByte();
                        positionInterpolator.start((byte) type, anmScale, new Vector2(w, h), (short) time);
                    } break;
                    case Anm.ANM_OP_AUTOROTATE: {
                        skipToArgs();
                        int value = parseByte();
                        if (value == 0)
                            flags &= ~Anm.ANM_FLAG_AUTOROTATE;
                        else
                            flags |= Anm.ANM_FLAG_AUTOROTATE;
                    } break;
                    case Anm.ANM_OP_ANGULAR_SPEED: {
                        skipToArgs();
                        angularSpeed = parseFloat();
                    } break;
                    case Anm.ANM_OP_ORIGIN_MODE: {
                        skipToArgs();
                        originMode = parseByte();
                    } break;
                    case Anm.ANM_OP_ANCHOR_MODE: {
                        skipToArgs();
                        anchorMode = parseByte();
                    } break;
                    case Anm.ANM_OP_ANCHOR_OFFSET: {
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

        /**
         * Завантажує ANM ресурс.
         * @author uwuhasmile
         */
        public void loadAnm(Anm anm) {
            this.anm = anm;
        }

        /**
         * Завантажує скрипт з ANM-ресурсу.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс
         * @author uwuhasmile
         */
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

            flags = Anm.ANM_FLAG_VISIBLE | Anm.ANM_FLAG_TELEPORT;
            uScrolling = 0.0f;
            vScrolling = 0.0f;
            color.set(Color.WHITE);
            alpha = 1.0f;
            anmPosition.set(0.0f, 0.0f);
            anmAngle = 0.0f;
            anmScale.set(1.0f, 1.0f);
            angularSpeed = 0.0f;
            originMode = Anm.ANM_ORIGIN_PARENT;
            anchorMode = Anm.ANM_ANCHOR_CENTER;
            anchorOffset.set(0.0f, 0.0f);

            lastAbsolutePosition = new Vector2(position);
            lastAbsoluteAngle = 0.0f;
            lastAbsoluteScale = new Vector2(scale);
        }

        /**
         * Завантажує текстуру id з ANM-ресурсу.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс
         * @author uwuhasmile
         */
        public void loadSource(int id) {
            if (anm == null) throw new PR25RuntimeException("ANM is null, no sources");
            region = anm.getSource(id);
        }

        /**
         * Власноруч завантажує певну текстуру. Може бути корисно для відмалювання поверхні.
         * @author uwuhasmile
         */
        public void loadTexture(Texture texture) {
            this.region = new TextureRegion(texture);
        }

        /**
         * Власноруч завантажує певну текстуру. Може бути корисно для відмалювання поверхні.
         * @author uwuhasmile
         */
        public void loadTexture(TextureRegion region) {
            this.region = new TextureRegion(region);
        }

        /**
         * Встановлює interrupt для віртуальної машини.
         * Якщо більше за 0, то на наступний тік віртуальна машина спробує знайти та перейти до нього.
         * Потрібно, наприклад, для зміни анімацій.
         * @author uwuhasmile
         */
        public void interrupt(byte interrupt) {
            this.interrupt = interrupt;
        }

        /**
         * Скидає interrupt.
         * @author uwuhasmile
         */
        public void resetInterrupt() {
            interrupt = 0;
        }

        /**
         * Позначає як телепортовану.
         * Якщо була така позначка, то наступного кадру не буде інтерполяції між тіками.
         * @author uwuhasmile
         */
        public void teleport() {
            flags |= Anm.ANM_FLAG_TELEPORT;
        }

        /**
         * Повністю зупиняє виконання скрипта.
         * @author uwuhasmile
         */
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

        /**
         * @return видимість відносно батьків
         * @author uwuhasmile
         */
        public boolean absoluteVisible() {
            AnmVirtualMachine o = this;
            boolean result = true;
            while (o != null && result) {
                if ((o.flags & Anm.ANM_FLAG_VISIBLE) == 0) result = false;
                else if (o.alpha == 0.0f) result = false;
                else o = o.parent;
            }
            return result;
        }

        /**
         * @return колір відносно батьків
         * @author uwuhasmile
         */
        public Color absoluteColor() {
            AnmVirtualMachine o = this;
            Color result = Color.WHITE;
            while (o != null) {
                result.mul(o.color);
                o = o.parent;
            }
            return result;
        }

        /**
         * @return прозорість відносно батьків
         * @author uwuhasmile
         */
        public float absoluteAlpha() {
            AnmVirtualMachine o = this;
            float result = 1.0f;
            while (o != null) {
                result *= o.alpha;
                o = o.parent;
            }
            return result;
        }

        /**
         * @return позиція відносно батьків
         * @author uwuhasmile
         */
        public Vector2 absolutePosition() {
            AnmVirtualMachine o = this;
            Vector2 result = new Vector2();
            while (o != null) {
                if (o.originMode == Anm.ANM_ORIGIN_PARENT) result.add(o.position);
                result.add(o.anmPosition);
                o = o.parent;
            }
            return result;
        }

        /**
         * @return кут повороту відносно батьків
         * @author uwuhasmile
         */
        public float absoluteAngle() {
            AnmVirtualMachine o = this;
            float result = 0.0f;
            while (o != null) {
                if ((o.flags & Anm.ANM_FLAG_AUTOROTATE) != 0) result += o.angle;
                result += o.anmAngle;
                o = o.parent;
            }
            return result;
        }

        /**
         * @return розмір відносно батьків
         * @author uwuhasmile
         */
        public Vector2 absoluteScale() {
            AnmVirtualMachine o = this;
            Vector2 result = new Vector2(1.0f, 1.0f);
            while (o != null) {
                if (o.originMode == Anm.ANM_ORIGIN_PARENT) result.scl(o.scale);
                result.scl(o.anmScale);
                o = o.parent;
            }
            return result;
        }

        /**
         * @return остаточна позиція якірної точки
         * @author uwuhasmile
         */
        public Vector2 absoluteAnchorOffset() {
            int width = region.getRegionWidth();
            int height = region.getRegionHeight();
            Vector2 result = anchorOffset.cpy();
            switch (anchorMode) {
                case Anm.ANM_ANCHOR_TOP_LEFT: result.set(0.0f, 0.0f); break;
                case Anm.ANM_ANCHOR_TOP_MIDDLE: result.set(-width * 0.5f, 0.0f); break;
                case Anm.ANM_ANCHOR_TOP_RIGHT: result.set(-width, 0.0f); break;
                case Anm.ANM_ANCHOR_MIDDLE_LEFT: result.set(0.0f, -height * 0.5f); break;
                case Anm.ANM_ANCHOR_CENTER: result.set(-width * 0.5f, -height * 0.5f); break;
                case Anm.ANM_ANCHOR_MIDDLE_RIGHT: result.set(-width, -height * 0.5f); break;
                case Anm.ANM_ANCHOR_BOTTOM_LEFT: result.set(0.0f, -height); break;
                case Anm.ANM_ANCHOR_BOTTOM_MIDDLE: result.set(-width * 0.5f, -height); break;
                case Anm.ANM_ANCHOR_BOTTOM_RIGHT: result.set(-width, -height); break;
            }
            return result;
        }

        /**
         * Пропускає поточну інструкцію.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс, скрипт вже було завершено, або невірний код інструкції
         * @author uwuhasmile
         */
        private void skip() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int opcode = anm.bytecode.get(pointer);
            if (opcode < 0 || opcode >= Anm.ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
            pointer += Anm.ANM_INSTRUCTION_SIZES[opcode];
        }

        /**
         * Пропускає поточну інструкцію до аргументу.
         * Фактично, пропускає 3 байти (1 байт - код інструкції, 2 байти - часовий мітка)
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс або скрипт вже було завершено
         * @author uwuhasmile
         */
        private void skipToArgs() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            pointer += 3;
        }

        /**
         * @return часова мітка інструкції
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс або скрипт вже було завершено
         * @author uwuhasmile
         */
        private short parseTime() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            return anm.bytecode.getShort(pointer + 1);
        }

        /**
         * @return код інструкції
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс, скрипт вже було завершено, або код не відповідає дійсній інструкції
         * @author uwuhasmile
         */
        private int parseOpcode() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int opcode = anm.bytecode.get(pointer);
            if (opcode < 0 || opcode >= Anm.ANM_INSTRUCTION_SIZES.length) throw new PR25RuntimeException("Invalid opcode " + opcode);
            return anm.bytecode.get(pointer);
        }

        /**
         * Повертає float-значення та переходить до наступного аргументу.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс або скрипт вже було завершено
         * @author uwuhasmile
         */
        private float parseFloat() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            float f = anm.bytecode.getFloat(pointer);
            pointer += 4;
            return f;
        }

        /**
         * Повертає int-значення та переходить до наступного аргументу.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс або скрипт вже було завершено
         * @author uwuhasmile
         */
        private int parseInt() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            int i = anm.bytecode.getInt(pointer);
            pointer += 4;
            return i;
        }

        /**
         * Повертає byte-значення та переходить до наступного аргументу.
         * @throws PR25RuntimeException якщо не виставлений ANM-ресурс або скрипт вже було завершено
         * @author uwuhasmile
         */
        private int parseByte() {
            if (anm == null) throw new PR25RuntimeException("No ANM script");
            if (pointer >= scriptEnd) throw new PR25RuntimeException("End of ANM script was reached");
            return anm.bytecode.get(pointer++);
        }
    }
}
