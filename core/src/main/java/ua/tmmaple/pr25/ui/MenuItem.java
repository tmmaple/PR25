package ua.tmmaple.pr25.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;

/**
 * Пункт меню інтерфейсу користувача.
 * Може бути кількох типів. Взаємодія через клавіатуру, має бути виділена.
 * @author afiliushkin
 */
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
    private boolean justPressed;

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

    /**
     * Робить з елементу просто кнопку, що виконує якусь дію при натисканні.
     * @param listener дія, що має виконатись при натисканні
     * @author afiliushkin
     */
    public MenuItem makeButton(ButtonListener listener) {
        this.listener = listener;
        type = ItemType.BUTTON;
        return this;
    }

    /**
     * Робить з елементу слайдер цілих чисел. Керується кнопками вправо та вліво.
     * @param listener дія, що має виконатись при зміні значення
     * @param start початкове значення
     * @param step крок, з яким міняється значення
     * @param min найменше можливе значення
     * @param max найбільше можливе значення
     * @param loop чи може значення зациклитись
     * @author afiliushkin
     */
    public MenuItem makeIntSlider(IntListener listener, int start, int step, int min, int max, boolean loop) {
        this.listener = listener;
        type = ItemType.INT_SLIDER;
        i0 = start;
        i1 = step;
        i2 = min;
        i3 = max;
        this.loop = loop;
        listener.intChanged(this, start);
        return this;
    }

    /**
     * Робить з елементу слайдер десяткових чисел. Керується кнопками вправо та вліво.
     * @param listener дія, що має виконатись при натисканні
     * @param start початкове значення
     * @param step крок, з яким міняється значення
     * @param min найменше можливе значення
     * @param max найбільше можливе значення
     * @param loop чи може значення зациклитись
     * @author afiliushkin
     */
    public MenuItem makeFloatSlider(FloatListener listener, float start, float step, float min, float max, boolean loop) {
        this.listener = listener;
        type = ItemType.FLOAT_SLIDER;
        f0 = start;
        f1 = step;
        f2 = min;
        f3 = max;
        this.loop = loop;
        listener.floatChanged(this, start);
        return this;
    }

    /**
     * @param font шрифт, коли елемент не виділено
     * @author afiliushkin
     */
    public MenuItem unfocusedFont(int font) {
        unfocusedSettings.setFont((byte) font);
        return this;
    }

    /**
     * @param color колір, коли елемент не виділено
     * @author afiliushkin
     */
    public MenuItem unfocusedColor(int color) {
        Color.rgb888ToColor(unfocusedSettings.color, color);
        return this;
    }

    /**
     * @param font шрифт, коли елемент виділено
     * @author afiliushkin
     */
    public MenuItem focusedFont(int font) {
        focusedSettings.setFont((byte) font);
        return this;
    }

    /**
     * @param color колір, коли елемент виділено
     * @author afiliushkin
     */
    public MenuItem focusedColor(int color) {
        Color.rgb888ToColor(focusedSettings.color, color);
        return this;
    }

    /**
     * @param align горизонтальне розташування тексту в елементі
     * @author afiliushkin
     */
    public MenuItem align(int align) {
        unfocusedSettings.hAlign = align;
        focusedSettings.hAlign = align;
        return this;
    }

    /**
     * @param width ширина зони, де показується текст
     * @author afiliushkin
     */
    public MenuItem width(int width) {
        unfocusedSettings.targetWidth = width;
        focusedSettings.targetWidth = width;
        return this;
    }

    /**
     * @param x горизонтальна позиція
     * @param y вертикальна позиція
     * @author afiliushkin
     */
    public MenuItem position(float x, float y) {
        this.position.set(x, y);
        vm.position.set(x, y);
        return this;
    }

    /**
     * @param x горизонтальне зміщення тексту
     * @param y вертикальне зміщення тексту
     * @author afiliushkin
     */
    public MenuItem offset(float x, float y) {
        this.offset.set(x, y);
        return this;
    }

    /**
     * @param text текст, що показується
     * @author afiliushkin
     */
    public MenuItem text(CharSequence text) {
        this.text = text;
        return this;
    }

    /**
     * Задає ANM-скрипт, за яким слідуватиме елемент.
     * @param anm ANM-ресурс
     * @param script анімація в ANM-ресурсі
     * @author afiliushkin
     */
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

    /**
     * Задає батьківську віртуальну машину ANM.
     * @param vm віртуальна машина
     * @author afiliushkin
     */
    public MenuItem parent(GraphicManager.AnmVirtualMachine vm) {
        this.parentVm = vm;
        this.vm.parent = vm;
        if (!usesVm) {
            focusedSettings.parent = parentVm;
            unfocusedSettings.parent = parentVm;
        }
        return this;
    }

    /**
     * Задає елемент згори, для навігації
     * @author afiliushkin
     */
    public MenuItem up(MenuItem up) {
        this.up = up;
        return this;
    }

    /**
     * Задає елемент знизу, для навігації
     * @author afiliushkin
     */
    public MenuItem down(MenuItem down) {
        this.down = down;
        return this;
    }

    /**
     * Виділяє елемент для можливості взаємодії
     * @author afiliushkin
     */
    public void focus() {
        if (focused)
            return;
        if (usesVm)
            vm.interrupt((byte) 1);
        focused = true;
        justPressed = true;
    }

    /**
     * Знімає виділення з елементу
     * @author afiliushkin
     */
    public void unfocus() {
        if (!focused)
            return;
        if (usesVm)
            vm.interrupt((byte) 2);
        focused = false;
    }

    /**
     * Оновлює графіку та стан вводу користувача для взаємодії з елементом
     * @author afiliushkin
     */
    public void update() {
        if (type == null)
            return;
        if (justPressed)
            justPressed = false;
        else if (focused) {
            boolean accept = God.global.inputState(God.INPUT_UI_ACCEPT) == God.INPUT_STATE_JUST_PRESSED;
            boolean right = God.global.inputState(God.INPUT_UI_RIGHT) == God.INPUT_STATE_JUST_PRESSED;
            boolean left = God.global.inputState(God.INPUT_UI_LEFT) == God.INPUT_STATE_JUST_PRESSED;
            boolean up = God.global.inputState(God.INPUT_UI_UP) == God.INPUT_STATE_JUST_PRESSED;
            boolean down = God.global.inputState(God.INPUT_UI_DOWN) == God.INPUT_STATE_JUST_PRESSED;
            if (type == ItemType.BUTTON && listener != null && accept) {
                listener.pressed(this);
                Audio.global.playSound("accept.ogg", 1.0f);
            } else if (listener != null && right) {
                if (type == ItemType.INT_SLIDER) {
                    i0 += i1;
                    if (i0 > i3)
                        if (loop)
                            i0 = i2;
                        else
                            i0 = i3;
                    listener.intChanged(this, i0);
                } else if (type == ItemType.FLOAT_SLIDER) {
                    f0 += f1;
                    if (f0 > f3)
                        if (loop)
                            f0 = f2;
                        else
                            f0 = f3;
                    listener.floatChanged(this, f0);
                }
                Audio.global.playSound("accept.ogg", 1.0f);
            } else if (listener != null && left) {
                if (type == ItemType.INT_SLIDER) {
                    i0 -= i1;
                    if (i0 < i2)
                        if (loop)
                            i0 = i3;
                        else
                            i0 = i2;
                    listener.intChanged(this, i0);
                } else if (type == ItemType.FLOAT_SLIDER) {
                    f0 -= f1;
                    if (f0 < f2)
                        if (loop)
                            f0 = f3;
                        else
                            f0 = f2;
                    listener.floatChanged(this, f0);
                }
                Audio.global.playSound("accept.ogg", 1.0f);
            } else if (this.up != null && up) {
                unfocus();
                this.up.focus();
                Audio.global.playSound("select.ogg", 1.0f);
            } else if (this.down != null && down) {
                unfocus();
                this.down.focus();
                Audio.global.playSound("select.ogg", 1.0f);
            }
        }
        if (usesVm)
            vm.execute();
    }

    /**
     * Малює елемент на екрані
     * @author afiliushkin
     */
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

    /**
     * @return відносна позиція елементу на екрані
     * @author afiliushkin
     */
    public Vector2 position() {
        return position.cpy();
    }

    /**
     * Знищує функціональну та візуальну частину елемента для переналаштування.
     * @author afiliushkin
     */
    public void destroy() {
        type = null;
        focused = false;
        if (usesVm) {
            vm.delete();
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
