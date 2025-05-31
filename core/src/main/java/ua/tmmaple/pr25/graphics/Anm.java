package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Набір скриптів для використання {@link ua.tmmaple.pr25.graphics.GraphicManager.AnmVirtualMachine}.
 * @author uwuhasmile
 */
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

    /**
     * @return регіон текстури за id, або null, якщо такого id не існує
     * @author uwuhasmile
     */
    public TextureRegion getSource(int id) {
        if (id < 0 || id >= sources.length)
            return null;
        return new TextureRegion(sources[id]);
    }

    /**
     * @return скрипт за id
     * @throws PR25RuntimeException якщо скрипта не існує
     * @author uwuhasmile
     */
    public AnmScript getScript(String id) {
        for (AnmScript script : scripts)
            if (script.name.equals(id)) return script;
        throw new PR25RuntimeException("No script found with name " + id);
    }

    /**
     * Генерує з прочитаних даних про шляхи до текстур та джерела.
     * @author uwuhasmile
     */
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

    /**
     * Скрипт ANM, що містить назву, позицію першої інструкції в байтах, та позицію кінця скрипту в байтах.
     * @author uwuhasmile
     */
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

    /**
     * Метадані для завантаження ANM - шляхи текстур, дані про регіони, та скрипти.
     * @author uwuhasmile
     */
    public static final class AnmData {
        private String[] imports;
        private AnmSource[] sources;
        private AnmScript[] scripts;

        public final byte[] data;
        private final DataInputStream stream;
        private int position;

        public AnmData(FileHandle file) {
            data = file.readBytes();
            stream = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(data), 256));
            parse();
        }

        public AnmData(byte[] data) {
            this.data = data;
            stream = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(data), 256));
            parse();
        }

        /**
         * @return кількість імпортованих текстур
         * @author uwuhasmile
         */
        public int imports() {
            return imports == null ? 0 : imports.length;
        }

        /**
         * @return кількість регіонів текстур
         * @author uwuhasmile
         */
        public int sources() {
            return sources == null ? 0 : sources.length;
        }

        /**
         * @return скрипти
         * @author uwuhasmile
         */
        public AnmScript[] scripts() {
            return scripts;
        }

        /**
         * @return шлях до текстури за i
         * @throws PR25RuntimeException якщо текстури не існує
         * @author uwuhasmile
         */
        public String getImport(int i) {
            if (i < 0 || i >= imports.length) throw new PR25RuntimeException("Invalid index: " + i);
            return imports[i];
        }

        /**
         * @return шлях до регіону текстури за i
         * @throws PR25RuntimeException якщо регіону не існує
         * @author uwuhasmile
         */
        public AnmSource getSource(int i) {
            if (i < 0 || i >= sources.length) throw new PR25RuntimeException("Invalid index: " + i);
            return sources[i];
        }

        /**
         * Читає дані ANM у вигляді байтів
         * @throws PR25RuntimeException якщо сталась помилка читання, або дані некоректні
         * @author uwuhasmile
         */
        private void parse() {
            readHeader();
            readImports();
            readSources();
            readScripts();
            try {
                stream.close();
            } catch (IOException e) {
                throw new PR25RuntimeException(e);
            }
        }

        /**
         * Читає заголовок.
         * @throws PR25RuntimeException якщо неправильне магічне число або версія
         * @author uwuhasmile
         */
        private void readHeader() {
            try {
                byte[] magic = new byte[4];
                if (stream.read(magic) < 4 || !Arrays.equals(magic, ANM_MAGIC))
                    throw new PR25RuntimeException("Invalid ANM magic");
                byte version = stream.readByte();
                if (version != ANM_VERSION) throw new PR25RuntimeException("Invalid ANM version: expected " + ANM_VERSION + " but got " + version);
            } catch (IOException e) {
                throw new PR25RuntimeException(e);
            }
            position += 5;
        }

        /**
         * Читає шляхи до текстур.
         * @throws PR25RuntimeException якщо сталась помилка читання
         * @author uwuhasmile
         */
        private void readImports() {
            try {
                position += 1;
                int count = stream.readUnsignedByte();
                if (count == 0) return;
                imports = new String[count];
                for (int i = 0; i < count; ++i) {
                    int size = stream.readUnsignedByte();
                    position += 1;
                    byte[] source = new byte[size];
                    if (size < stream.read(source)) throw new PR25RuntimeException("Failed to read ANM import");
                    position += size;
                    imports[i] = new String(source, StandardCharsets.US_ASCII);
                }
            } catch (IOException e) {
                throw new PR25RuntimeException(e);
            }
        }

        /**
         * Читає регіони.
         * @throws PR25RuntimeException якщо сталась помилка читання
         * @author uwuhasmile
         */
        private void readSources() {
            try {
                int count = stream.readUnsignedByte();
                position += 1;
                if (count == 0) return;
                sources = new AnmSource[count];
                for (int i = 0; i < count; ++i) {
                    int im = stream.readUnsignedByte();
                    int x = stream.readInt();
                    int y = stream.readInt();
                    int width = stream.readInt();
                    int height = stream.readInt();
                    position += 17;
                    sources[i] = new AnmSource(im, x, y, width, height);
                }
            } catch (IOException e) {
                throw new PR25RuntimeException(e);
            }
        }

        /**
         * Читає скрипти.
         * @throws PR25RuntimeException якщо сталась помилка читання, або заголовок скрипта має неправильні дані
         * @author uwuhasmile
         */
        private void readScripts() {
            try {
                int count = stream.readUnsignedByte();
                position += 1;
                if (count == 0) return;
                scripts = new AnmScript[count];
                for (int i = 0; i < count; ++i) {
                    int size = stream.readUnsignedByte();
                    position += 1;
                    byte[] script = new byte[size];
                    if (size < stream.read(script)) throw new PR25RuntimeException("Failed to read ANM script name");
                    String name = new String(script, StandardCharsets.US_ASCII);
                    position += size;
                    size = stream.readInt();
                    position += 4;
                    int start = position;
                    if (stream.skipBytes(size) < size) throw new PR25RuntimeException("Invalid ANM size");
                    position += size;
                    scripts[i] = new AnmScript(name, start, position);
                }
            } catch (IOException e) {
                throw new PR25RuntimeException(e);
            }
        }

        /**
         * Регіон текстури ANM.
         * Містить ідентифікатор текстури, позицію та розмір регіону.
         * @author uwuhasmile
         */
        public static class AnmSource {
            public final int i;
            public final int x;
            public final int y;
            public final int width;
            public final int height;

            private AnmSource(int i, int x, int y, int width, int height) {
                this.i = i;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }
        }
    }
}
