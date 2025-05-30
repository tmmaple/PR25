package ua.tmmaple.pr25;

import com.badlogic.gdx.Gdx;

public final class Input {
    private static final byte INPUT_NONE = 0;
    private static final byte INPUT_JUST_PRESSED = 1;
    private static final byte INPUT_PRESSED = 2;
    private static final byte INPUT_RELEASED = 3;

    private static final byte[] INPUT_KEY_STATES = new byte[com.badlogic.gdx.Input.Keys.MAX_KEYCODE];

    public static Input global;

    public Input() { }

    public static int register() {
        Flow.FlowNode<Input> node = new Flow.FlowNode<>(global, Input::update);
        return Flow.global.addToUpdate(node, 6000);
    }

    private static int update(Input instance) {
        for (int i = 0; i < INPUT_KEY_STATES.length; ++i) {
            if (Gdx.input.isKeyPressed(i)) {
                if (INPUT_KEY_STATES[i] == INPUT_NONE || INPUT_KEY_STATES[i] == INPUT_RELEASED)
                    INPUT_KEY_STATES[i] = INPUT_JUST_PRESSED;
                else if (INPUT_KEY_STATES[i] == INPUT_JUST_PRESSED)
                    INPUT_KEY_STATES[i] = INPUT_PRESSED;
            } else {
                if (INPUT_KEY_STATES[i] == INPUT_PRESSED || INPUT_KEY_STATES[i] == INPUT_JUST_PRESSED)
                    INPUT_KEY_STATES[i] = INPUT_RELEASED;
                else if (INPUT_KEY_STATES[i] == INPUT_RELEASED)
                    INPUT_KEY_STATES[i] = INPUT_NONE;
            }
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    public boolean isKeyPressed(int keyCode) {
        return INPUT_KEY_STATES[keyCode] == INPUT_JUST_PRESSED || INPUT_KEY_STATES[keyCode] == INPUT_PRESSED;
    }

    public boolean wasKeyJustPressed(int keyCode) {
        return INPUT_KEY_STATES[keyCode] == INPUT_JUST_PRESSED;
    }

    public boolean wasKeyJustReleased(int keyCode) {
        return INPUT_KEY_STATES[keyCode] == INPUT_RELEASED;
    }
}
