package ua.tmmaple.pr25.assets;

import ua.tmmaple.pr25.entities.Enemy;
import ua.tmmaple.pr25.task.TimelineTask;

public abstract class Stage {
    public abstract String[] loadList();

    public abstract void reset();

    public abstract TimelineTask main();
}
