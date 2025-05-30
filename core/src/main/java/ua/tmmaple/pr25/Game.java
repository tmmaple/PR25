package ua.tmmaple.pr25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.graphics.TextManager;

public class Game extends ApplicationAdapter {
    public static final int BASE_WINDOW_WIDTH = 640;
    public static final int BASE_WINDOW_HEIGHT = 480;

    public static final float UPDATE_DELTA = 1.0f / 60.0f;

    private float accumulator;

    @Override
    public void create() {
        Audio.global = new Audio();
        GraphicManager.global = new GraphicManager();
        TextManager.global = new TextManager();
        Flow.global = new Flow();

        Assets.global = new Assets();
        God.global = new God();

        accumulator = UPDATE_DELTA;

        Audio.global.initialize();
        GraphicManager.global.initialize();
        TextManager.global.initialize();

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
        TextManager.global.begin();
        switch (Flow.global.executeDraw()) {
            case 1: Gdx.app.exit(); break;
            case 2: break;
            case -1: System.exit(-1); break;
        }
        TextManager.global.end();
        GraphicManager.global.end();
    }

    @Override
    public void resize(int width, int height) {
        GraphicManager.global.resize(width, height);
    }

    @Override
    public void dispose() {
        Flow.global.shutdown();
        TextManager.global.shutdown();
        GraphicManager.global.shutdown();
        Audio.global.shutdown();
    }
}
