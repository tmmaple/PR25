package ua.tmmaple.pr25.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Анімація зміни між двома значеннями.
 * Має оновлюватись власноруч у системах чи власних об'єктах.
 * @author uwuhasmile
 */
public abstract class Tweener<T> {
    public static final byte INTERPOLATION_LINEAR = 0;
    public static final byte INTERPOLATION_EASE_IN = 1;
    public static final byte INTERPOLATION_EASE_OUT = 2;
    public static final byte INTERPOLATION_EASE_IN_OUT = 3;

    protected T value;

    protected byte type;
    protected T a;
    protected T b;
    protected short t;
    protected short curr;

    private boolean running;

    public Tweener() {
        running = false;
    }

    /**
     * Запускає анімацію.
     * @param type тип інтерполяції
     * @param a початкове значення
     * @param b кінцеве значення
     * @param t час інтерполяції, в тіках
     * @author uwuhasmile
     */
    public final void start(byte type, T a, T b, short t) {
        initializeValues(a, b);

        this.type = type;
        this.t = t;
        curr = 0;

        running = true;
    }

    /**
     * Зупиняє анімацію
     * @author uwuhasmile
     */
    public final void end() {
        running = false;
    }

    /**
     * Оновлює анімацію на один тік
     * @author uwuhasmile
     */
    public final void update() {
        if (!isRunning()) return;
        ++curr;
        if (curr >= t) {
            value = b;
            running = false;
        } else {
            float alpha = (float) curr / t;
            switch (type) {
                case INTERPOLATION_EASE_IN:
                    alpha *= alpha;
                    break;
                case INTERPOLATION_EASE_OUT:
                    alpha = 1.0f - (1.0f - alpha) * (1.0f - alpha);
                    break;
                case INTERPOLATION_EASE_IN_OUT:
                    alpha = alpha < 0.5 ? (2.0f * alpha * alpha) : 1.0f - (-2.0f * alpha + 2.0f) * (-2.0f * alpha + 2.0f) * 0.5f;
                    break;
            }
            setValue(alpha);
        }
    }

    /**
     * Встановлює значення анімації
     * @param alpha позиція від 0 до 1
     */
    protected abstract void setValue(float alpha);

    /**
     * Встановлює початкове значення
     * @param a перша позиція
     */
    protected abstract void initializeValues(T a, T b);

    public final boolean isRunning() {
        return running;
    }

    public final T value() {
        return value;
    }

    /**
     * Анімація зміни між двома значеннями float.
     * @author uwuhasmile
     */
    public static class FloatTweener extends Tweener<Float> {
        @Override
        protected void setValue(float alpha) {
            value = a + (b - a) * alpha;
        }

        @Override
        protected void initializeValues(Float a, Float b) {
            value = a;
            this.a = a;
            this.b = b;
        }
    }

    /**
     * Анімація зміни між двома двовимірними векторами.
     * @author uwuhasmile
     */
    public static class Vector2Tweener extends Tweener<Vector2> {
        public Vector2Tweener() {
            super();
            value = new Vector2();
        }

        @Override
        protected void setValue(float alpha) {
            value.set(a).lerp(b, alpha);
        }

        @Override
        protected void initializeValues(Vector2 a, Vector2 b) {
            value.set(a);
            this.a = a.cpy();
            this.b = b.cpy();
        }
    }

    /**
     * Анімація зміни між двома кольорами.
     * @author uwuhasmile
     */
    public static class ColorTweener extends Tweener<Color> {
        public ColorTweener() {
            super();
            value = new Color();
        }

        @Override
        protected void setValue(float alpha) {
            value.set(a).lerp(b, alpha);
        }

        @Override
        protected void initializeValues(Color a, Color b) {
            value.set(a);
            this.a = a.cpy();
            this.b = b.cpy();
        }
    }
}
