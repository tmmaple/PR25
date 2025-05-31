package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.util.Tweener;

import java.util.ArrayList;
import java.util.List;

public interface AnmIM {
    Object[] BUILTIN_CONSTANTS = {
        "ANM_UV_NONE", new AnmValue(Anm.ANM_UV_NONE), "ANM_UV_REPEAT", new AnmValue(Anm.ANM_UV_REPEAT), "ANM_UV_MIRROR", new AnmValue(Anm.ANM_UV_MIRROR),
        "ANM_ORIGIN_PARENT", new AnmValue(Anm.ANM_ORIGIN_PARENT), "ANM_ORIGIN_SURFACE", new AnmValue(Anm.ANM_ORIGIN_SURFACE),
        "ANM_ANCHOR_TOP_LEFT", new AnmValue(Anm.ANM_ANCHOR_TOP_LEFT), "ANM_ANCHOR_TOP_MIDDLE", new AnmValue(Anm.ANM_ANCHOR_TOP_MIDDLE), "ANM_ANCHOR_TOP_RIGHT", new AnmValue(Anm.ANM_ANCHOR_TOP_RIGHT),
        "ANM_ANCHOR_MIDDLE_LEFT", new AnmValue(Anm.ANM_ANCHOR_MIDDLE_LEFT), "ANM_ANCHOR_CENTER", new AnmValue(Anm.ANM_ANCHOR_CENTER), "ANM_ANCHOR_MIDDLE_RIGHT", new AnmValue(Anm.ANM_ANCHOR_MIDDLE_RIGHT),
        "ANM_ANCHOR_BOTTOM_LEFT", new AnmValue(Anm.ANM_ANCHOR_BOTTOM_LEFT), "ANM_ANCHOR_BOTTOM_MIDDLE", new AnmValue(Anm.ANM_ANCHOR_BOTTOM_MIDDLE), "ANM_ANCHOR_BOTTOM_RIGHT", new AnmValue(Anm.ANM_ANCHOR_BOTTOM_RIGHT),
        "INTERPOLATION_LINEAR", new AnmValue(Tweener.INTERPOLATION_LINEAR),
        "INTERPOLATION_EASE_IN", new AnmValue(Tweener.INTERPOLATION_EASE_IN),
        "INTERPOLATION_EASE_OUT", new AnmValue(Tweener.INTERPOLATION_EASE_OUT),
        "INTERPOLATION_EASE_IN_OUT", new AnmValue(Tweener.INTERPOLATION_EASE_IN_OUT),
    };

    byte VALUE_TYPE_BYTE = 0;
    byte VALUE_TYPE_INTEGER = 1;
    byte VALUE_TYPE_FLOAT = 2;
    byte VALUE_TYPE_BYTE_OFFSET = 3;

    static AnmProgram createProgram() {
        return new AnmProgram();
    }

    final class AnmProgram {
        public final List<String> imports;
        public final List<AnmSource> sources;

        public final List<AnmScript> scripts;

        private AnmProgram() {
            imports = new ArrayList<>();
            sources = new ArrayList<>();
            scripts = new ArrayList<>();
        }

        public void addImport(String path) {
            if (imports.contains(path)) throw new AnmParserException("Duplicate import: " + path);
            imports.add(path);
        }

        public void addSource(byte from, int x, int y, int width, int height) {
            if (imports.size() <= from) throw new AnmParserException("Invalid source: " + from);
            for (AnmSource source : sources)
                if (source.equals(from, x, y, width, height)) return;
            sources.add(new AnmSource(from, x, y, width, height));
        }

        public AnmScript addScript(String name) {
            for (AnmScript script : scripts)
                if (script.name.equals(name)) throw new AnmParserException("Duplicate script: " + name);
            AnmScript script = new AnmScript(name);
            scripts.add(script);
            return script;
        }
    }

    final class AnmSource {
        public final byte from;
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        private AnmSource(byte from, int x, int y, int width, int height) {
            this.from = from;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean equals(byte from, int x, int y, int width, int height) {
            return this.from == from && this.x == x && this.y == y && this.width == width && this.height == height;
        }
    }

    final class AnmScript {
        public final String name;
        public final List<AnmInstruction> instructions;

        private AnmScript(String name) {
            this.name = name;
            instructions = new ArrayList<>();
        }
    }

    final class AnmInstruction {
        public final short time;
        public final int opcode;
        public final AnmValue[] args;
        public final int byteOffset;

        public AnmInstruction(short time, int opcode, int byteOffset, AnmValue... args) {
            this.time = time;
            this.opcode = opcode;
            this.args = args;
            this.byteOffset = byteOffset;
        }
    }

    final class AnmValue {
        public final byte type;
        public final int iValue;
        public final float fValue;

        public AnmValue(byte bValue) {
            this(VALUE_TYPE_BYTE, bValue, 0.0f);
        }

        public AnmValue(int iValue) {
            this(VALUE_TYPE_INTEGER, iValue, 0.0f);
        }

        public AnmValue(float fValue) {
            this(VALUE_TYPE_FLOAT, 0, fValue);
        }

        public static AnmValue byteOffset(int iValue) {
            return new AnmValue(VALUE_TYPE_BYTE_OFFSET, iValue, 0.0f);
        }

        private AnmValue(byte type, int iValue, float fValue) {
            this.type = type;
            this.iValue = iValue;
            this.fValue = fValue;
        }

        public byte asByte() {
            if (type == VALUE_TYPE_FLOAT) throw new AnmParserException("Can't convert float to byte");
            return (byte) iValue;
        }

        public int asInteger() {
            if (type == VALUE_TYPE_FLOAT) throw new AnmParserException("Can't convert float to integer");
            return iValue;
        }

        public float asFloat() {
            if (type == VALUE_TYPE_FLOAT) return fValue;
            return (float) iValue;
        }

        public int asByteOffset() {
            if (type != VALUE_TYPE_BYTE_OFFSET) throw new AnmParserException("Not a byte offset");
            return iValue;
        }
    }
}
