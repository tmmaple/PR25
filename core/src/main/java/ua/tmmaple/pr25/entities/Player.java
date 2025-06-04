package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.Game;
import ua.tmmaple.pr25.God;
import ua.tmmaple.pr25.assets.Assets;
import ua.tmmaple.pr25.graphics.Anm;
import ua.tmmaple.pr25.graphics.GraphicManager;

public class Player {
    final GraphicManager.AnmVirtualMachine plr;
    private final GraphicManager.AnmVirtualMachine sprite;
    private final GraphicManager.AnmVirtualMachine hitbox;
    private final GraphicManager.AnmVirtualMachine smallBulletOrb;
    private final GraphicManager.AnmVirtualMachine bigBulletOrb;
    Array<GraphicManager.AnmVirtualMachine> smallBullets;
    Array<GraphicManager.AnmVirtualMachine> bigBullets;
    private int smallBulletCooldown;
    private int bigBulletCooldown;
    public Player() {
        plr = GraphicManager.global.new AnmVirtualMachine();
        sprite = GraphicManager.global.new AnmVirtualMachine();
        sprite.parent = plr;
        sprite.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        sprite.loadScriptAndPlay("PlayerSprite");
        hitbox = GraphicManager.global.new AnmVirtualMachine();
        hitbox.parent = plr;
        hitbox.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        hitbox.loadScriptAndPlay("PlayerHitbox");
        plr.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        plr.loadScriptAndPlay("Player");
        smallBulletOrb = GraphicManager.global.new AnmVirtualMachine();
        smallBulletOrb.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        smallBulletOrb.loadSource((byte)15);
        smallBulletOrb.parent = plr;
        smallBulletOrb.position.set(20, 20);
        smallBullets = new Array<>();
        bigBulletOrb = GraphicManager.global.new AnmVirtualMachine();
        bigBulletOrb.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
        bigBulletOrb.loadSource((byte)15);
        bigBulletOrb.parent = plr;
        bigBulletOrb.position.set(-20, 20);
        bigBullets = new Array<>();
        Flow.global.addToUpdate(new Flow.FlowNode<>(this, Player::update, Player::updAdded, Player::updRemoved),1);
        Flow.global.addToDraw(new Flow.FlowNode<>(this, Player::draw, Player::drawAdded, Player::drawRemoved),1);
    }

    private int update(){
        plr.execute();
        if (God.global.inputState(God.INPUT_MOVE_UP)==God.INPUT_STATE_JUST_PRESSED){
            sprite.interrupt((byte) 1);
        }
        if (God.global.inputState(God.INPUT_MOVE_LEFT)==God.INPUT_STATE_JUST_PRESSED){
            sprite.interrupt((byte) 2);
        }
        if (God.global.inputState(God.INPUT_MOVE_RIGHT)==God.INPUT_STATE_JUST_PRESSED){
            sprite.interrupt((byte) 3);
        }
        if (God.global.inputState(God.INPUT_MOVE_UP)==God.INPUT_STATE_PRESSED){
            if (plr.position.y < Game.BASE_WINDOW_HEIGHT-26) {
                plr.position.add(0, 3);
            }
        }
        if (God.global.inputState(God.INPUT_MOVE_DOWN)==God.INPUT_STATE_PRESSED){
            if (plr.position.y > 26) {
                plr.position.add(0, -3);
            }
        }
        if (God.global.inputState(God.INPUT_MOVE_LEFT)==God.INPUT_STATE_PRESSED){
            if (plr.position.x > 21) {
                plr.position.add(-3, 0);
            }
        }
        if (God.global.inputState(God.INPUT_MOVE_RIGHT)==God.INPUT_STATE_PRESSED){
            if (plr.position.x < Game.BASE_WINDOW_WIDTH-21) {
                plr.position.add(3, 0);
            }
        }
        if (God.global.inputState(God.INPUT_FIRE)==God.INPUT_STATE_PRESSED){
            if (smallBulletCooldown==0){
                GraphicManager.AnmVirtualMachine bullet = GraphicManager.global.new AnmVirtualMachine();
                bullet.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
                bullet.loadSource(14);
                bullet.position.set(smallBulletOrb.absolutePosition());
                smallBullets.add(bullet);
                smallBulletCooldown =3;
            }
            if (bigBulletCooldown==0){
                GraphicManager.AnmVirtualMachine bullet = GraphicManager.global.new AnmVirtualMachine();
                bullet.loadAnm(Assets.global.get(Anm.class,"game/plr.anm"));
                bullet.loadSource(13);
                bullet.position.set(bigBulletOrb.absolutePosition());
                bigBullets.add(bullet);
                bigBulletCooldown =10;
            }
        }
        sprite.execute();
        hitbox.execute();
        for (int i = 0; i < smallBullets.size; i++) {
            GraphicManager.AnmVirtualMachine bullet = smallBullets.get(i);
            bullet.position.add(0, 8);
            if (bullet.position.y > Game.BASE_WINDOW_HEIGHT) smallBullets.removeIndex(i);
        }
        for (int i = 0; i < bigBullets.size; i++) {
            GraphicManager.AnmVirtualMachine bullet = bigBullets.get(i);
            bullet.position.add(0, 5);
            if (bullet.position.y > Game.BASE_WINDOW_HEIGHT) bigBullets.removeIndex(i);
        }
        if (smallBulletCooldown >0) smallBulletCooldown--;
        if (bigBulletCooldown >0) bigBulletCooldown--;
        return 0;
    }

    private int updAdded(){

        return 0;
    }
    private int updRemoved(){

        return 0;
    }
    private int draw(){
        sprite.draw();
        smallBulletOrb.draw();
        bigBulletOrb.draw();
        for (GraphicManager.AnmVirtualMachine bullet : smallBullets) {
            bullet.draw();
        }
        for (GraphicManager.AnmVirtualMachine bullet : bigBullets) {
            bullet.draw();
        }
        return 0;
    }
    private int drawAdded(){

        return 0;
    }
    private int drawRemoved(){

        return 0;
    }
}
