package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.Enemy;
import ua.tmmaple.pr25.entities.GameplayManager;
import ua.tmmaple.pr25.entities.Gun;
import ua.tmmaple.pr25.entities.VfxManager;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;

public class Stage02 extends Stage {
    private float birdX;

    private Enemy midboss;
    private Enemy boss;

    @Override
    public String[] anmList() {
        return new String[] {
            "stages/st02BG.anm",
            "stages/st02E.anm",
            "stages/st02Boss.anm"
        };
    }

    @Override
    public String[] bgmList() {
        return new String[] {
            "bgm/st02.bgm",
            "bgm/st02Boss.bgm"
        };
    }

    @Override
    public void reset() {

    }

    @Override
    public TimelineTask main() {
        return Task.timeline(
            Task.keyframe(0, Task.sequence(
                e -> {
                    background.load(getAnm(0), "Background");
                    background.setCameraLimits(0.0f, 480.0f, true);
                    background.cameraVelocity = 16.0f;
                    background.accelerateCamera((short) 90, 1.0f, INTERPOLATION_EASE_OUT);
                    return true;
                },
                Task.wait(() -> (short) 40),
                e -> { playMusic(0); return true; }
            )),
            Task.keyframe(
                100,
                e -> {
                    e.addAsyncTask(Task.repeat(
                            () -> (short) 80,
                            Task.sequence(
                                en -> {
                                    float x = random.nextBoolean() ? 1.0f : -1.0f;
                                    float y = random.nextFloat(-300.0f, -30.0f);
                                    en.createChildRelative(leaf01(-x, random.nextInt(20) > 16), x * 180.0f, y, 1);
                                    return true;
                                },
                                Task.wait(() -> (short) 5)
                            )
                        )
                    );
                    return true;
                }
            ),
            Task.keyframe(
                500,
                Task.sequence(
                    e -> {
                        birdX = -100.0f;
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 16,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(bird01(random.nextBoolean()), birdX, 20.0f, 5);
                                birdX += 16.0f;
                                return true;
                            },
                            Task.wait(() -> (short) 20)
                        )
                    )
                )
            ),
            Task.keyframe(
                620,
                Task.sequence(
                    e -> {
                        birdX = -60.0f;
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 5,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(bird02(birdX, random.nextBoolean()), random.nextFloat(-120.0f, 120.0f), 20.0f, 10);
                                birdX += 30.0f;
                                return true;
                            },
                            Task.wait(() -> (short) 8)
                        )
                    )
                )
            ),
            Task.keyframe(
                730,
                Task.sequence(
                    e -> {
                        birdX = -90.0f;
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 5,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(bird02(birdX, random.nextBoolean()), random.nextFloat(-120.0f, 120.0f), 40.0f, 10);
                                birdX += 40.0f;
                                return true;
                            },
                            Task.wait(() -> (short) 8)
                        )
                    )
                )
            ),
            Task.keyframe(
                770,
                Task.sequence(
                    e -> {
                        birdX = 30.0f;
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 5,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(bird02(birdX, random.nextBoolean()), random.nextFloat(-120.0f, 120.0f), 50.0f, 10);
                                birdX += 20.0f;
                                return true;
                            },
                            Task.wait(() -> (short) 8)
                        )
                    )
                )
            ),
            Task.keyframe(
                840,
                Task.sequence(
                    e -> {
                        birdX = -120.0f;
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 16,
                        Task.sequence(
                            e -> {
                                e.createChildAbsolute(bird02(birdX, random.nextBoolean()), random.nextFloat(-120.0f, 120.0f), random.nextFloat(30.0f, 60.0f), 10);
                                birdX += 16.0f;
                                return true;
                            },
                            Task.wait(() -> (short) 14)
                        )
                    )
                )
            ),
            Task.keyframe(
            1000,
                e -> {
                    e.createChildRelative(midBoss(), 200.0f, 16.0f, 350);
                    return true;
                }
            ),
            Task.keyframe(
                1001,
                Task.repeat(
                    () -> (short) 400,
                    Task.sequence(
                        e -> {
                            if (midboss == null || !midboss.isActive()) {
                                float x = random.nextFloat(-140.0f, 140.0f);
                                e.createChildRelative(leafMidbossDefeated(x), x, 16.0f, 2);
                                midboss = null;
                            }
                            return true;
                        },
                        Task.wait(() -> (short) 5)
                    )
                )
            ),
            Task.keyframe(
                1500,
                e -> {
                    e.addAsyncTask(
                        Task.repeat(
                            () -> (short) 200,
                            Task.sequence(
                                en -> {
                                    float x = random.nextFloat(-120.0f, 120.0f);
                                    en.createChildRelative(bird03(x), x, 16.0f, 5);
                                    return true;
                                },
                                Task.wait(() -> (short) 10)
                            )
                        )
                    );
                    return true;
                }
            ),
            Task.keyframe(
                1800,
                e -> {
                    for (int i = 0; i < 4; ++i) {
                        float x = random.nextFloat(-120.0f, 120.0f);
                        float y =  random.nextFloat(-120.0f, -100.0f);
                        e.createChildRelative(firefly(x), x, y, 5);
                    }
                    return true;
                }
            ),
            Task.keyframe(
                2200,
                e -> {
                    for (int i = 0; i < 3; ++i) {
                        float x = random.nextFloat(-160.0f, 160.0f);
                        float y =  random.nextFloat(-120.0f, -40.0f);
                        e.createChildRelative(firefly(x), x, y, 5);
                    }
                    return true;
                }
            ),
            Task.keyframe(
                2700,
                e -> {
                    for (int i = 0; i < 2; ++i) {
                        float x = random.nextFloat(-160.0f, 160.0f);
                        float y =  random.nextFloat(-120.0f, -40.0f);
                        e.createChildRelative(firefly(x), x, y, 5);
                    }
                    return true;
                }
            ),
            Task.keyframe(
                3200,
                e -> {
                    for (int i = 0; i < 4; ++i) {
                        float x = random.nextFloat(-160.0f, 160.0f);
                        float y =  random.nextFloat(-120.0f, -40.0f);
                        e.createChildRelative(firefly(x), x, y, 5);
                    }
                    return true;
                }
            ),
            Task.keyframe(
                3600,
                e -> {
                    background.resetCameraLimits();
                    background.moveCamera((short) 130, 960.0f, INTERPOLATION_EASE_OUT);
                    background.cameraVelocity = 0.0f;
                    return true;
                }
            ),
            Task.keyframe(
                3700,
                Task.sequence(
                    e -> {
                        e.createChildRelative(boss(), 90.0f, 72.0f, 1000);
                        return true;
                    },
                    Task.wait(() -> (short) 16),
                    e -> boss == null || !boss.isActive(),
                    Task.wait(() -> (short) 180),
                    e -> {
                        God.global.results();
                        return true;
                    }
                )
            )
        );
    }

    private TimelineTask leaf01(float hDir, boolean spawnBullet) {
        return Task.timeline(
            Task.keyframe(
               e -> {
                    e.setSprite(getAnm(1), "Leaf");
                    e.setVelocity(MathUtils.PI * (0.5f + -hDir * 0.5f), random.nextFloat(5.0f, 9.0f));
                    boolean score = random.nextBoolean();
                    e.setDrop(score ? 1 : 0, score ? 0 : 1);
                    e.setHitbox(14.0f, 14.0f);
                    if (spawnBullet) {
                        e.initGun(0);
                        e.setGunBulletType(0, Gun.BulletType.BULLET_16x16_RED);
                        e.setGunAim(0, Gun.Aim.FAN_PLAYER);
                        e.setGunAngle(0, 0.0f, MathUtils.degRad * 15.0f);
                        e.setGunCount(0, 1, 3);
                        e.setGunRepeating(0, 1);
                        e.setGunSpeed(0, 2.0f, 0.5f);
                    }
                    return true;
                }
            ),
            Task.keyframe(
                40,
                e -> {
                    if (spawnBullet)
                        e.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                e -> { e.adjustGunAcceleration(0, 4.0f / 50.0f); return true; }
            )
        );
    }

    private TimelineTask bird01(boolean right) {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(1), "Nightingale");
                    e.setSpriteRotation(true);
                    boolean power = random.nextBoolean();
                    e.setDrop(power ? 0 : 2, power ? 1 : 0);
                    e.setHitbox(12.0f, 12.0f);
                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_8x8_WHITE);
                    e.setGunAim(0, Gun.Aim.RING_PLAYER);
                    e.setGunAngle(0, 0.0f, 0.0f);
                    e.setGunCount(0, 12, 1);
                    e.setGunRadius(0, 8.0f, 4.0f);
                    e.setGunRepeating(0, 1);
                    e.setGunSpeed(0, 2.0f, 1.0f);
                    e.setVelocity(-MathUtils.HALF_PI, 2.0f);
                    return true;
                }
            ),
            Task.keyframe(
                40,
                e -> {
                    e.changeVelocity(INTERPOLATION_EASE_IN, 5.0f, right ? 0.0f : -MathUtils.PI, 40);
                    return true;
                }
            ),
            Task.keyframe(
                80,
                e -> {
                    e.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                200,
                e -> true
            )
        );
    }

    private TimelineTask bird02(float x, boolean right) {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(1), "Nightingale");
                    e.setSpriteRotation(true);
                    boolean power = random.nextBoolean();
                    e.setAngle(-MathUtils.HALF_PI);
                    e.setDrop(power ? 0 : 2, power ? 2 : 0);
                    e.setHitbox(12.0f, 12.0f);
                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_10x16_RED);
                    e.setGunAim(0, Gun.Aim.RING_PLAYER);
                    e.setGunAngle(0, 0.0f, MathUtils.degRad * 12.0f);
                    e.setGunCount(0, 5, 3);
                    e.setGunRadius(0, 8.0f, 4.0f);
                    e.setGunRepeating(0, 1);
                    e.setGunSpeed(0, 4.0f, 2.0f);
                    e.setGunFireSound(0, "sndNoise04.wav");
                    e.changePosition(INTERPOLATION_EASE_OUT, x, -50.0f, 40);
                    return true;
                }
            ),
            Task.keyframe(
                70,
                e -> {
                    e.turnGunOn(0);
                    e.changeVelocity(INTERPOLATION_EASE_IN, 5.0f, right ? 0.0f : MathUtils.PI, 40);
                    return true;
                }
            ),
            Task.keyframe(
                90,
                e -> {
                    e.changeSpeed(INTERPOLATION_LINEAR, 1.0f, 30);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                e -> {
                    e.adjustGunAngularSpeed(0, MathUtils.degRad * 32.0f / 60.0f);
                    e.changeVelocity(INTERPOLATION_LINEAR, 6.0f, right ? 0.0f : -MathUtils.PI, 30);
                    return true;
                }
            ),
            Task.keyframe(
                270,
                e -> true
            )
        );
    }

    private TimelineTask midBoss() {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    midboss = e;
                    e.setSprite(getAnm(1), "Midboss");
                    e.setHitbox(48.0f, 48.0f);
                    e.setDrop(5, 2);
                    e.setInvincible(true);
                    e.setDeathSound("sndNoise06.wav");
                    e.setDeathVfx(VfxManager.Vfx.MIDBOSS_ORANGE_DEATH);

                    e.initGun(0);
                    e.setGunFireSound(0, "sndBulletHighMid.wav");
                    e.setGunBulletType(0, Gun.BulletType.BULLET_8x12_ORANGE);
                    e.setGunAim(0, Gun.Aim.FAN_PLAYER);
                    e.setGunCount(0, 5, 3);
                    e.setGunAngle(0, 0.0f, MathUtils.degRad * 35.0f);
                    e.setGunSpeed(0, 7.0f, 3.0f);
                    e.setGunAcceleration(0, -0.1f, -0.05f);
                    e.setGunRepeating(0, 3);
                    e.setGunRepeatInterval(0, 6);

                    e.initGun(1);
                    e.setGunFireSound(1, "sndNoise02.wav");
                    e.setGunBulletType(1, Gun.BulletType.BULLET_12x12_WHITE);
                    e.setGunAim(1, Gun.Aim.RING_PLAYER);
                    e.setGunCount(1, 12, 3);
                    e.setGunAngle(1, 0.0f, MathUtils.degRad * 28.0f);
                    e.setGunSpeed(1, 3.0f, 1.0f);
                    e.setGunAngularSpeed(1, MathUtils.degRad * 12.0f / 30.0f, MathUtils.degRad * 5.0f / 30.0f);
                    e.setGunRepeating(1, 0);
                    e.setGunRepeatInterval(1, 40);

                    e.interrupt(2);
                    e.changePosition(INTERPOLATION_EASE_OUT, 0.0f, -120.0f, 70);
                    return true;
                }
            ),
            Task.keyframe(
                80,
                    e -> {
                    e.setInvincible(false);
                    e.interrupt(1);
                    e.makeBoss("???");
                    return true;
                }
            ),
            Task.keyframe(
                    100,
                Task.repeat(
                    () -> (short) 3,
                    Task.sequence(
                        e -> {
                            e.destroyGunBullets(0);
                            e.setInvincible(false);
                            e.turnGunOn(0);
                            return true;
                        },
                        Task.wait(() -> (short) 40),
                        e -> {
                            e.setInvincible(true);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-120.0f, 120.0f), 72.0f, 60);
                            e.interrupt(2);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.setInvincible(false);
                            e.adjustGunAcceleration(0, 0.01f);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-100.0f, 100.0f), -120.0f, 60);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.interrupt(1);
                            e.turnGunOn(0);
                            return true;
                        },
                        Task.wait(() -> (short) 40),
                        e -> {
                            e.interrupt(2);
                            e.setInvincible(true);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-120.0f, 120.0f), 72.0f, 60);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.setInvincible(false);
                            e.adjustGunAcceleration(0, 0.01f);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-60.0f, 60.0f), random.nextFloat(-140.0f, -90.0f), 60);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.interrupt(1);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.interrupt(2);
                            e.moveOrbitally(random.nextFloat(0.0f, MathUtils.PI), 140.0f, 50.0f);
                            e.changeSpeed(INTERPOLATION_LINEAR, 18.0f, 60);
                            e.turnGunOn(1);
                            return true;
                        },
                        Task.wait(() -> (short) 240),
                        e -> {
                            e.stopMovement();
                            e.turnGunOff(1);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-120.0f, 120.0f), 72.0f, 60);
                            return true;
                        },
                        Task.wait(() -> (short) 60),
                        e -> {
                            e.setInvincible(false);
                            e.changePosition(INTERPOLATION_EASE_OUT, random.nextFloat(-100.0f, 100.0f), -120.0f, 60);
                            return true;
                        },
                        Task.wait(() -> (short) 60)
                    )
                )
            ),
            Task.keyframe(
                120,
                e -> {
                    midboss = null;
                    e.changePosition(INTERPOLATION_EASE_IN, 400.0f, -80.0f, 90);
                    return true;
                }
            ),
            Task.keyframe(210, e -> true)
        );
    }

    private TimelineTask leafMidbossDefeated(float x) {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(1), "Leaf");
                    e.setHitbox(14.0f, 14.0f);
                    boolean score = random.nextBoolean();
                    e.setDrop(score ? 1 : 0, score ? 0 : 1);
                    float angle = MathUtils.HALF_PI * -0.5f;
                    if (x > 70.0f)
                        angle -= MathUtils.HALF_PI;
                    else if (x > -70.0f && MathUtils.randomBoolean())
                        angle -= MathUtils.HALF_PI;
                    float speed = random.nextFloat(8.0f, 15.0f);
                    e.setVelocity(angle, speed);
                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_8x12_RED);
                    e.setGunAim(0, Gun.Aim.RING_PLAYER);
                    e.setGunCount(0, 4, 1);
                    e.setGunSpeed(0, 2.0f, 2.0f);
                    e.setGunRepeating(0, 0);
                    e.setGunRepeatInterval(0, 15);
                    e.setGunDelay(0, 30);
                    e.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                180,
                e -> true
            )
        );
    }

    private TimelineTask bird03(float x) {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(1), "Nightingale");
                    e.setSpriteRotation(true);
                    e.setVelocity(-MathUtils.HALF_PI, random.nextFloat(6.0f, 8.0f));
                    boolean power = random.nextBoolean();
                    e.setDrop(power ? 0 : 1, power ? 1 : 0);
                    e.setHitbox(12.0f, 12.0f);
                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_12x12_RED);
                    e.setGunAim(0, Gun.Aim.RING_PLAYER);
                    e.setGunAngle(0, 0.0f, MathUtils.degRad * 12.0f);
                    e.setGunCount(0, 4, 1);
                    e.setGunRadius(0, 2.0f, 1.0f);
                    e.setGunRepeating(0, 1);
                    e.setGunSpeed(0, 2.0f, 1.0f);
                    return true;
                }
            ),
            Task.keyframe(
                20,
                e -> {
                    float angle = random.nextFloat(MathUtils.HALF_PI * -0.25f, MathUtils.HALF_PI * 0.25f);
                    if ((x < 0.0f && x > -50.0f) || x > 50.0f)
                        angle -= MathUtils.PI;
                    e.changeVelocity(INTERPOLATION_LINEAR, random.nextFloat(6.0f, 8.0f), angle, 15);
                    e.turnGunOn(0);
                    return true;
                }
            ),
            Task.keyframe(
                90,
                e -> true
            )
        );
    }

    private TimelineTask firefly(float x) {
        return Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(1), "Firefly");
                    e.setCollision(false);
                    e.interrupt(1);
                    e.moveCircularly(0.0f, 90.0f);
                    e.changeSpeed(INTERPOLATION_LINEAR, random.nextBoolean() ? -12.0f : 12.0f, 65);
                    boolean power = random.nextBoolean();
                    e.setDrop(power ? 0 : 4, power ? 2 : 0);
                    e.setHitbox(16.0f, 16.0f);
                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_RINGED_16x16_WHITE);
                    e.setGunAim(0, Gun.Aim.RANDOM_FAN_PLAYER);
                    e.setGunAngle(0, 0.0f, MathUtils.degRad * 46.0f);
                    e.setGunCount(0, 4, 1);
                    e.setGunRepeating(0, 1);
                    e.setGunSpeed(0, 4.0f, 3.0f);
                    return true;
                }
            ),
            Task.keyframe(
                65,
                e -> {
                    e.setCollision(true);
                    float angle = x < 0.0f ? -MathUtils.PI : 0.0f;
                    float speed = random.nextFloat(-9.0f, 9.0f);
                    e.setSpeed(speed);
                    e.moveOrbitally(angle, 70.0f, 30.0f);
                    return true;
                }
            ),
            Task.keyframe(
                120,
                e -> {
                    e.moveLinearly();
                    e.turnGunOn(0);
                    float angle = random.nextBoolean() ? -MathUtils.PI : 0.0f;
                    e.changeVelocity(INTERPOLATION_EASE_IN, 8.0f, angle, 40);
                    return true;
                }
            ),
            Task.keyframe(
                260,
                e -> true
            )
        );
    }

    private TimelineTask boss() {
        TimelineTask ballA =  Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(2), "Ball");
                    e.setInvincible(true);
                    e.setHitbox(8.0f, 8.0f);
                    e.changePosition(INTERPOLATION_EASE_IN, 48.0f, 0.0f, 40);

                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_16x16_RED);
                    e.setGunFireSound(0, "sndNoise04.wav");
                    e.setGunAim(0, Gun.Aim.RANDOM_FAN_STATIC);
                    e.setGunCount(0, 1, 2);
                    e.setGunAngle(0, -MathUtils.HALF_PI, MathUtils.degRad * 65.0f);
                    e.setGunSpeed(0, 5.0f, 2.4f);
                    e.setGunRepeating(0, 25);
                    e.setGunRepeatInterval(0, 6);
                    e.setGunDelay(0, 60);

                    return true;
                }
            ),
            Task.keyframe(
                40,
                    Task.sequence(
                    e -> {
                        e.moveCircularly(0.0f, 48.0f);
                        return true;
                    },
                    Task.whileLoop(
                        () -> boss != null && boss.isActive(),
                        Task.sequence(
                            e -> {
                                e.changeSpeed(INTERPOLATION_LINEAR, 16.0f, 180);
                                e.turnGunOn(0);
                                return true;
                            },
                            Task.wait(() -> (short) 180),
                            e -> {
                                e.changeSpeed(INTERPOLATION_LINEAR, 0.0f, 60);
                                return true;
                            },
                            Task.wait(() -> (short) 70)
                        )
                    )
                )
            )
        );
        TimelineTask ballB =  Task.timeline(
            Task.keyframe(
                e -> {
                    e.setSprite(getAnm(2), "Ball");
                    e.setInvincible(true);
                    e.setHitbox(8.0f, 8.0f);
                    e.changePosition(INTERPOLATION_EASE_IN, -48.0f, 0.0f, 40);

                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_16x16_RED);
                    e.setGunFireSound(0, "sndNoise04.wav");
                    e.setGunAim(0, Gun.Aim.RANDOM_FAN_STATIC);
                    e.setGunCount(0, 1, 2);
                    e.setGunAngle(0, -MathUtils.HALF_PI, MathUtils.degRad * 65.0f);
                    e.setGunSpeed(0, 5.0f, 2.4f);
                    e.setGunRepeating(0, 25);
                    e.setGunRepeatInterval(0, 6);
                    e.setGunDelay(0, 60);

                    return true;
                }
            ),
            Task.keyframe(
                40,
                Task.sequence(
                    e -> {
                        e.moveCircularly(MathUtils.PI, 48.0f);
                        return true;
                    },
                    Task.whileLoop(
                        () -> boss != null && boss.isActive(),
                        Task.sequence(
                            e -> {
                                e.changeSpeed(INTERPOLATION_LINEAR, 16.0f, 180);
                                e.turnGunOn(0);
                                return true;
                            },
                            Task.wait(() -> (short) 180),
                            e -> {
                                e.changeSpeed(INTERPOLATION_LINEAR, 0.0f, 60);
                                return true;
                            },
                            Task.wait(() -> (short) 70)
                        )
                    )
                )
            )
        );

        TimelineTask ballRoot = Task.timeline(
            Task.keyframe(
                Task.sequence(
                    e -> {
                        e.setCollision(false);
                        e.createChildRelative(ballA, 0.0f, 0.0f, 1);
                        e.createChildRelative(ballB, 0.0f, 0.0f, 1);
                        e.moveOrbitally(MathUtils.HALF_PI, 80.0f, 30.0f);
                        e.changeSpeed(INTERPOLATION_EASE_IN, -6.5f, 100);
                        return true;
                    },
                    e -> boss != null && boss.isActive()
                )
            )
        );

        return Task.timeline(
            Task.keyframe(
                e -> {
                    boss = e;
                    playMusic(1);

                    e.setSprite(getAnm(2), "Lynx");
                    e.setHitbox(48.0f, 48.0f);
                    e.setInvincible(true);
                    e.interrupt(2);
                    e.changePosition(INTERPOLATION_EASE_OUT, 0.0f, -120.0f, 70);
                    e.setDeathSound("sndNoise06.wav");
                    e.setDeathVfx(VfxManager.Vfx.BOSS_ORANGE_DEATH);
                    e.setDrop(15, 10);

                    e.initGun(0);
                    e.setGunBulletType(0, Gun.BulletType.BULLET_12x12_WHITE);
                    e.setGunAim(0, Gun.Aim.FAN_PLAYER);
                    e.setGunCount(0, 8, 3);
                    e.setGunSpeed(0, 4.0f, 2.0f);
                    e.setGunAngle(0, 0.0f, MathUtils.degRad * 12.0f);
                    e.setGunFireSound(0, "sndNoise04.wav");
                    e.setGunRepeating(0, 1);

                    e.initGun(1);
                    e.setGunBulletType(1, Gun.BulletType.BULLET_10x16_ORANGE);
                    e.setGunAim(1, Gun.Aim.RING_PLAYER);
                    e.setGunCount(1, 20, 1);
                    e.setGunSpeed(1, 5.0f, 3.0f);
                    e.setGunAcceleration(1, -0.04f, -0.04f);
                    e.setGunFireSound(1, "sndNoise02.wav");
                    e.setGunRepeating(1, 1);

                    e.initGun(2);
                    e.setGunBulletType(2, Gun.BulletType.BULLET_RINGED_16x16_RED);
                    e.setGunAim(2, Gun.Aim.FAN_PLAYER);
                    e.setGunCount(2, 1, 9);
                    e.setGunSpeed(2, 2.0f, 6.0f);
                    e.setGunFireSound(2, "sndNoise04.wav");
                    e.setGunRepeating(2, 0);
                    e.setGunRepeatInterval(2, 19);

                    return true;
                }
            ),
            Task.keyframe(
                50,
                e -> {
                    e.setInvincible(false);
                    e.interrupt(1);
                    e.makeBoss("lynx");
                    return true;
                }
            ),
            Task.keyframe(
                90,
                e -> {
                    e.turnGunOn(0);
                    e.interrupt(2);
                    e.changePosition(INTERPOLATION_EASE_OUT, -90.0f, -90.0f, 70);
                    return true;
                }
            ),
            Task.keyframe(
                160,
                e -> {
                    e.turnGunOn(0);
                    e.interrupt(2);
                    e.changePosition(INTERPOLATION_EASE_OUT, 90.0f, -90.0f, 70);
                    return true;
                }
            ),
            Task.keyframe(
                250,
                Task.sequence(
                    e -> {
                        e.interrupt(2);
                        e.moveOrbitally(0.0f, 90.0f, 40.0f);
                        e.setSpeed(12.0f);
                        e.setInvincible(true);
                        return true;
                    },
                    Task.repeat(
                        () -> (short) 2,
                        Task.sequence(
                            e -> {
                                e.turnGunOn(1);
                                return true;
                            },
                            Task.wait(() -> (short) 10)
                        )
                    ),
                    e -> {
                        e.stopMovement();
                        e.setInvincible(false);
                        e.changePosition(INTERPOLATION_EASE_OUT, -70.0f, -60.0f, 70);
                        return true;
                    },
                    Task.wait(() -> (short) 60),
                    e -> {
                        e.interrupt(1);
                        return true;
                    },
                    Task.wait(() -> (short) 30),
                    Task.sequence(
                        e -> {
                            e.interrupt(2);
                            e.moveOrbitally(-MathUtils.PI - MathUtils.HALF_PI * 0.3f, 90.0f, 40.0f);
                            e.setSpeed(13.0f);
                            e.setInvincible(true);
                            return true;
                        },
                        Task.repeat(
                            () -> (short) 3,
                            Task.sequence(
                                e -> {
                                    e.turnGunOn(1);
                                    return true;
                                },
                                Task.wait(() -> (short) 10)
                            )
                        )
                    ),
                    e -> {
                        e.turnGunOn(2);
                        e.stopMovement();
                        e.setInvincible(false);
                        e.changePosition(INTERPOLATION_EASE_OUT, 70.0f, -60.0f, 70);
                        return true;
                    },
                    Task.wait(() -> (short) 60),
                    e -> {
                        e.adjustGunSpeed(1, 3.0f);
                        e.adjustGunAcceleration(1, 0.0f);
                        e.turnGunOff(2);
                        e.interrupt(1);
                        return true;
                    },
                    Task.wait(() -> (short) 30)
                )
            ),
            Task.keyframe(
                270,
                e -> {
                    e.interrupt(2);
                    e.changePosition(INTERPOLATION_EASE_OUT, 70.0f, -40.0f, 40);
                    return true;
                }
            ),
            Task.keyframe(
                310,
                e -> {
                    e.interrupt(1);
                    e.moveOrbitally(0.0f, 70.0f, 20.0f);
                    e.setSpeed(-2.4f);
                    return true;
                }
            ),
            Task.keyframe(
                340,
                Task.sequence(
                    e -> {
                        e.initGun(0);
                        e.initGun(1);
                        e.setGunBulletType(0, Gun.BulletType.BULLET_10x16_WHITE);
                        e.setGunBulletType(1, Gun.BulletType.BULLET_10x16_WHITE);
                        e.setGunAim(0, Gun.Aim.FAN_STATIC);
                        e.setGunAim(1, Gun.Aim.FAN_STATIC);
                        e.setGunOffset(0, Gun.OffsetMode.ENEMY, 20.0f, 0.0f);
                        e.setGunOffset(1, Gun.OffsetMode.ENEMY, -20.0f, 0.0f);
                        float baseAngle = -MathUtils.HALF_PI;
                        float relativeAngle = MathUtils.degRad * -12.0f;
                        e.setGunAngle(0, baseAngle - relativeAngle, MathUtils.degRad * 12.0f);
                        e.setGunAngle(1, baseAngle + relativeAngle, MathUtils.degRad * 12.0f);
                        e.setGunSpeed(0, 2.0f, 1.0f);
                        e.setGunSpeed(1, 2.0f, 1.0f);
                        e.setGunCount(0, 4, 1);
                        e.setGunCount(1, 4, 1);
                        e.setGunRepeating(0, 0);
                        e.setGunRepeating(1, 0);
                        e.setGunRepeatInterval(0, 20);
                        e.setGunRepeatInterval(1, 20);
                        e.setGunFireSound(0, "sndNoise04.wav");
                        e.turnGunOn(0);
                        e.turnGunOn(1);
                        return true;
                    },
                    Task.wait(() -> (short) 900),
                    e -> {
                        e.turnGunOff(0);
                        e.turnGunOff(1);
                        return true;
                    }
                )
            ),
            Task.keyframe(
                351,
                e -> {
                    e.stopMovement();
                    e.changePosition(INTERPOLATION_EASE_OUT, 15.0f, -60.0f, 30);
                    return true;
                }
            ),
            Task.keyframe(
                440,
                e -> {
                    e.createChildRelative(ballRoot, 0.0f, -30.0f, 1);
                    e.changeSpeed(INTERPOLATION_LINEAR, 4.0f, 200);
                    return true;
                }
            ),
            Task.keyframe(
                1200,
                e -> {
                    e.damage(10000000);
                    return true;
                }
            )
        );
    }
}
