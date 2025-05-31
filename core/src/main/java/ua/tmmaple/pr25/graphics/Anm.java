package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ua.tmmaple.pr25.assets.AnmData;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.nio.ByteBuffer;

public final class Anm implements Disposable {
    public static final byte[] ANM_MAGIC = { '%', 'A', 'N', 'M' };
    public static final byte ANM_VERSION =  0x01;
    public static final int ANM_FLAG_VISIBLE = 1 << 0;
    public static final int ANM_FLAG_FLIP_X = 1 << 1;
    public static final int ANM_FLAG_FLIP_Y = 1 << 2;
    public static final int ANM_FLAG_AUTOROTATE = 1 << 3;
    public static final int ANM_FLAG_TELEPORT = 1 << 4;
    /* Системні інструкції */
    public static final byte ANM_OP_NOP = 0x00;
    public static final byte ANM_OP_DELETE = 0x01;
    public static final byte ANM_OP_STOP = 0x02;
    public static final byte ANM_OP_PAUSE = 0x03;
    public static final byte ANM_OP_HIDE_PAUSE = 0x04;
    public static final byte ANM_OP_INTERRUPT = 0x05;
    public static final byte ANM_OP_SLEEP = 0x06;
    public static final byte ANM_OP_RETURN = 0x07;
    public static final byte ANM_OP_JUMP = 0x08;
    /* Інструкції налаштування текстури */
    public static final byte ANM_OP_SOURCE = 0x09;
    public static final byte ANM_OP_UV_POSITION = 0x0A;
    public static final byte ANM_OP_UV_SCALE = 0x0B;
    public static final byte ANM_OP_UV_SCROLLING_X = 0x0C;
    public static final byte ANM_OP_UV_SCROLLING_Y = 0x0D;
    public static final byte ANM_OP_UV_MOVE = 0x0E;
    public static final byte ANM_OP_UV_RESCALE = 0x0F;
    public static final byte ANM_OP_UV_MODE = 0x10;
    /* Інструкції налаштування рендерингу */
    public static final byte ANM_OP_COLOR = 0x11;
    public static final byte ANM_OP_ALPHA = 0x12;
    public static final byte ANM_OP_CHANGE_COLOR = 0x13;
    public static final byte ANM_OP_FADE = 0x14;
    public static final byte ANM_OP_BLENDING = 0x15;
    public static final byte ANM_OP_VISIBLE = 0x16;
    public static final byte ANM_OP_FLIP_X = 0x17;
    public static final byte ANM_OP_FLIP_Y = 0x18;
    /* Інструкції переміщення */
    public static final byte ANM_OP_POSITION = 0x19;
    public static final byte ANM_OP_ANGLE = 0x1A;
    public static final byte ANM_OP_SCALE = 0x1B;
    public static final byte ANM_OP_MOVE = 0x1C;
    public static final byte ANM_OP_ROTATE = 0x1D;
    public static final byte ANM_OP_GROW = 0x1E;
    public static final byte ANM_OP_AUTOROTATE = 0x1F;
    public static final byte ANM_OP_ANGULAR_SPEED = 0x20;
    public static final byte ANM_OP_ORIGIN_MODE = 0x21;
    public static final byte ANM_OP_ANCHOR_MODE = 0x22;
    public static final byte ANM_OP_ANCHOR_OFFSET = 0x23;
    /* Константи */
    public static final byte ANM_UV_NONE = 0;
    public static final byte ANM_UV_REPEAT = 1;
    public static final byte ANM_UV_MIRROR = 2;
    public static final byte ANM_ORIGIN_PARENT = 0;
    public static final byte ANM_ORIGIN_SURFACE = 1;
    public static final byte ANM_ANCHOR_TOP_LEFT = 0;
    public static final byte ANM_ANCHOR_TOP_MIDDLE = 1;
    public static final byte ANM_ANCHOR_TOP_RIGHT = 2;
    public static final byte ANM_ANCHOR_MIDDLE_LEFT = 3;
    public static final byte ANM_ANCHOR_CENTER = 4;
    public static final byte ANM_ANCHOR_MIDDLE_RIGHT = 5;
    public static final byte ANM_ANCHOR_BOTTOM_LEFT = 6;
    public static final byte ANM_ANCHOR_BOTTOM_MIDDLE = 7;
    public static final byte ANM_ANCHOR_BOTTOM_RIGHT = 8;
    /* Таблиця розмірів інструкцій в байтах */
    public static final byte[] ANM_INSTRUCTION_SIZES = {
        3, 3, 3, 3, 3, 4, 7, 3, 7,
        4, 11, 11, 7, 7, 16, 16, 4,
        15, 7, 20, 12, 4, 4, 4, 4,
        11, 7, 11, 16, 12, 16, 4, 7, 4, 4, 11,
    };

    private Texture[] imports;
    private TextureRegion[] sources;
    private AnmScript[] scripts;
    private boolean ownsTextures;

    ByteBuffer bytecode;

    public Anm(Texture[] imports, TextureRegion[] sources, AnmScript[] scripts, byte[] data) {
        this.imports = imports;
        this.sources = sources;
        this.scripts = scripts;
        bytecode = ByteBuffer.wrap(data);
        ownsTextures = false;
    }

    public Anm(String filename) {
        fromData(new AnmData(Gdx.files.internal(filename)));
    }

    public Anm(FileHandle file) {
        fromData(new AnmData(file));
    }

    public Anm(byte[] data) {
        fromData(new AnmData(data));
    }

    public Anm(AnmData data) {
        fromData(data);
    }

    public TextureRegion getSource(int id) {
        if (id < 0 || id >= sources.length)
            return null;
        return new TextureRegion(sources[id]);
    }

    public AnmScript getScript(String id) {
        for (AnmScript script : scripts)
            if (script.name.equals(id)) return script;
        throw new PR25RuntimeException("No script found with name " + id);
    }

    private void fromData(AnmData data) {
        imports = new Texture[data.imports()];
        for (int i = 0; i < data.imports(); ++i)
            imports[i] = new Texture(data.getImport(i));
        ownsTextures = true;
        sources = new TextureRegion[data.sources()];
        for (int i = 0; i < sources.length; ++i) {
            AnmData.AnmSource source = data.getSource(i);
            sources[i] = new TextureRegion(imports[source.i], source.x, source.y, source.width, source.height);
        }
        scripts = data.scripts();
        bytecode = ByteBuffer.wrap(data.data);
    }

    @Override
    public void dispose() {
        if (ownsTextures)
            for (Texture i : imports)
                i.dispose();
    }

    public static class AnmScript {
        public final String name;
        public final int start;
        public final int end;

        public AnmScript(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }
}
