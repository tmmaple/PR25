package ua.tmmaple.pr25.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public abstract class Interpolator<T> {
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

    Interpolator() {
        running = false;
    }

    public final void start(byte type, T a, T b, short t) {
        value = a;

        this.type = type;
        this.a = a;
        this.b = b;
        this.t = t;
        curr = 0;

        running = true;
    }

    public final void end() {
        running = false;
    }

    public final void update() {
        if (!isRunning()) return;
        ++curr;
        if (curr == t) {
            value = b;
            running = false;
        } else {
            float alpha = this.curr * (1.0f / t);
            switch (type) {
                case INTERPOLATION_LINEAR:
                    break;
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

    protected abstract void setValue(float alpha);

    public final boolean isRunning() {
        return running;
    }

    public final T value() {
        return value;
    }

    public static class FloatInterpolator extends Interpolator<Float> {
        @Override
        protected void setValue(float alpha) {
            value = a + (b - a) * alpha;
        }
    }

    public static class Vector2Interpolator extends Interpolator<Vector2> {
        @Override
        protected void setValue(float alpha) {
            value = a.cpy().lerp(b, alpha);
        }
    }

    public static class ColorInterpolator extends Interpolator<Color> {
        @Override
        protected void setValue(float alpha) {
            value = a.cpy().lerp(b, alpha);
        }
    }
}
