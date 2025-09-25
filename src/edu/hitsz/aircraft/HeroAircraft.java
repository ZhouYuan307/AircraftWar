package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;


public class HeroAircraft extends AbstractAircraft {



    //子弹数量
    private int shootNum = 1;
    //子弹伤害
    private int power = 30;
    //子弹方向s
    private int direction = -1;

    //创建静态实例
    private static HeroAircraft instance = new HeroAircraft();
    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
//    public HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
//        super(locationX, locationY, speedX, speedY, hp);
//    }

    //私有构造函数，使用默认参数
    private HeroAircraft(){
        super(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);
    }

    //对外提供接口
    public static HeroAircraft getInstance() {
        return instance;
    }

    //提供初始化函数，方便创建唯一实例时进行初始化
    public void init (int locationX, int locationY, int speedX, int speedY, int hp){
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
        this.maxHp = hp;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*5;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    public void heal(int healAmount) {
        this.hp = Math.min(this.hp+healAmount, maxHp);
    }

}
