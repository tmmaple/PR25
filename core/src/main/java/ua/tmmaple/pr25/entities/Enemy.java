package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

public class Enemy {
    GraphicManager.AnmVirtualMachine sprite;
    Vector2 position;
    private Gun gun;
    enum MoveType {
        LINEAR, ORBITAL, NONE
    }
    MoveType moveType;
    private float velocity;

    private Vector2 linearMoveVector;

    private Vector2 centre;
    private float xRadius;
    private float yRadius;
    private float currentAngle;

    public Enemy(float x, float y){
        this.position = new Vector2(x, y);
        this.sprite = GraphicManager.global.new AnmVirtualMachine();
        this.gun = new Gun(this, BulletManager.global.enemyBullets, 360, 0.3f, 10, 5);
        Flow.global.addToUpdate(new Flow.FlowNode<>(this, Enemy::update, Enemy::added),3);
        Flow.global.addToDraw(new Flow.FlowNode<>(this, Enemy::draw),3);
    }
    public void setVelocity(float velocity){
        this.velocity = velocity;
    }
    public void setLinearMove(float angle){
        this.linearMoveVector.set(velocity, 0).rotateDeg(angle);
        this.moveType = MoveType.LINEAR;
    }
    public void setOrbitalMove(Vector2 centre, float xRadius, float yRadius, float startAngle){
        this.centre = centre;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = startAngle;
        this.moveType = MoveType.ORBITAL;
    }
    public void setOrbitalMove(float angle, float xRadius, float yRadius){
        this.centre.set(position.x- xRadius *(float)Math.cos(angle), position.y - yRadius *(float)Math.sin(angle));
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = angle;
        this.moveType = MoveType.ORBITAL;
    }
    public void stopMovement(){
        this.moveType = MoveType.NONE;
    }
    private int update(){
        if(moveType == MoveType.LINEAR) position.add(linearMoveVector);
        if(moveType == MoveType.ORBITAL) {
            position.set(centre.x+xRadius*(float)Math.cos(currentAngle), centre.y+yRadius*(float)Math.sin(currentAngle));
            currentAngle += velocity/(xRadius+yRadius/2);
        }
        sprite.execute();
        gun.update();
        return 0;
    }
    private int added(){
        sprite.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        sprite.loadScriptAndPlay("PlayerSprite");
        linearMoveVector = new Vector2();
        centre = new Vector2();
        moveType = MoveType.NONE;
        return 0;
    }
    private int draw(){
        sprite.position.set(position);
        sprite.draw();
        return 0;
    }
}
