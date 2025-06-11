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
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0.0f, 0.0f);
                    en.setGunCount(0, 8, 2);
                    en.setGunSpeed(0, 1.0f, 0.5f);
                    en.setGunRepeating(0, 5);
                    en.setGunRepeatInterval(0, 20);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                en -> {
                    en.turnGunOff(0);
                    en.adjustGunSpeed(0, 4.0f);
                    en.adjustGunBulletType(0, Gun.BulletType.BULLET_16x16_BLUE);
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 3.0f, -MathUtils.HALF_PI * 0.25f, 60);
                    return true;
                }
            ),
            Task.keyframe(130,
                Task.repeat(
                    () -> (short) 5,
                    Task.sequence(
                        en -> { en.adjustGunAimAtPlayer(0, 0.0f, 0.0f); return true; },
                        Task.wait(() -> (short) 1)
                    )
                )
            ),
            Task.keyframe(200,
                en -> {
                    en.moveCircularly(-MathUtils.HALF_PI, 50.0f);
                    en.setSpeed(6.0f);
                    en.setGunAim(0, Gun.Aim.FAN_PLAYER);
                    en.setGunSpeed(0, 4.0f, 6.0f);
                    en.setGunRadius(0, 48.0f, 60.0f);
                    en.setGunRepeating(0, 60);
                    en.setGunRepeatInterval(0, 5);
                    en.setGunCount(0, 4, 1);
                    en.setGunAngle(0, 0.0f, MathUtils.degRad * 40.0f);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_8x12_ORANGE);
                    en.setGunAngularSpeed(0, 0.3f / 60.0f, 0.2f / 60.0f);
                    en.initGun(1);
                    en.setGunAim(1, Gun.Aim.RING_PLAYER);
                    en.setGunCount(1, 20, 1);
                    en.setGunRadius(1, 64.0f, 64.0f);
                    en.setGunSpeed(1, 4.0f, 2.0f);
                    en.setGunBulletType(1, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunRepeatInterval(1, 30);
                    en.setGunRepeating(1, 0);
                    en.turnGunOn(0);
                    en.turnGunOn(1);
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
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0.0f, MathUtils.degRad * 12.0f);
                    en.setGunCount(0, 8, 2);
                    en.setGunSpeed(0, 1.0f, 0.5f);
                    en.setGunRepeating(0, 5);
                    en.setGunRepeatInterval(0, 20);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                en -> {
                    en.turnGunOff(0);
                    en.adjustGunSpeed(0, 4.0f);
                    en.adjustGunBulletType(0, Gun.BulletType.BULLET_16x16_GREEN);
                    en.rotate(Tweener.INTERPOLATION_LINEAR, -MathUtils.HALF_PI * 0.25f, 60);
                    return true;
                }
            ),
            Task.keyframe(
                130,
                Task.repeat(
                    () -> (short) 5,
                    Task.sequence(
                        en -> { en.adjustGunAimAtPlayer(0, 0.0f, 0.0f); return true; },
                        Task.wait(() -> (short) 1)
                    )
                )
            ),
            Task.keyframe(
                130,
                en -> {
                    en.adjustGunAngularSpeed(0, MathUtils.degRad * 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                200,
                en -> {
                    en.destroyGunBullets(0);
                    return true;
                }
            ),
            Task.keyframe(10000000,
                Task.wait(() -> (short) 100000)
            )
        );
    }
}
