package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.Vector2;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;
import ua.tmmaple.pr25.util.Tweener;

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

    private final Tweener.FloatTweener velocityTweener;
    private final Tweener.FloatTweener angleTweener;
    private final Tweener.FloatTweener xPositionTweener;
    private final Tweener.FloatTweener yPositionTweener;

    public Enemy(float x, float y){
        this.position = new Vector2(x, y);
        this.sprite = GraphicManager.global.new AnmVirtualMachine();
        this.gun = new Gun(this);
        this.velocityTweener = new Tweener.FloatTweener();
        this.angleTweener = new Tweener.FloatTweener();
        this.xPositionTweener = new Tweener.FloatTweener();
        this.yPositionTweener = new Tweener.FloatTweener();
        Flow.global.addToUpdate(new Flow.FlowNode<>(this, Enemy::update, Enemy::added),3);
        Flow.global.addToDraw(new Flow.FlowNode<>(this, Enemy::draw),3);
    }
    public void changePosition(byte interpolation, float x, float y, short shiftTime){
        xPositionTweener.start(interpolation, position.x, x, shiftTime);
        yPositionTweener.start(interpolation, position.y, y, shiftTime);
    }
    public void changeVelocity(byte interpolation, float velocity, short shiftTime){
        velocityTweener.start(interpolation, this.velocity, velocity, shiftTime);
    }
    public void changeAngle(byte interpolation, float angle, short shiftTime){
        angleTweener.start(interpolation, linearMoveVector.angleRad(), angle, shiftTime);
    }
    public void setLinearMove(byte interpolation, float velocity, float angle, short shiftTime){
        velocityTweener.start(interpolation, this.velocity, velocity, shiftTime);
        angleTweener.start(interpolation, linearMoveVector.angleRad(), angle, shiftTime);
        moveType = MoveType.LINEAR;
    }
    public void setLinearMove(){this.moveType = MoveType.LINEAR;}
    public void setOrbitalMove(Vector2 centre, float xRadius, float yRadius, float startAngle){
        this.centre = centre;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = startAngle;
        this.moveType = MoveType.ORBITAL;
    }
    public void setRoundMove(Vector2 centre, float radius, float startAngle){setOrbitalMove(centre, radius, radius, startAngle);}
    public void setOrbitalMove(float angle, float xRadius, float yRadius){
        this.centre.set(position.x- xRadius *(float)Math.cos(angle), position.y - yRadius *(float)Math.sin(angle));
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.currentAngle = angle;
        this.moveType = MoveType.ORBITAL;
    }
    public void setRoundMove(float angle, float radius){setOrbitalMove(angle, radius, radius);}
    public void stopMovement(){
        this.moveType = MoveType.NONE;
    }
    private int update(){
        if (velocityTweener.isRunning()){
            velocityTweener.update();
            velocity = velocityTweener.value();
            linearMoveVector.scl(velocityTweener.value()/velocity);
        }
        if (angleTweener.isRunning()){
            angleTweener.update();
            linearMoveVector.set(velocity*(float)Math.cos(angleTweener.value()), velocity*(float)Math.sin(angleTweener.value()));
        }
        if (xPositionTweener.isRunning()){
            xPositionTweener.update();
            position.x = xPositionTweener.value();
        }
        if (yPositionTweener.isRunning()){
            yPositionTweener.update();
            position.y = yPositionTweener.value();
        }
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
