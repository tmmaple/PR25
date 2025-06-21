package ua.tmmaple.pr25.stages;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.assets.Stage;
import ua.tmmaple.pr25.entities.Enemy;
import ua.tmmaple.pr25.entities.Gun;
import ua.tmmaple.pr25.entities.VfxManager;
import ua.tmmaple.pr25.task.Task;
import ua.tmmaple.pr25.task.TimelineTask;

public class Stage02 extends Stage {
    private float birdX;

    private Enemy midboss;

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
                    e.setDrop(score ? 2 : 0, score ? 0 : 2);
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
                    e.setDrop(power ? 0 : 1, power ? 1 : 0);
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
                    e.setDrop(power ? 0 : 1, power ? 1 : 0);
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
                    e.changePosition(INTERPOLATION_EASE_IN, 400.0f, -80.0f, 90);
                    return true;
                }
            ),
            Task.keyframe(210, e -> true)
        );
    }
}
