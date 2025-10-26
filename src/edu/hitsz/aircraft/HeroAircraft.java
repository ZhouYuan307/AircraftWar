package edu.hitsz.aircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.HeroBulletFactory;
import edu.hitsz.strategy.ShootStraight;

import java.util.LinkedList;
import java.util.List;


public class HeroAircraft extends AbstractAircraft {
    private int effectTimer;
    public boolean isReset = true;
    //创建静态实例
    private static final HeroAircraft instance = new HeroAircraft();

    private final List<AbstractFlyingObject> observerList = new LinkedList<>();

    private int tempScores = 0;

    private long savedShootInterval;

    //私有构造函数，使用默认参数
    private HeroAircraft(){
        super(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100, new ShootStraight(), new HeroBulletFactory());
        this.shootInterval = 0;
        this.power = 300;
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
        this.effectTimer = 0;
        this.isReset = true;
        this.shootInterval = 0;
        setStrategy(new ShootStraight());
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    public void heal(int healAmount) {
        this.hp = Math.min(this.hp+healAmount, maxHp);
    }

    public void setShootInterval(int interval) {
        this.shootInterval = interval;
    }

    public void setEffectTimer(int duration) {
        this.effectTimer = duration;
    }

    public long getEffectTimer() {
        return this.effectTimer;
    }

    public void resetStrategy(){
        this.strategy = new ShootStraight();
        this.shootInterval = savedShootInterval;
        this.isReset = true;
    }

    public void effectTimerUpdate(int timeInterval){
        this.effectTimer -= timeInterval;

    }

    public void addObserver(AbstractFlyingObject observer){
        observerList.add(observer);
    }

    public void removeObserver(AbstractFlyingObject observer){
        observerList.remove(observer);
    }

    public void removeInvalid(){
        observerList.removeIf(AbstractFlyingObject::notValid);
    }

    public void useBomb(){
        tempScores = 0;
        for (AbstractFlyingObject observer :observerList){
            tempScores += observer.bombEffect();
        }
    }

    public int getScores(){
        int score = tempScores;
        tempScores = 0;
        return score;
    }

    public void addInterval(int interval){
        this.shootInterval += interval;
    }

    public void saveInterval(){
        this.savedShootInterval = this.shootInterval;
    }

    public long getInterval(){
        return this.shootInterval;
    }

}
