package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.stages.StageTest;
import ua.tmmaple.pr25.task.TimelineTask;

public final class StageManager {
    public static StageManager global;

    private static Flow.FlowNode<StageManager> node = null;

    private Stage stage;
    private TimelineTask task;
    public Enemy root;

    public static void register() {
        node = new Flow.FlowNode<>(global, StageManager::update);
        Flow.global.addToUpdate(node, 20);
        global.stage = new StageTest();
        global.task = global.stage.main();
    }

    public static void shutdown() {
        Flow.global.cut(node);
        node = null;
    }

    private int update() {
        if (global.task != null)
            global.task.execute(root);
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
