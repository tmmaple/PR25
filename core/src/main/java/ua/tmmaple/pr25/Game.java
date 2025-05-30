package ua.tmmaple.pr25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.GraphicManager;

public class Game extends ApplicationAdapter {
    public static final int BASE_WINDOW_WIDTH = 640;
    public static final int BASE_WINDOW_HEIGHT = 480;

    public static final float UPDATE_DELTA = 1.0f / 60.0f;

    private float accumulator;

    @Override
    public void create() {
        Audio.global = new Audio();
        Audio.global.loadSounds();
        GraphicManager.global = new GraphicManager();
        Flow.global = new Flow();

        Assets.global = new Assets();
        God.global = new God();

        accumulator = UPDATE_DELTA;

        GraphicManager.initialize();

        Assets.register();
        God.register();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (delta > 0.25f)
            delta = 0.25f;
        accumulator += delta;
        updating:
        while (accumulator >= UPDATE_DELTA) {
            switch (Flow.global.executeUpdate()) {
                case 1: Gdx.app.exit(); break;
                case 2: break updating;
                case -1: System.exit(-1); break;
            }
            accumulator -= UPDATE_DELTA;
        }
        GraphicManager.global.update(accumulator / UPDATE_DELTA);
        GraphicManager.global.begin();
        switch (Flow.global.executeDraw()) {
            case 1: Gdx.app.exit(); break;
            case 2: break;
            case -1: System.exit(-1); break;
        }
        GraphicManager.global.end();
    }

    @Override
    public void resize(int width, int height) {
        GraphicManager.global.resize(width, height);
    }

    @Override
    public void dispose() {
        Flow.global.shutdown();
        GraphicManager.shutdown();
        Audio.global.shutdown();
    }
}
