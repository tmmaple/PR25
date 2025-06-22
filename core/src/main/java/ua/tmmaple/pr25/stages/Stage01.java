package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.BulletManager;
import ua.tmmaple.pr25.entities.Gun;
import ua.tmmaple.pr25.entities.VfxManager;
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
                200,
                Task.sequence(
                    en -> {
                        background.setCameraLimits(128.0f, 192.0f, true);
                        en.createChildAbsolute(dragonflyB(40), -100.0f, 20.0f, 1);
                        en.createChildAbsolute(dragonflyB(50), 100.0f, 20.0f, 1);
                        return true;
                    },
                    Task.wait(() -> (short) 10),
                    en -> {
                        background.setCameraLimits(128.0f, 192.0f, true);
                        en.createChildAbsolute(dragonflyB(40), -80.0f, 20.0f, 1);
                        en.createChildAbsolute(dragonflyB(50), 80.0f, 20.0f, 1);
                        return true;
                    },
                    Task.wait(() -> (short) 10),
                    en -> {
                        background.setCameraLimits(128.0f, 192.0f, true);
                        en.createChildAbsolute(dragonflyB(40), -60.0f, 20.0f, 1);
                        en.createChildAbsolute(dragonflyB(50), 60.0f, 20.0f, 1);
                        return true;
                    },
                    Task.wait(() -> (short) 10),
                    en -> {
                        background.setCameraLimits(128.0f, 192.0f, true);
                        en.createChildAbsolute(dragonflyB(40), -50.0f, 20.0f, 1);
                        en.createChildAbsolute(dragonflyB(50), 50.0f, 20.0f, 1);
                        return true;
                    },
                    Task.wait(() -> (short) 10)
                )
            ),
            Task.keyframe(
                350,
                en -> {
                    en.createChildAbsolute(dragonflyB(30), -120.0f, 50.0f, 4);
                    en.createChildAbsolute(dragonflyB(50), 0.0f, 20.0f, 4);
                    return true;
                }
            ),
            Task.keyframe(
                450,
                en -> {
                    en.createChildAbsolute(dragonflyB(30), -80.0f, 40.0f, 4);
                    en.createChildAbsolute(dragonflyB(20), 120.0f, 20.0f, 4);
                    return true;
                }
            ),
            Task.keyframe(
                900,
                en -> {
                    en.createChildAbsolute(bunnyA(), -100.0f, 20.0f, 10);
                    en.createChildAbsolute(bunnyA(), 100.0f, 20.0f, 10);
                    return true;
                }
            ),
            Task.keyframe(
                1200,
                en -> {
                    en.createChildAbsolute(dragonflyA(), -120.0f, 0.0f, 4);
                    en.createChildAbsolute(bunnyB(), 120.0f, 0.0f, 10);
                    return true;
                }
            ),
            Task.keyframe(1500, en -> {
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyC(), -220, random.nextFloat(-30, 30), 10);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyD(), 220, random.nextFloat(-30, 30), 10);
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
                        () -> (short) 9,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(dragonflyB(random.nextInt(10, 50)), random.nextFloat(-120, 120), 20, 10);
                                return true;
                            },
                            Task.wait(() -> (short) 80)
                        )
                    ));
                    return true;
                }
            ),
            Task.keyframe(2900, en -> {
                en.createChildAbsolute(midBoss(), 0.0f, 30.0f, 500);
                return true;
            }),
            Task.keyframe( 3600, en -> {
                en.createChildAbsolute(dragonflyC(), -220.0f, -30.0f, 4);
                en.createChildAbsolute(dragonflyD(), 220.0f, -30.0f, 4);
                en.addAsyncTask(Task.repeat(
                    () -> (short) 6,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyA(), random.nextFloat(-120, 120), 20.0f, 4);
                            return true;
                        },
                        Task.wait(() -> (short) 80)
                    )
                ));
               return true;
            }),
            Task.keyframe(
                3800,
                en -> {
                    en.createChildAbsolute(dragonflyA(), -60.0f, 90.0f, 4);
                    en.createChildAbsolute(dragonflyA(), 60.0f, 90.0f, 4);
                    en.createChildAbsolute(dragonflyA(), -80.0f, 60.0f, 4);
                    en.createChildAbsolute(dragonflyA(), 80.0f, 60.0f, 4);
                    en.createChildAbsolute(dragonflyA(), -100.0f, 30.0f, 4);
                    en.createChildAbsolute(dragonflyA(), 100.0f, 30.0f, 4);
                    return true;
                }
            ),
            Task.keyframe(4100, en -> {
                en.createChildAbsolute(bunnyB(), -120.0f, 0.0f, 10);
                en.createChildAbsolute(bunnyB(), 120.0f, 0.0f, 10);

                return true;
            }),
            Task.keyframe(4400, en -> {
                en.createChildAbsolute(bunnyA(), 0.0f, 10.0f, 10);
                en.createChildAbsolute(bunnyB(), 100.0f, 20.0f, 10);
                en.createChildAbsolute(bunnyB(), -100.0f, 20.0f, 10);
                return true;
            }),
            Task.keyframe(4700, en -> {
                en.addAsyncTask(Task.repeat(
                    () -> (short) 8,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyC(), -230, random.nextFloat(-30, 30), 10);
                            return true;
                        },
                        Task.wait(() -> (short) 60)
                    )
                ));
                en.addAsyncTask(Task.repeat(
                    () -> (short) 8,
                    Task.sequence(
                        e -> {
                            e.createChildAbsolute(dragonflyD(), 230, random.nextFloat(-30, 30), 10);
                            return true;
                        },
                        Task.wait(() -> (short) 60)
                    )
                ));
                return true;
            }),
            Task.keyframe(5300, en -> {
                return true;
            }),
            Task.keyframe(5400, en -> {
                stopMusic();
                playMusic(1);
                en.createChildAbsolute(boss(), 50.0f, 30.0f, 2000);
                en.addAsyncTask(Task.sequence(
                    Task.whileLoop(
                        () -> !en.children.isEmpty(),
                        Task.wait(()->(short) 20)
                    ),
                    Task.sequence(
                        e-> {
                            fadeMusic(10);
                            background.resetCameraLimits();
                            return true;
                        },
                        Task.wait(()->(short) 500),
                        e-> {
                            this.nextStage(new Stage02());
                            return true;
                        }
                    )
                ));
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
                    en.setDrop(1, 1
                    );
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
                    en.setGunAim(0, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, -MathUtils.HALF_PI, MathUtils.degRad * 30);
                    en.setGunCount(0, 2, 3);
                    en.setGunSpeed(0, 4f, 3f);
                    en.setGunRepeating(0, 3);
                    en.setGunRepeatInterval(0, 80);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                en -> {
                    en.changeVelocity(Tweener.INTERPOLATION_EASE_IN, 3f, 0f, 50);
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
                    en.setDrop(1, 1);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 3.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.stopMovement();
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.RING_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, -MathUtils.HALF_PI, MathUtils.degRad * 30);
                    en.setGunCount(0, 4, 1);
                    en.setGunSpeed(0, 2f, 1f);
                    en.setGunRepeating(0, 6);
                    en.setGunRepeatInterval(0, 60);
                    en.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                flyTime,
                en -> {
                    float direction = random.nextBoolean() ? 0 : -MathUtils.PI;
                    en.changeVelocity(Tweener.INTERPOLATION_EASE_IN, 3f, direction, 50);
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
                    en.setDrop(1, 1);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 4.0f, random.nextFloat(-MathUtils.HALF_PI/2, MathUtils.HALF_PI/2), 20);
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, MathUtils.degRad * -20f, MathUtils.degRad * 10.0f);
                    en.setGunCount(0, 6, 3);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 3);
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
                    en.setDrop(1, 1);
                    en.setSprite(getAnm(1), "Dragonfly");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                60,
                en -> {
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 4.0f, random.nextFloat(MathUtils.degRad * 135f, MathUtils.degRad * 215f), 20);
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, MathUtils.degRad * 110f, MathUtils.degRad * 10.0f);
                    en.setGunCount(0, 3, 2);
                    en.setGunSpeed(0, 3f, 4f);
                    en.setGunRepeating(0, 3);
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
                    en.setDrop(1, 2);
                    en.setSprite(getAnm(1), "Bunny");
                    en.setSpriteRotation(true);
                    en.setVelocity(-MathUtils.HALF_PI, 3.0f);
                    return true;
                }
            ),
            Task.keyframe(
                50,
                en -> {
                    en.stopMovement();
                    en.initGun(0);
                    en.setGunAim(0, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(0, MathUtils.degRad * 90.0f, MathUtils.degRad * 10f);
                    en.setGunCount(0, 5, 1);
                    en.setGunSpeed(0, 2.0f, 2.0f);
                    en.setGunAngularSpeed(0, 0.002f, 0.002f);
                    en.setGunRepeating(0, 4);
                    en.setGunRepeatInterval(0, 20);
                    en.turnGunOn(0);
                    en.initGun(1);
                    en.setGunAim(1, Gun.Aim.FAN_STATIC);
                    en.setGunBulletType(1, Gun.BulletType.BULLET_RINGED_12x12_RED);
                    en.setGunAngle(1, -MathUtils.degRad * 90.0f, MathUtils.degRad * 15f);
                    en.setGunCount(1, 5, 1);
                    en.setGunSpeed(1, 2.0f, 2.0f);
                    en.setGunAngularSpeed(1, -0.002f, -0.002f);
                    en.setGunRepeating(1, 4);
                    en.setGunRepeatInterval(1, 20);
                    en.turnGunOn(1);
                    return true;
                }
            ),
            Task.keyframe(
                220,
                en -> {
                    en.adjustGunAngularSpeed(0, 0f);
                    en.adjustGunAngularSpeed(1, 0f);
                    en.adjustGunAngle(0, -MathUtils.HALF_PI);
                    en.adjustGunAngle(1, -MathUtils.HALF_PI);
                    en.changeVelocity(Tweener.INTERPOLATION_LINEAR, 3f, MathUtils.PI, 50);
                    return true;
                }
            ),
            Task.keyframe(
                400,
                Task.wait(() -> (short) 1000)
            )
        );
    }

    private TimelineTask bunnyB() {
        return Task.timeline(
            Task.keyframe(
                en -> {
                    en.setHitbox(48.0f, 48.0f);
                    en.setDrop(1, 2);
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
                    en.rotate(Tweener.INTERPOLATION_LINEAR, -MathUtils.HALF_PI * 0.25f, 60);
                    return true;
                }
            ),
            Task.keyframe(
                200,
                en -> {
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
                en.setHitbox(48.0f, 48.0f);
                en.setDrop(7, 7);
                en.setSprite(getAnm(1), "Midboss");
                en.setSpriteRotation(true);
                en.setDeathVfx(VfxManager.Vfx.MIDBOSS_BLUE_DEATH);
                en.setDeathSound("sndNoise06.wav");
                en.interrupt(2);

                // Плавний спуск на позицію
                en.changePosition(Tweener.INTERPOLATION_EASE_OUT, 0.0f, -100.0f, 40);
                en.setInvincible(true);
                return true;
            }),
            Task.keyframe(
                40,
                e -> {
                    e.interrupt(1);
                    e.makeBoss("???");
                    e.setInvincible(true);
                    return true;
                }
            ),
            Task.keyframe(100, en -> {
                en.setSpeed(3.0f);
                en.setGunFireSound(0, "sndBulletHighMid.wav");
                en.moveCircularly(0.0f, 80.0f);
                en.interrupt(2);
                en.changeSpeed(INTERPOLATION_EASE_IN, 16.0f, 80);
                en.initGun(0);
                en.setGunAim(0, Gun.Aim.FAN_PLAYER);
                en.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_12x12_PURPLE);
                en.setGunCount(0, 6, 1);
                en.setGunAngle(0, 0.0f, MathUtils.degRad * 20.0f);
                en.setGunSpeed(0, 2.5f, 1.5f);
                en.setGunRepeatInterval(0, 50);
                en.setGunRepeating(0, 0);
                en.turnGunOn(0);
                return true;
            }),

            // Зміна патерну
            Task.keyframe(300, en -> {
                en.turnGunOff(0);
                en.setGunBulletType(0, Gun.BulletType.BULLET_10x16_BLUE);
                en.setSpeed(-4.0f);
                en.changeSpeed(INTERPOLATION_EASE_IN, -16.0f, 80);
                en.setGunAim(0, Gun.Aim.RING_PLAYER);
                en.setGunCount(0, 10, 2);
                en.setGunSpeed(0, 3.0f, 2.0f);
                en.setGunAcceleration(0, 0.05f, 0.04f);
                en.setGunRepeatInterval(0, 30);
                en.setGunFireSound(0, "sndNoise02.wav");
                en.turnGunOn(0);
                return true;
            }),

            // Завершити
            Task.keyframe(700, en -> {
                en.setInvincible(true);
                en.stopMovement();
                en.changePosition(INTERPOLATION_EASE_OUT, -50.0f, 60.0f, 60);
                en.turnGunOff(0);
                return true;
            }),
            Task.keyframe(770, en -> true)
        );
    }


    private TimelineTask boss() {
        return Task.timeline(
            Task.keyframe(en -> {
                en.setHitbox(96.0f, 96.0f);
                en.setDrop(15, 9);
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
