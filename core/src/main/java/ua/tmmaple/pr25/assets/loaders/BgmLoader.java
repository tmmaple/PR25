package ua.tmmaple.pr25.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.audio.Bgm;

public final class BgmLoader extends AsynchronousAssetLoader<Bgm, BgmLoader.BgmParam> {
    private Bgm bgm;

    public BgmLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BgmParam parameter) {
        bgm = new Bgm(file);
    }

    @Override
    public Bgm loadSync(AssetManager manager, String fileName, FileHandle file, BgmParam parameter) {
        Bgm bgm = this.bgm;
        this.bgm = null;
        return bgm;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BgmParam parameter) {
        return null;
    }


    public static final class BgmParam extends AssetLoaderParameters<Bgm> { }
}
