package ua.tmmaple.pr25.assets;

import com.badlogic.gdx.files.FileHandle;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.util.PR25RuntimeException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class AnmData {
    private String[] imports;
    private AnmSource[] sources;
    private Anm.AnmScript[] scripts;

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

    public int imports() {
        return imports == null ? 0 : imports.length;
    }

    public int sources() {
        return sources == null ? 0 : sources.length;
    }

    public Anm.AnmScript[] scripts() {
        return scripts;
    }

    public String getImport(int i) {
        if (i < 0 || i >= imports.length) throw new PR25RuntimeException("Invalid index: " + i);
        return imports[i];
    }

    public AnmSource getSource(int i) {
        if (i < 0 || i >= sources.length) throw new PR25RuntimeException("Invalid index: " + i);
        return sources[i];
    }

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

    private void readHeader() {
        try {
            byte[] magic = new byte[4];
            if (stream.read(magic) < 4 || !Arrays.equals(magic, Anm.ANM_MAGIC))
                throw new PR25RuntimeException("Invalid ANM magic");
            byte version = stream.readByte();
            if (version != Anm.ANM_VERSION) throw new PR25RuntimeException("Invalid ANM version: expected " + Anm.ANM_VERSION + " but got " + version);
        } catch (IOException e) {
            throw new PR25RuntimeException(e);
        }
        position += 5;
    }

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

    private void readScripts() {
        try {
            int count = stream.readUnsignedByte();
            position += 1;
            if (count == 0) return;
            scripts = new Anm.AnmScript[count];
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
                scripts[i] = new Anm.AnmScript(name, start, position);
            }
        } catch (IOException e) {
            throw new PR25RuntimeException(e);
        }
    }

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
