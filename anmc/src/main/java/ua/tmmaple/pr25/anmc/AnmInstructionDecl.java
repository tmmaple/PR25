package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.graphics.AnmVM;

public final class AnmInstructionDecl {
    public static final AnmInstructionDecl[] INSTRUCTION_TABLE = {
        new AnmInstructionDecl("nop"),
        new AnmInstructionDecl("delete"),
        new AnmInstructionDecl("stop"),
        new AnmInstructionDecl("pause"),
        new AnmInstructionDecl("hidePause"),
        new AnmInstructionDecl("interrupt", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("sleep", AnmIM.VALUE_TYPE_INTEGER),
        new AnmInstructionDecl("return"),
        new AnmInstructionDecl("jump", AnmIM.VALUE_TYPE_BYTE_OFFSET),

        new AnmInstructionDecl("source", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("uvPosition", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("uvScale", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("uvScrollingX", AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("uvScrollingY", AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("uvMove", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("uvRescale", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("uvMode", AnmIM.VALUE_TYPE_BYTE),

        new AnmInstructionDecl("color", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("alpha", AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("changeColor", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("fade", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("blending", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("visible", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("flipX", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("flipY", AnmIM.VALUE_TYPE_BYTE),

        new AnmInstructionDecl("position", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("angle", AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("scale", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("move", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("rotate", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("grow", AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("autorotate", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("angularSpeed", AnmIM.VALUE_TYPE_FLOAT),
        new AnmInstructionDecl("originMode", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("anchorMode", AnmIM.VALUE_TYPE_BYTE),
        new AnmInstructionDecl("anchorOffset", AnmIM.VALUE_TYPE_FLOAT, AnmIM.VALUE_TYPE_FLOAT),
    };
    private static final byte[] KEYWORD_LOOKUP_TABLE = {
        0, 0,
        1, 2, 3, 4, 5, 6, 7, 8,
    };

    public final String name;
    public final byte[] args;

    private AnmInstructionDecl(String name, byte... args) {
        this.name = name;
        this.args = args;
    }

    public static int size(int opcode) {
        if (opcode >= AnmVM.ANM_INSTRUCTION_SIZES.length) throw new AnmParserException("Invalid opcode: " + opcode);
        return AnmVM.ANM_INSTRUCTION_SIZES[opcode];
    }

    public static int size(String name) {
        for (int i = 0; i < INSTRUCTION_TABLE.length; ++i)
            if (INSTRUCTION_TABLE[i].name.equals(name)) return AnmVM.ANM_INSTRUCTION_SIZES[i];
        return 0;
    }

    public static byte byKeyword(int keyword) {
        if (keyword < KEYWORD_LOOKUP_TABLE.length) return KEYWORD_LOOKUP_TABLE[keyword];
        return 0;
    }

    public static byte[] args(int opcode) {
        if (opcode >= INSTRUCTION_TABLE.length) throw new AnmParserException("Invalid opcode: " + opcode);
        return INSTRUCTION_TABLE[opcode].args;
    }

    public static byte sizeByKeyword(int keyword) {
        if (keyword < KEYWORD_LOOKUP_TABLE.length) return AnmVM.ANM_INSTRUCTION_SIZES[KEYWORD_LOOKUP_TABLE[keyword]];
        return 0;
    }

    public static byte find(String name) {
        for (byte i = 0; i < INSTRUCTION_TABLE.length; ++i)
            if (INSTRUCTION_TABLE[i].name.equals(name)) return i;
        return 0;
    }
}
