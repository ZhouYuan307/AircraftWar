package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.application.Main;
import edu.hitsz.item.BaseItem;
import edu.hitsz.item.HealthItem;
import edu.hitsz.item.BombItem;
import edu.hitsz.item.AttackItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


//精英敌机
public class EliteEnemy extends AbstractAircraft{


    /**攻击方式 */

    //子弹数量
    private int shootNum = 1;
    //子弹伤害
    private int power = 30;
    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = 1;


    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> EnemyAttack = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*5;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            EnemyAttack.add(bullet);
        }
        return EnemyAttack;
    }

    @Override
    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;

            vanish();
        }
    }

    public List<BaseItem> spawnItems() {
        List<BaseItem> ItemList = new LinkedList<>();
        Random random = new Random();
        double isDrop = random.nextDouble();
        double dropType = random.nextDouble();

        if (isDrop < 0.8) {
            int x = this.getLocationX();
            int y = this.getLocationY() + direction*2;
            int speedX = 0;
            int speedY = 5;
            BaseItem item;

            if (dropType < 0.4) {
                // 40%概率是生命道具
                item = new HealthItem(x, y, speedX, speedY);
            } else if (dropType < 0.6) {
                // 30%概率是炸弹道具
                item = new BombItem(x, y, speedX, speedY);
            } else {
                // 30%概率是攻击道具
                item = new AttackItem(x, y, speedX, speedY);
            }
            ItemList.add(item);
        }


        return ItemList;
    }

}
