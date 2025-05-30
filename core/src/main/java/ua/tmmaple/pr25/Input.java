package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;
import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class Input {
    private static final byte INPUT_NONE = 0;
    private static final byte INPUT_JUST_PRESSED = 1;
    private static final byte INPUT_PRESSED = 2;
    private static final byte INPUT_RELEASED = 3;

    private static Input instance;

    private final byte[] INPUT_KEY_STATES = new byte[com.badlogic.gdx.Input.Keys.MAX_KEYCODE];

    Input() { }

    static void register(Input instance) {
        if (Input.instance != null) throw new PR25RuntimeException("Input manager is already initialized");

        Input.instance = instance;
        Flow.FlowNode<Input> node = new Flow.FlowNode<>(instance, Input::update, null, Input::removed);
        Flow.addToUpdate(node, 6000);
    }

    private static int update(Input instance) {
        for (int i = 0; i < instance.INPUT_KEY_STATES.length; ++i) {
            if (Gdx.input.isKeyPressed(i)) {
                if (instance.INPUT_KEY_STATES[i] == INPUT_NONE || instance.INPUT_KEY_STATES[i] == INPUT_RELEASED)
                    instance.INPUT_KEY_STATES[i] = INPUT_JUST_PRESSED;
                else if (instance.INPUT_KEY_STATES[i] == INPUT_JUST_PRESSED)
                    instance.INPUT_KEY_STATES[i] = INPUT_PRESSED;
            } else {
                if (instance.INPUT_KEY_STATES[i] == INPUT_PRESSED || instance.INPUT_KEY_STATES[i] == INPUT_JUST_PRESSED)
                    instance.INPUT_KEY_STATES[i] = INPUT_RELEASED;
                else if (instance.INPUT_KEY_STATES[i] == INPUT_RELEASED)
                    instance.INPUT_KEY_STATES[i] = INPUT_NONE;
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    public static boolean isKeyPressed(int keyCode) {
        if (Input.instance == null) return false;
        return Input.instance.INPUT_KEY_STATES[keyCode] == INPUT_JUST_PRESSED || Input.instance.INPUT_KEY_STATES[keyCode] == INPUT_PRESSED;
    }

    public static boolean wasKeyJustPressed(int keyCode) {
        if (Input.instance == null) return false;
        return Input.instance.INPUT_KEY_STATES[keyCode] == INPUT_JUST_PRESSED;
    }

    public static boolean wasKeyJustReleased(int keyCode) {
        if (Input.instance == null) return false;
        return Input.instance.INPUT_KEY_STATES[keyCode] == INPUT_RELEASED;
    }

    private static int removed(Input instance) {
        Input.instance = null;
        return 0;
    }
}
