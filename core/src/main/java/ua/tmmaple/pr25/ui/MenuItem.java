package ua.tmmaple.pr25.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;

public final class MenuItem {
    private enum ItemType {
        BUTTON,
        INT_SLIDER,
        FLOAT_SLIDER,
    }

    private GraphicManager.AnmVirtualMachine parentVm;

    private ItemType type;

    private final Vector2 position;
    public final Vector2 offset;

    private final GraphicManager.AnmVirtualMachine vm;
    private boolean usesVm;
    private final TextManager.TextSettings unfocusedSettings;
    private final TextManager.TextSettings focusedSettings;

    public CharSequence text;

    private int i0;
    private int i1;
    private int i2;
    private int i3;
    private float f0;
    private float f1;
    private float f2;
    private float f3;
    private boolean loop;

    private boolean focused;

    private MenuItem up;
    private MenuItem down;

    private ItemListener listener;

    public MenuItem() {
        position = new Vector2();
        offset = new Vector2();
        vm = GraphicManager.global.new AnmVirtualMachine();
        unfocusedSettings = TextManager.global.new TextSettings();
        focusedSettings = TextManager.global.new TextSettings();
    }

    public MenuItem makeButton(ButtonListener listener) {
        this.listener = listener;
        type = ItemType.BUTTON;
        return this;
    }

    public MenuItem makeIntSlider(IntListener listener, int start, int step, int min, int max, boolean loop) {
        this.listener = listener;
        type = ItemType.INT_SLIDER;
        i0 = start;
        i1 = step;
        i2 = min;
        i3 = max;
        this.loop = loop;
        return this;
    }

    public MenuItem makeFloatSlider(FloatListener listener, float start, float step, float min, float max, boolean loop) {
        this.listener = listener;
        type = ItemType.FLOAT_SLIDER;
        f0 = start;
        f1 = step;
        f2 = min;
        f3 = max;
        this.loop = loop;
        return this;
    }

    public MenuItem unfocusedFont(int font) {
        unfocusedSettings.setFont((byte) font);
        return this;
    }

    public MenuItem unfocusedColor(int color) {
        Color.rgb888ToColor(unfocusedSettings.color, color);
        return this;
    }

    public MenuItem focusedFont(int font) {
        focusedSettings.setFont((byte) font);
        return this;
    }

    public MenuItem focusedColor(int color) {
        Color.rgb888ToColor(focusedSettings.color, color);
        return this;
    }

    public MenuItem align(int align) {
        unfocusedSettings.hAlign = align;
        focusedSettings.hAlign = align;
        return this;
    }

    public MenuItem width(int width) {
        unfocusedSettings.targetWidth = width;
        focusedSettings.targetWidth = width;
        return this;
    }

    public MenuItem position(float x, float y) {
        this.position.set(x, y);
        vm.position.set(x, y);
        return this;
    }

    public MenuItem offset(float x, float y) {
        this.offset.set(x, y);
        return this;
    }

    public MenuItem text(CharSequence text) {
        this.text = text;
        return this;
    }

    public MenuItem anm(Anm anm, String script) {
        if (usesVm)
            vm.delete();
        if (anm == null || script == null || script.isEmpty()) {
            focusedSettings.parent = parentVm;
            unfocusedSettings.parent = parentVm;
            usesVm = false;
            return  this;
        }
        usesVm = true;
        focusedSettings.parent = vm;
        unfocusedSettings.parent = vm;
        vm.loadAnm(anm);
        vm.loadScriptAndPlay(script);
        return this;
    }

    public MenuItem parent(GraphicManager.AnmVirtualMachine vm) {
        this.parentVm = vm;
        this.vm.parent = vm;
        if (!usesVm) {
            focusedSettings.parent = parentVm;
            unfocusedSettings.parent = parentVm;
        }
        return this;
    }

    public MenuItem up(MenuItem up) {
        this.up = up;
        return this;
    }

