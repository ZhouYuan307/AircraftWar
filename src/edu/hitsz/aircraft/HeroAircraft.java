package edu.hitsz.aircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.HeroBulletFactory;
import edu.hitsz.strategy.ShootStraight;


public class HeroAircraft extends AbstractAircraft {





    //创建静态实例
    private static HeroAircraft instance = new HeroAircraft();

//    public HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
//        super(locationX, locationY, speedX, speedY, hp);
//    }

    //私有构造函数，使用默认参数
    private HeroAircraft(){
        super(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100, new ShootStraight(), new HeroBulletFactory());
        this.shootInterval = 0;
        this.power = 30;
        this.direction = -1;

    }

    //对外提供接口
    public static HeroAircraft getInstance() {
        return instance;
    }

    //提供初始化函数，方便将英雄机初始化（重生）
    public void init (int locationX, int locationY, int speedX, int speedY, int hp){
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
        this.maxHp = hp;
        this.isValid = true;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

//    @Override
//    /**
//     * 通过射击产生子弹
//     * @return 射击出的子弹List
//     */
//    public List<BaseBullet> shoot() {
//
//        // 获取当前时间
////        long currentTime = System.currentTimeMillis();
////
////        // 检查是否达到射击间隔
////        if (currentTime - lastShootTime < shootInterval) {
////            return new LinkedList<>(); // 返回空列表，不射击
////        }
////
////        // 更新上一次射击时间
////        lastShootTime = currentTime;
//
//
//        List<BaseBullet> res = new LinkedList<>();
//        int x = this.getLocationX();
//        int y = this.getLocationY() + direction*2;
//        int speedX = 0;
//        int speedY = this.getSpeedY() + direction*5;
//        BaseBullet bullet;
//        for(int i=0; i<shootNum; i++){
//            // 子弹发射位置相对飞机位置向前偏移
//            // 多个子弹横向分散
//            bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY * 2, power);
//            res.add(bullet);
//        }
//        return res;
//    }

    public void heal(int healAmount) {
        this.hp = Math.min(this.hp+healAmount, maxHp);
    }

    public void setShootInterval(int interval) {
        this.shootInterval = interval;
    }



}
