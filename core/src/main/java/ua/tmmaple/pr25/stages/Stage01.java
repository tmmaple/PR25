package ua.tmmaple.pr25.stages;

import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;

public final class Stage01 extends Stage {
    @Override
    public String[] anmList() {
        return new String[] {
            "stages/st01BG.anm",
            "stages/st01E.anm",
        };
    }

    @Override
    public String[] bgmList() {
        return new String[] {

        };
    }

    @Override
    public TimelineTask main() {
        // Тут логіка рівня
        return Task.timeline(

        );
    }

    @Override
    public void reset() { }
}
