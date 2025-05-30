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
