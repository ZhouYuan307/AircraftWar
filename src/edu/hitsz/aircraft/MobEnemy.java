package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.BulletFactory;
import edu.hitsz.item.BaseItem;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractEnemy {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy, BulletFactory bulletFactory) {
        super(locationX, locationY, speedX, speedY, hp, strategy, bulletFactory);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

//    @Override
//    public List<BaseBullet> shoot() {
//        return new LinkedList<>();
//    }

    @Override
    public List<BaseItem> spawnItems() {return new LinkedList<>();}

    @Override
    public int getScores(){
        return 10;
    }

}
