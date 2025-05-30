package ua.tmmaple.pr25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.AnmVirtualMachine;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.i18n.Language;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends ApplicationAdapter {
    public static final int BASE_WINDOW_WIDTH = 640;
    public static final int BASE_WINDOW_HEIGHT = 480;

    public static final float UPDATE_DELTA = 1.0f / 60.0f;

    private float accumulator;

    private AnmVirtualMachine anmVirtualMachine;
    private Anm anm;

    public Game() {
        GraphicManager.global = new GraphicManager();
        Flow.global = new Flow();

        Assets.global = new Assets();
        Language.global = new Language();
        Input.global = new Input();
        God.global = new God();

        accumulator = UPDATE_DELTA;
    }

    @Override
    public void create() {
        GraphicManager.initialize();

        Assets.register();
        Language.register();
        Input.register();
        God.register();

        anmVirtualMachine = new AnmVirtualMachine();
        anm = new Anm(Gdx.files.internal("game/plr.anm"));
        anmVirtualMachine.loadAnm(anm);
        anmVirtualMachine.loadScript("PlayerSprite");
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
            if (Input.global.wasKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE))
                anmVirtualMachine.interrupt((byte) 2);
            anmVirtualMachine.position.add(2.0f, 1.0f);
            GraphicManager.global.execute(anmVirtualMachine);
            accumulator -= UPDATE_DELTA;
        }
        GraphicManager.global.update(accumulator / UPDATE_DELTA);
        GraphicManager.global.begin();
        switch (Flow.global.executeDraw()) {
            case 1: Gdx.app.exit(); break;
            case 2: break;
            case -1: System.exit(-1); break;
        }
        GraphicManager.global.draw(anmVirtualMachine);
        GraphicManager.global.end();
    }

    @Override
    public void resize(int width, int height) {
        GraphicManager.global.resize(width, height);
    }

    @Override
    public void dispose() {
        anm.dispose();
        Flow.global.shutdown();
        GraphicManager.shutdown();
    }
}
