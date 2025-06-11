package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.audio.Bgm;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.task.TimelineTask;

public final class StageManager {
    public static StageManager global;

    private static Flow.FlowNode<StageManager> node = null;

    public Anm[] anms;
    public String[] bgms;

    private Stage stage;
    private Enemy root;

    public static void register() {
        node = new Flow.FlowNode<>(global, StageManager::update);
        Flow.global.addToUpdate(node, 20);
    }

    public static void shutdown() {
        Flow.global.cut(node);
        node = null;
    }

    public void load(Stage stage) {
        this.stage = stage;
        String[] anmList = stage.anmList();
        for (String anm : anmList)
            Assets.global.load(Anm.class, anm);
        bgms = stage.bgmList();
        for (String bgm : bgms)
            Assets.global.load(Bgm.class, bgm);
    }

    private int update() {
        if (Assets.global.isLoaded() && root == null) {
            String[] anmList = stage.anmList();
            anms = new Anm[anmList.length];
            for (int i = 0; i < anmList.length; ++i)
                this.anms[i] = Assets.global.get(Anm.class, anmList[i]);
            stage.init(this, Background.global);
            root = EnemyManager.global.createEnemy(stage.main(), 0.0f, 0.0f, null, 1);
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
