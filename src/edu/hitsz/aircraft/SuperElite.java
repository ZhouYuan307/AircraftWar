package edu.hitsz.aircraft;

import edu.hitsz.bullet.BulletFactory;
import edu.hitsz.item.*;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SuperElite extends AbstractEnemy{
    public SuperElite(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy, BulletFactory bulletFactory) {
        super(locationX, locationY, speedX, speedY, hp, strategy, bulletFactory);
        this.shootInterval = 1500;
        this.power = 30;
        this.direction = 1;
    }

//    @Override
//    public List<BaseBullet> shoot() {
//
//        // 获取当前时间
//        long currentTime = System.currentTimeMillis();
//
//        // 检查是否达到射击间隔
//        if (currentTime - lastShootTime < shootInterval) {
//            return new LinkedList<>(); // 返回空列表，不射击
//        }
//
//        // 更新上一次射击时间
//        lastShootTime = currentTime;
//
//
//        List<BaseBullet> enemyAttack = new LinkedList<>();
//        int x = this.getLocationX();
//        int y = this.getLocationY() + direction*2;
//        int speedX = 0;
//        int speedY = this.getSpeedY() + direction*5;
//        BaseBullet bullet;
//        for(int i= -1; i<shootNum - 1; i++){
//            // 子弹发射位置相对飞机位置向前偏移
//            // 多个子弹横向分散
//            bullet = new EnemyBullet(x + i*20, y, speedX+i, speedY, power);
//            enemyAttack.add(bullet);
//        }
//        return enemyAttack;
//    }

    @Override
    public List<BaseItem> spawnItems() {
        List<BaseItem> itemList = new LinkedList<>();
        Random random = new Random();
        double isDrop = random.nextDouble();
        double dropType = random.nextDouble();

        if (isDrop < 0.9) {
            int x = this.getLocationX();
            int y = this.getLocationY() + direction*2;
            int speedX = 0;
            int speedY = 5;
            BaseItem item;
            ItemFactory factory;

            //概率掉落各种道具或者直接不掉落
            if (dropType < 0.4) {
                factory = new HealthItemFactory();
                item = factory.createItem(x, y, speedX, speedY);

            } else if (dropType < 0.6) {
                factory = new BombItemFactory();
                item = factory.createItem(x, y, speedX, speedY);
            } else if  (dropType < 0.8) {
                factory = new AttackItemFactory();
                item = factory.createItem(x, y, speedX, speedY);
            }else{
                factory = new BulletPlusItemFactory();
                item = factory.createItem(x, y, speedX, speedY);
            }
            itemList.add(item);
        }


        return itemList;
    }

    @Override
    public int getScores() {
        return 50;
    }
}
