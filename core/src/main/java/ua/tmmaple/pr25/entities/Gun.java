package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;

public class Gun {
    private final Enemy owner;
    BulletManager.EnemyBullet[] bullets;
    float range;
    float turnAngle;
    int interval;
    int iterations;
    int iterationsLeft;
    int timer;
    Vector2 direction;

    public Gun(Enemy owner, BulletManager.EnemyBullet[] bullets, float range, float turnAngle, int interval, int iterations) {
        this.owner = owner;
        this.bullets = bullets;
        this.range = (float) Math.toRadians(range);
        this.turnAngle = turnAngle;
        this.interval = interval;
        this.iterations = iterations;
        this.iterationsLeft = iterations;
        this.timer = 0;
        this.direction = new Vector2();
    }

    public void shoot() {
        if (timer == 0){
            float directionAngle = (float) Math.atan2(direction.y-owner.sprite.position.y, direction.x-owner.sprite.position.x);
            float min = directionAngle-range/2;
            float max = directionAngle + range/2;
            for (float angle = min; angle <= max; angle += turnAngle) {
                BulletManager.global.createEnemyBullet(bullets, owner.sprite.position, angle);
            }
            if (iterationsLeft == 0){
                iterationsLeft = iterations;
                owner.isAttacking = false;
            } else iterationsLeft--;
            timer = interval;
        } else {
            timer --;
        }
    }

}
