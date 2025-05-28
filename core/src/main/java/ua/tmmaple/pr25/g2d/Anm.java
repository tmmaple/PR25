package ua.tmmaple.pr25.g2d;

public final class Anm {
    public static final byte[] ANM_MAGIC = { '%', 'A', 'N', 'M' };
    public static final byte ANM_VERSION =  0x01;

    /* System instructions */
    public static final byte OP_NOP = 0x00;
    public static final byte OP_DELETE = 0x01;
    public static final byte OP_STOP = 0x02;
    public static final byte OP_PAUSE = 0x03;
    public static final byte OP_HIDE_PAUSE = 0x04;
    public static final byte OP_INTERRUPT = 0x05;
    public static final byte OP_SLEEP = 0x06;
    public static final byte OP_RETURN = 0x07;
    public static final byte OP_JUMP = 0x08;

    /* Texture settings instructions */
    public static final byte OP_SOURCE = 0x09;
    public static final byte OP_UV_POSITION = 0x0A;
    public static final byte OP_UV_SCALE = 0x0B;
    public static final byte OP_UV_SCROLLING_X = 0x0C;
    public static final byte OP_UV_SCROLLING_Y = 0x0D;
    public static final byte OP_UV_MOVE = 0x0E;
    public static final byte OP_UV_RESCALE = 0x0F;
    public static final byte OP_UV_MODE = 0x10;

    /* Render settings instructions */
    public static final byte OP_COLOR = 0x11;
    public static final byte OP_ALPHA = 0x12;
    public static final byte OP_CHANGE_COLOR = 0x13;
    public static final byte OP_FADE = 0x14;
    public static final byte OP_BLENDING = 0x15;
    public static final byte OP_VISIBLE = 0x16;
    public static final byte OP_FLIP_X = 0x17;
    public static final byte OP_FLIP_Y = 0x18;

    /* Transform instructions */
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

    /* Constants */
    public static final byte UV_NONE = 0;
    public static final byte UV_REPEAT = 1;
    public static final byte UV_MIRROR = 2;
    public static final byte ORIGIN_PARENT = 0;
    public static final byte ORIGIN_SURFACE = 1;
    public static final byte ORIGIN_SCREEN = 2;
    public static final byte ANCHOR_TOP_LEFT = 0;
    public static final byte ANCHOR_TOP_MIDDLE = 1;
    public static final byte ANCHOR_TOP_RIGHT = 2;
    public static final byte ANCHOR_MIDDLE_LEFT = 3;
    public static final byte ANCHOR_CENTER = 4;
    public static final byte ANCHOR_MIDDLE_RIGHT = 5;
    public static final byte ANCHOR_BOTTOM_LEFT = 6;
    public static final byte ANCHOR_BOTTOM_MIDDLE = 7;
    public static final byte ANCHOR_BOTTOM_RIGHT = 8;
    public static final byte INTERPOLATION_LINEAR = 9;
    public static final byte INTERPOLATION_EASE_IN = 10;
    public static final byte INTERPOLATION_EASE_OUT = 11;
    public static final byte INTERPOLATION_EASE_IN_OUT = 11;
}