    public MenuItem down(MenuItem down) {
        this.down = down;
        return this;
    }

    public void focus() {
        if (usesVm)
            vm.interrupt((byte) 1);
        focused = true;
    }

    public void unfocus() {
        if (usesVm)
            vm.interrupt((byte) 2);
        focused = false;
    }

    public void update() {
        if (type == null)
            return;
        if (focused) {
            boolean accept = God.global.inputState(God.INPUT_UI_ACCEPT) == God.INPUT_STATE_JUST_PRESSED;
            boolean right = God.global.inputState(God.INPUT_UI_RIGHT) == God.INPUT_STATE_JUST_PRESSED;
            boolean left = God.global.inputState(God.INPUT_UI_LEFT) == God.INPUT_STATE_JUST_PRESSED;
            boolean up = God.global.inputState(God.INPUT_UI_UP) == God.INPUT_STATE_JUST_PRESSED;
            boolean down = God.global.inputState(God.INPUT_UI_DOWN) == God.INPUT_STATE_JUST_PRESSED;
            if (type == ItemType.BUTTON && listener != null && accept)
                listener.pressed(this);
            else if (listener != null && right) {
                if (type == ItemType.INT_SLIDER) {
                    if (i0 == i3 && loop)
                        i0 = i2;
                    else if (i0 < i3) {
                        i0 += i1;
                        if (i0 > i3)
                            i0 = i3;
                    }
                    listener.intChanged(this, i0);
                } else if (type == ItemType.FLOAT_SLIDER) {
                    if (f0 == f3 && loop)
                        f0 = f2;
                    else if (f0 < f3) {
                        f0 += f1;
                        if (f0 > f3)
                            f0 = f3;
                    }
                    listener.floatChanged(this, f0);
                }
            } else if (listener != null && left) {
                if (type == ItemType.INT_SLIDER) {
                    if (i0 == i2 && loop)
                        i0 = i3;
                    else if (i0 > i2) {
                        i0 += i1;
                        if (i0 < i2)
                            i0 = i2;
                    }
                    listener.intChanged(this, i0);
                } else if (type == ItemType.FLOAT_SLIDER) {
                    if (f0 == f2 && loop)
                        f0 = f3;
                    else if (f0 > f2) {
                        f0 += f1;
                        if (f0 < f2)
                            f0 = f2;
                    }
                    listener.floatChanged(this, f0);
                }
            } else if (this.up != null && up) {
                unfocus();
                this.up.focus();
            } else if (this.down != null && down) {
                unfocus();
                this.down.focus();
            }
        }
        if (usesVm)
            vm.execute();
    }

    public void draw() {
        if (type == null)
            return;
        if (usesVm)
            vm.draw();
        if (focused) {
            focusedSettings.position.set(offset);
            if (!usesVm)
                focusedSettings.position.add(position);
            focusedSettings.draw(text);
        } else {
            unfocusedSettings.position.set(offset);
            if (!usesVm)
                unfocusedSettings.position.add(position);
            unfocusedSettings.draw(text);
        }
    }

    public Vector2 position() {
        return position.cpy();
    }

    public void destroy() {
        type = null;
        focused = false;
        text = null;
        vm.delete();
        if (usesVm) {
            usesVm = false;
            focusedSettings.parent = parentVm;
            unfocusedSettings.parent = parentVm;
        }
    }

    private interface ItemListener {
        default void pressed(MenuItem item) { }

        default void intChanged(MenuItem item, int value) { }

        default void floatChanged(MenuItem item, float value) { }
    }

    public interface ButtonListener extends ItemListener {
        @Override
        void pressed(MenuItem item);
    }

    public interface IntListener extends ItemListener {
        @Override
        void intChanged(MenuItem item, int value);
    }

    public interface FloatListener extends ItemListener {
        @Override
        void floatChanged(MenuItem item, float value);
    }
}
