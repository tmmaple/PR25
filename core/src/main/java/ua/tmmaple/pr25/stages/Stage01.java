package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.BulletManager;
import ua.tmmaple.pr25.entities.Gun;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;
import ua.tmmaple.pr25.util.Tweener;

public final class Stage01 extends Stage {

    @Override
    public String[] anmList() {
        return new String[]{
            "stages/st01BG.anm",
            "stages/st01E.anm",
        };
    }

    @Override
    public String[] bgmList() {
        return new String[]{
            "bgm/st01.bgm",
            "bgm/st01boss.bgm",
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
                    playMusic(0);
                    return true;
                }
            ),
            Task.keyframe(
                100,
                en -> {
                    background.setCameraLimits(128.0f, 192.0f, true);
                    en.createChildAbsolute(dragonflyB(50), -100.0f, 20.0f, 20);
                    en.createChildAbsolute(dragonflyB(60), 100.0f, 20.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                350,
                en -> {
                    en.createChildAbsolute(dragonflyB(100), -150.0f, 50.0f, 20);
                    en.createChildAbsolute(dragonflyB(60), 0.0f, 20.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                450,
                en -> {
                    en.createChildAbsolute(dragonflyB(70), -80.0f, 40.0f, 20);
                    en.createChildAbsolute(dragonflyB(40), 90.0f, 20.0f, 20);
                    en.createChildAbsolute(dragonflyB(30), 140.0f, 20.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                620,
                en -> {
                    en.createChildAbsolute(dragonflyA(), -70.0f, 90.0f, 20);
                    en.createChildAbsolute(dragonflyA(), 70.0f, 90.0f, 20);
                    en.createChildAbsolute(dragonflyA(), -100.0f, 60.0f, 20);
                    en.createChildAbsolute(dragonflyA(), 100.0f, 60.0f, 20);
                    en.createChildAbsolute(dragonflyA(), -130.0f, 30.0f, 20);
                    en.createChildAbsolute(dragonflyA(), 130.0f, 30.0f, 20);
                    return true;
                }
            ),
            Task.keyframe(
                900,
                en -> {
                    en.createChildAbsolute(bunnyA(), -100.0f, 20.0f, 30);
                    en.createChildAbsolute(bunnyA(), 100.0f, 20.0f, 30);
                    return true;
                }
            ),
            Task.keyframe(
                1200,
                en -> {
                    en.createChildAbsolute(dragonflyA(), -120.0f, 0.0f, 20);
                    en.createChildAbsolute(bunnyB(), 120.0f, 0.0f, 30);
                    return true;
                }
            ),
            Task.keyframe(1500, en -> {
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyC(), -220, random.nextFloat(-30, 30), 20);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyD(), 220, random.nextFloat(-30, 30), 20);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
                return true;
            }),
            Task.keyframe(
                2000,
                en -> {
                    en.addAsyncTask(Task.repeat(
                        () -> (short) 10,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(dragonflyB(random.nextInt(10, 60)), random.nextFloat(-120, 120), 20, 20);
                                return true;
                            },
                            Task.wait(() -> (short) 80)
                        )
                    ));
                    return true;
                }
            ),
            Task.keyframe(2900, en -> {
                BulletManager.global.destroyEnemyBullets();
                en.createChildAbsolute(midBoss(), 0.0f, 30.0f, 400);
                return true;
            }),
            Task.keyframe( 3600, en -> {
                en.createChildAbsolute(dragonflyC(), -200.0f, -30.0f, 20);
                en.createChildAbsolute(dragonflyD(), 200.0f, -30.0f, 20);
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyA(), random.nextFloat(-140, 140), 20.0f, 20);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
               return true;
            }),
            Task.keyframe(4100, en -> {
                en.createChildAbsolute(bunnyB(), -120.0f, 0.0f, 30);
                en.createChildAbsolute(bunnyB(), 120.0f, 0.0f, 30);
                return true;
            }),
            Task.keyframe(4400, en -> {
                en.addAsyncTask(Task.repeat(
                    () -> (short) 8,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyC(), -220, random.nextFloat(-30, 30), 20);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
                en.addAsyncTask(Task.repeat(
                    () -> (short) 8,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyD(), 220, random.nextFloat(-30, 30), 20);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
                en.createChildAbsolute(bunnyA(), 0.0f, 10.0f, 30);
                return true;
            }),
            Task.keyframe(5100, en -> {

                return true;
            }),
            Task.keyframe(5300, en -> {
                BulletManager.global.destroyEnemyBullets();
                return true;
            }),
            Task.keyframe(5400, en -> {
                stopMusic();
                playMusic(1);
                en.createChildAbsolute(boss(), 50.0f, 30.0f, 600);
                return true;
            }),
            Task.keyframe(
                10000,
                en -> true
            )
        );
    }

    private TimelineTask dragonflyA() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(3, 3);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0.0f, 0.0f);
                    en.setGunCount(0, 1, 1);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 0);
                    en.setGunRepeatInterval(0, 40);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask dragonflyB(int flyTime) {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(3, 3);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 4.0f);
                    return true;
                }
            ),
            Task.keyframe(
                flyTime,
                en -> {
                    en.stopMovement();
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0f, 0f);
                    en.setGunCount(0, 1, 1);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 0);
                    en.setGunRepeatInterval(0, 40);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask dragonflyC() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(3, 3);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 4.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 4.0f, random.nextFloat(-MathUtils.HALF_PI, MathUtils.HALF_PI), 20);
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0.0f, MathUtils.degRad * 10.0f);
                    en.setGunCount(0, 1, 1);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 0);
                    en.setGunRepeatInterval(0, 60);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }
    private TimelineTask dragonflyD() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(3, 3);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 4.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 4.0f, random.nextFloat(MathUtils.HALF_PI, MathUtils.HALF_PI+MathUtils.PI), 20);
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, 0.0f, MathUtils.degRad * 10.0f);
                    en.setGunCount(0, 1, 1);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 0);
                    en.setGunRepeatInterval(0, 60);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask bunnyA() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(5, 7);
                    en.setSprite(getAnm(1), "Bunny");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 3.0f);
                    return true;
                }
            ),
            Task.keyframe(
                100,
                en -> {
                    en.stopMovement();
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, MathUtils.degRad * 90.0f, MathUtils.degRad * 10f);
                    en.setGunCount(0, 5, 1);
                    en.setGunSpeed(0, 2.0f, 2.0f);
                    en.setGunAngularSpeed(0, 0.02f, 0.02f);
                    en.setGunRepeating(0, 4);
                    en.setGunRepeatInterval(0, 20);
                    en.turnGunOn(0);
                    en.initGun(1);
                    en.setGunAim(1, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(1, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(1, MathUtils.degRad * 90.0f, MathUtils.degRad * 10f);
                    en.setGunCount(1, 5, 1);
                    en.setGunSpeed(1, 2.0f, 2.0f);
                    en.setGunAngularSpeed(1, -0.02f, -0.02f);
                    en.setGunRepeating(1, 4);
                    en.setGunRepeatInterval(1, 20);
                    en.turnGunOn(1);
                    return true;
                }
            ),
            Task.keyframe(
                260,
                en -> {
                    en.adjustGunAngularSpeed(0, 0f);
                    en.adjustGunAngularSpeed(1, 0f);
                    en.adjustGunAngle(0, -MathUtils.HALF_PI);
                    en.adjustGunAngle(1, -MathUtils.HALF_PI);
                    return true;
                }
            ),
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask bunnyB() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(5, 7);
                    en.setSprite(getAnm(1), "Bunny");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_PLAYER);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_GREEN);
                    en.setGunAngle(0, 0.0f, MathUtils.degRad * 12.0f);
                    en.setGunCount(0, 5, 2);
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
            Task.keyframe(
                100000,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask midBoss() {
        return Task.timeline(
            Task.keyframe(en -> {
                en.setHitbox(64.0f, 64.0f);
                en.setDrop(5, 60);
                en.setSprite(getAnm(1), "Midboss");
                en.setSpriteRotation(true);

                // Плавний спуск на позицію
                en.changePosition(Tweener.INTERPOLATION_EASE_OUT, 0.0f, -100.0f, 60);
                return true;
            }),

            Task.keyframe(100, en -> {
                en.setSpeed(3.0f);
                en.moveCircularly(0.0f, 80.0f);
                en.initGun(0);
                en.setGunAim(0, Gun.Aim.FAN_PLAYER);
                en.setGunBulletType(0, Gun.BulletType.BULLET_16x16_GREEN);
                en.setGunCount(0, 6, 1);
                en.setGunAngle(0, 0.0f, MathUtils.degRad * 20.0f);
                en.setGunSpeed(0, 2.5f, 1.5f);
                en.setGunRepeatInterval(0, 25);
                en.setGunRepeating(0, 0);
                en.turnGunOn(0);
                return true;
            }),

            // Зміна патерну
            Task.keyframe(300, en -> {
                en.turnGunOff(0);
                en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                en.setGunCount(0, 12, 1);
                en.setGunRepeatInterval(0, 40);
                en.turnGunOn(0);
                return true;
            }),

            // Завершити
            Task.keyframe(700, en -> {
                en.destroy(); // зникає перед основним босом
                return true;
            })
        );
    }


    private TimelineTask boss() {
        return Task.timeline(
            Task.keyframe(en -> {
                en.setHitbox(96.0f, 96.0f);
                en.setDrop(10, 100);
                en.setSprite(getAnm(1), "Boss");
                en.setSpriteRotation(true);

                // Плавний спуск вниз
                en.changePosition(Tweener.INTERPOLATION_EASE_OUT, 0.0f, -80.0f, 60);
                return true;
            }),

            Task.keyframe(100, en -> {
                en.setSpeed(2.5f);
                en.moveCircularly(0.0f, 90.0f);
                en.initGun(0);
                en.setGunAim(0, Gun.Aim.RING_PLAYER);
                en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                en.setGunCount(0, 16, 1);
                en.setGunSpeed(0, 2.0f, 1.0f);
                en.setGunRadius(0, 80.0f, 80.0f);
                en.setGunRepeatInterval(0, 40);
                en.setGunRepeating(0, 0);
                en.turnGunOn(0);
                return true;
            }),

            Task.keyframe(160, en -> {
                en.initGun(1);
                en.setGunAim(1, Gun.Aim.FAN_PLAYER);
                en.setGunBulletType(1, Gun.BulletType.BULLET_16x16_BLUE);
                en.setGunCount(1, 5, 1);
                en.setGunAngle(1, 0.0f, MathUtils.degRad * 15.0f);
                en.setGunSpeed(1, 3.5f, 1.0f);
                en.setGunRepeatInterval(1, 20);
                en.setGunRepeating(1, 0);
                en.turnGunOn(1);
                return true;
            }),

            Task.keyframe(500, Task.repeat(
                () -> (short) 999,
                Task.sequence(
                    en -> {
                        en.adjustGunBulletType(1, Gun.BulletType.BULLET_8x12_ORANGE);
                        en.setGunRepeatInterval(1, 10);
                        return true;
                    },
                    Task.wait(() -> (short) 120),
                    en -> {
                        en.adjustGunBulletType(1, Gun.BulletType.BULLET_RINGED_12x12_RED);
                        en.setGunRepeatInterval(1, 30);
                        return true;
                    },
                    Task.wait(() -> (short) 120)
                )
            ))
        );
    }


}
