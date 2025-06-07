package ua.tmmaple.pr25.entities;

import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

//Поки це просто болванка для тесту gun
public class Enemy {
    GraphicManager.AnmVirtualMachine sprite;
    Gun gun;
    boolean isAttacking;
    int attackCooldown;
    public Enemy(){
        sprite = GraphicManager.global.new AnmVirtualMachine();
        sprite.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        sprite.loadScriptAndPlay("PlayerSprite");
        sprite.position.set(300, 300);
        isAttacking = false;
        attackCooldown = 100;
        gun = new Gun(this, BulletManager.global.enemyBullets, 180, 0.3f, 10, 5);
        Flow.global.addToUpdate(new Flow.FlowNode<>(this, Enemy::update),3);
        Flow.global.addToDraw(new Flow.FlowNode<>(this, Enemy::draw),3);
    }
    private void attack(){
        gun.direction.set(Player.playerPos);
        isAttacking = true;
    }
    private int update(){
        sprite.execute();
        if(attackCooldown==0) {
            attack();
            attackCooldown=100;
        } else attackCooldown--;
        if(isAttacking) {
            gun.shoot();
        }
        return 0;
    }
    private int draw(){
        sprite.draw();
        return 0;
    }
}
