package ua.tmmaple.pr25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.AnmGraphics;
import ua.tmmaple.pr25.graphics.AnmVM;
import ua.tmmaple.pr25.i18n.Language;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends ApplicationAdapter {
    public static final int BASE_WINDOW_WIDTH = 640;
    public static final int BASE_WINDOW_HEIGHT = 480;

    public static final float UPDATE_DELTA = 1.0f / 60.0f;

    private float accumulator;

    private final AnmVM anmVM;

    private final Flow flow;
    private final Input input;
    private final Assets assets;
    private final God god;
    private final Language language;

    private AnmGraphics anmGraphics;
    private Anm anm;

    public Game() {
        anmVM = new AnmVM();

        flow = new Flow();

        input = new Input();
        assets = new Assets();
        god = new God();
        language = new Language();

        accumulator = UPDATE_DELTA;
    }

    @Override
    public void create() {
        AnmVM.initialize(anmVM);

        Flow.initialize(flow);
        Input.register(input);
        Assets.register(assets);
        God.register(god);
        Language.register(language);

        anmGraphics = new AnmGraphics();
        anm = new Anm(Gdx.files.internal("game/plr.anm"));
        anmGraphics.loadAnm(anm);
        anmGraphics.loadScript("PlayerSprite");
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (delta > 0.25f)
            delta = 0.25f;
        accumulator += delta;
        updating:
        while (accumulator >= UPDATE_DELTA) {
            switch (flow.executeUpdate()) {
                case 1: Gdx.app.exit(); break;
                case 2: break updating;
                case -1: System.exit(-1); break;
            }
            if (Input.wasKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE))
                anmGraphics.interrupt((byte) 2);
            anmGraphics.position.add(2.0f, 1.0f);
            anmVM.execute(anmGraphics);
            accumulator -= UPDATE_DELTA;
        }
        delta = Gdx.graphics.getDeltaTime();
        if (delta > 0.25f)
            delta = 0.25f;
        anmVM.update(delta, accumulator / UPDATE_DELTA);
        anmVM.begin();
        switch (flow.executeDraw()) {
            case 1: Gdx.app.exit(); break;
            case 2: break;
            case -1: System.exit(-1); break;
        }
        anmVM.draw(anmGraphics);
        anmVM.end();
    }

    @Override
    public void resize(int width, int height) {
        anmVM.resize(width, height);
    }

    @Override
    public void dispose() {
        anm.dispose();
        Flow.shutdown();
        AnmVM.shutdown();
    }
}
