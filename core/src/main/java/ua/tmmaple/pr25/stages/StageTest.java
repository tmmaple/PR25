package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.Gun;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
import ua.tmmaple.pr25.util.Tweener;

public class StageTest extends Stage {
    @Override
    public String[] anmList() {
        return new String[] {
            "stages/st01BG.anm",
            "stages/st01E.anm",
        };
    }

    public String[] bgmList() {
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
                    background.load(getAnm(0), "Background");
                    background.setCameraPosition(0.0f);
                    background.cameraVelocity = 0.5f;
                    return true;
                }
            ),
            Task.keyframe(
                200,
                en -> {
                    background.setCameraLimits(128.0f, 192.0f, true);
                    en.createChildAbsolute(enemyA(), -100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyA(), -100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyA(), -120.0f, 0.0f, 20);
                    en.createChildAbsolute(enemyB(), 120.0f, 0.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                600,
                en -> {
                    background.setCameraLimits(128.0f, 192.0f, true);
                    en.createChildAbsolute(enemyA(), -100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyA(), -100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyA(), -120.0f, 0.0f, 20);
                    en.createChildAbsolute(enemyB(), 120.0f, 0.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                900,
                en -> {
                    background.setCameraLimits(128.0f, 192.0f, true);
                    en.createChildAbsolute(enemyA(), -100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyA(), -100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyA(), -120.0f, 0.0f, 20);
                    en.createChildAbsolute(enemyB(), 120.0f, 0.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                1200,
                en -> {
                    background.setCameraLimits(128.0f, 192.0f, true);
                    en.createChildAbsolute(enemyA(), -100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 40.0f, 20);
                    en.createChildAbsolute(enemyA(), -100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyB(), 100.0f, 20.0f, 20);
                    en.createChildAbsolute(enemyA(), -120.0f, 0.0f, 20);
                    en.createChildAbsolute(enemyB(), 120.0f, 0.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                10000,
                en -> true
            )
        );
    }

    private TimelineTask enemyA() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(50, 50);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setHitbox(32, 32);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(
                120,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(10000000,
                Task.wait(() -> (short) 100000)
            )
        );
    }

    private TimelineTask enemyB() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(32.0f, 32.0f);
                    en.setIgnorePlayer(true);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setHitbox(32, 32);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(
                120,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(
                130,
                Task.repeat(
                    () -> (short) 5,
                    Task.sequence(
                        Task.wait(() -> (short) 1)
                    )
                )
            ),
            Task.keyframe(
                130,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(
                200,
                en -> {
                    return true;
                }
            ),
            Task.keyframe(10000000,
                Task.wait(() -> (short) 100000)
            )
        );
    }
}
