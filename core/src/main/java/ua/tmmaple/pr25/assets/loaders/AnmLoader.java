package ua.tmmaple.pr25.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.graphics.Anm;

/**
 * Завантажувач ANM.
 * @author afiliushkin
 */
public final class AnmLoader extends AsynchronousAssetLoader<Anm, AnmLoader.AnmParameter> {
    private Anm.AnmData data;

    public AnmLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, AnmParameter parameter) {
        if (data == null) data = new Anm.AnmData(file);
    }

    @Override
    public Anm loadSync(AssetManager manager, String fileName, FileHandle file, AnmParameter parameter) {
        Texture[] imports = null;
        if (data.imports() > 0) {
            imports = new Texture[data.imports()];
            for (int i = 0; i < imports.length; ++i)
                imports[i] = manager.get(data.getImport(i), Texture.class);
        }
        TextureRegion[] sources = null;
        if (data.sources() > 0) {
            sources = new TextureRegion[data.sources()];
            for (int i = 0; i < sources.length; ++i) {
                Anm.AnmData.AnmSource source = data.getSource(i);
                sources[i] = new TextureRegion(imports[source.i], source.x, source.y, source.width, source.height);
            }
        }
        Anm.AnmScript[] scripts = data.scripts();
        byte[] data = this.data.data;
        Anm result = new Anm(imports, sources, scripts, data);
        this.data = null;
        return result;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AnmParameter parameter) {
        Array<AssetDescriptor> deps = new Array<>();
        if (data == null) data = new Anm.AnmData(file);
        for (int i = 0; i < data.imports(); ++i) {
            String importPath = data.getImport(i);
            FileHandle resolved = resolve(importPath);

            TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
            param.genMipMaps = true;
            param.magFilter = Texture.TextureFilter.MipMapNearestNearest;
            param.minFilter = Texture.TextureFilter.MipMapNearestNearest;
            AssetDescriptor<Texture> descriptor = new AssetDescriptor<>(resolved, Texture.class, param);
            deps.add(descriptor);
        }
        return deps;
    }

    public static class AnmParameter extends AssetLoaderParameters<Anm> { }
}
