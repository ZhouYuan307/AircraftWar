package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.bullet.BulletFactory;
import edu.hitsz.item.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.ShootStrategy;

public class BossEnemy extends AbstractEnemy{

    private int minX = (int)(ImageManager.BOSS_IMAGE.getWidth()*0.5); // 最小X边界
    private int maxX = (int)(Main.WINDOW_WIDTH- ImageManager.BOSS_IMAGE.getWidth()*0.5);

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy, BulletFactory bulletFactory) {
        super(locationX, locationY, speedX, speedY, hp, strategy, bulletFactory);
        this.shootInterval = 3000;
        this.power = 30;
        this.direction = 1;
    }


//    @Override
//    public List<BaseBullet> shoot() {
//
//
//        // 获取当前时间
//        long currentTime = System.currentTimeMillis();
//
//        // 检查是否达到射击间隔
//        if (currentTime - lastShootTime < shootInterval) {
//            return new LinkedList<>(); // 返回空列表，不射击
//        }
//        // 更新上一次射击时间
//        lastShootTime = currentTime;
//
//        List<BaseBullet> enemyAttack = new LinkedList<>();
//        int x = this.getLocationX();
//        int y = this.getLocationY() + direction*2;
//        int speedX;
//        int speedY;
//        BaseBullet bullet;
//        for(int i = 0; i < shootNum; i++) {
//            // 计算每个子弹的角度（均匀分布在一圈）
//            double angle = 2 * Math.PI * i / shootNum;
//            double radius = 100;
//            double bulletX = x + radius * Math.cos(angle);
//            double bulletY = y + radius * Math.sin(angle);
//            double speed = 5.0; // 速度大小
//
//            speedX = (int)(speed * Math.cos(angle));
//            speedY = (int)(speed * Math.sin(angle));
//
//            bullet = new EnemyBullet((int)bulletX, (int)bulletY, speedX, speedY, power);
//            enemyAttack.add(bullet);
//        }
//        return enemyAttack;
//    }

    @Override
    public List<BaseItem> spawnItems() {
        List<BaseItem> itemList = new LinkedList<>();
        Random random = new Random();

        // 随机决定掉落道具的数量（0-3个）
        int dropCount = random.nextInt(4); // 生成0-3的随机数

        for (int i = 0; i < dropCount; i++) {
            double dropType = random.nextDouble();

            int x = this.getLocationX();
            int y = this.getLocationY() + direction * 2;
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
    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= minX || locationX >= maxX) {
            // 横向超出边界后反向
            speedX = -speedX;
        }
    }

    @Override
    public int getScores() {
        return 200;
    }
}

