package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.Enemy;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
import ua.tmmaple.pr25.util.Tweener;

public class StageTest extends Stage {
    @Override
    public String[] loadList() {
        return new String[] {

        };
    }

    @Override
    public void reset() {

    }

    @Override
    public TimelineTask main() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.changeAngle(Tweener.INTERPOLATION_LINEAR, -MathUtils.HALF_PI, (short) 0);
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 2.0f, (short) 0);
                    return true;
                }
            ),
            Task.keyframe(
                (short) 60,
                en -> {
                    en.setLinearMove(Tweener.INTERPOLATION_LINEAR, 0.0f, 4.0f, (short) 50);
                    return true;
                }
            ),
            Task.keyframe((short) 60, Task.wait(() -> (short) 10000))
        );
    }
}
