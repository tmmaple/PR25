package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;

/**
 * Патерн атаки ворога
 * @author SkyWarp
 */
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
    boolean isAttacking;
    int attackCooldown;

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
        this.isAttacking = false;
        attackCooldown = 100;
    }
    public void update() {
        if(attackCooldown==0) {
            attack();
            attackCooldown=100;
        } else attackCooldown--;
        if(isAttacking) {
            shoot();
        }
    }
    private void attack(){
        direction.set(Player.global.position);
        isAttacking = true;
    }
    public void shoot() {
        if (timer == 0){
            float directionAngle = (float) Math.atan2(direction.y-owner.position.y, direction.x-owner.position.x);
            float min = directionAngle-range/2;
            float max = directionAngle + range/2;
            for (float angle = min; angle <= max; angle += turnAngle) {
                BulletManager.global.createEnemyBullet(bullets, owner.position, angle);
            }
            if (iterationsLeft == 0){
                iterationsLeft = iterations;
                isAttacking = false;
            } else iterationsLeft--;
            timer = interval;
        } else {
            timer --;
        }
    }

}
