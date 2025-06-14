package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.audio.Audio;
import ua.tmmaple.pr25.audio.Bgm;
import ua.tmmaple.pr25.graphics.Anm;

public final class StageManager {
    public static StageManager global;

    private static Flow.FlowNode<StageManager> node = null;

    public Anm[] anms;
    public String[] bgms;

    private Stage stage;
    private Enemy root;
    private boolean loading;

    public static void register() {
        node = new Flow.FlowNode<>(global, StageManager::update);
        node.removedListener = StageManager::removed;
        Flow.global.addToUpdate(node, 12);
    }

    public static void shutdown() {
        Flow.global.cut(node);
        node = null;
    }

    public void load(Stage stage) {
        unload();
        loading = true;
        this.stage = stage;
        String[] anmList = stage.anmList();
        for (String anm : anmList)
            Assets.global.load(Anm.class, anm);
        bgms = stage.bgmList();
        for (String bgm : bgms)
            Assets.global.load(Bgm.class, bgm);
    }

    public void unload() {
        if (stage == null)
            return;
        Audio.global.stopMusic();
        Background.global.unload();
        EnemyManager.global.clear();
        BulletManager.global.clear();
        anms = null;
        bgms = null;
        String[] anms = stage.anmList();
        for (String anm : anms)
            Assets.global.unload(anm);
        String[] bgms = stage.bgmList();
        for (String bgm : bgms)
            Assets.global.unload(bgm);
        stage = null;
        root = null;
    }

    public boolean isActive() {
        return !loading && root != null;
    }

    private int update() {
        if (root != null && !root.active)
            root = null;
        if (loading && Assets.global.isLoaded()) {
            loading = false;
            String[] anmList = stage.anmList();
            anms = new Anm[anmList.length];
            for (int i = 0; i < anmList.length; ++i)
                this.anms[i] = Assets.global.get(Anm.class, anmList[i]);
            stage.init(this, Background.global);
            root = EnemyManager.global.createEnemy(stage.main(), 0.0f, 0.0f, null, 1);
            root.setInvincible(true);
            root.setCollision(false);
            Player.global.respawn();
        }
        return Flow.FLOW_RESULT_CONTINUE;
    }

    private int removed() {
        unload();
        return 0;
    }
}
