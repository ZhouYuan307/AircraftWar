package edu.hitsz.application;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.item.BaseItem;
import edu.hitsz.item.BombItem;
import edu.hitsz.music.PlaySoundManager;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends JPanel {

    //name
    protected String userName;

    //音乐相关
    protected PlaySoundManager playSoundManager;



    //背景图片
    protected BufferedImage background;
    protected int backGroundTop = 0;

    //Scheduled 线程池，用于任务调度
    protected final ScheduledExecutorService executorService;

    //时间间隔(ms)，控制刷新频率
    protected final int timeInterval = 40;

    //飞行对象
    protected final HeroAircraft heroAircraft;
    protected final List<AbstractEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseItem> droppedItems;

    //系统信息
    //当前得分
    protected int score = 0;
    //当前时刻
    protected int time = 0;
    // 记录上一次生成boss时的分数基数
    protected int lastBossScore = 0;
    //周期（ms)指示子弹的发射、敌机的产生频率
    protected final int cycleDuration = 600;
    protected int cycleTime = 0;
    //boss状态
    protected boolean isBossExist = false;


    //难度参数配置
    //屏幕中出现的敌机最大数量
    protected int enemyMaxNumber = 5;
    //boss生成间隔
    protected int bossInterval = 1000;
    protected double eliteEnemyRate = 0.2;
    protected double superEliteRate = 0.1;
    protected int bossHp = 600;
    protected int superEliteHp = 50;
    protected int eliteHp = 30;
    protected int mobHp = 30;
    protected int gameLevel = 0;

    Random random = new Random();

    public Game(String musicMode, String userName) {
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.init(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);//不关闭游戏而重新开始时，必须重新初始化否则会直接结束
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        droppedItems = new LinkedList<>();
        this.userName = userName;

        /*
          Scheduled 线程池，用于定时任务调度
          关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
          apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        this.playSoundManager = new PlaySoundManager(Objects.equals(musicMode, "ON"));

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);
    }


    protected AbstractEnemy getEnemy() {
        AbstractEnemy enemy;
        EnemyFactory factory;
        double enemyType = random.nextDouble();

        if (enemyType < (1 - superEliteRate - eliteEnemyRate)) {
            factory = new MobEnemyFactory();
            enemy = factory.createEnemy(mobHp);
        } else if (enemyType < (1 - superEliteRate)) {
            factory = new EliteEnemyFactory();
            enemy = factory.createEnemy(eliteHp);
        } else{
            factory = new SuperEliteFactory();
            enemy = factory.createEnemy(superEliteHp);
        }
        return enemy;
    }

    protected void increaseDifficulty() {
        if (time > 600) {
            int difficultyRate = time / 600;
            //每18s，增加敌机属性，上限20次
            if (difficultyRate % 30 == 0 && difficultyRate <= 600) {
                //即使超过1也没关系，不影响生成逻辑
                eliteEnemyRate += gameLevel * 0.01;
                superEliteRate += gameLevel * 0.01;
                mobHp += gameLevel;
                eliteHp += gameLevel * 2;
                superEliteHp += gameLevel * 3;
                System.out.println("-------敌机属性提升！-------\n" + "普通敌人：" + mobHp + "\n" + "精英敌人：" + eliteHp + "\n" + "超级敌人：" + superEliteHp + "\n");
            }
            //每分钟，增加飞机数量，上限10
            if (difficultyRate % 100 == 0 && enemyMaxNumber <= 10) {
                enemyMaxNumber += gameLevel;
                System.out.println("-------敌机数量上限提升！-------\n" + "当前数量上限：" + enemyMaxNumber + "\n");
            }
            //每30s，降低射速,上限10次
            if (difficultyRate % 50 == 0 && difficultyRate <= 500) {
                heroAircraft.addInterval(gameLevel * 100);
                System.out.println("-------英雄机射速降低！-------\n"+"当前射击间隔(小于600取600)：" + heroAircraft.getInterval() + "\n");
            }
        }
    }



    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        playSoundManager.playBgm();
        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;


            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);

                // 新敌机产生

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    AbstractEnemy enemy = getEnemy();
                    enemyAircrafts.add(enemy);
                    heroAircraft.addObserver(enemy);
                    checkForBossSpawn();
                }


                // 飞机射出子弹
                shootAction();
                //增加难度
                increaseDifficulty();
            }
            //技能持续
            checkSkillDuration();

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            //道具移动
            itemsMoveAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {

                // 游戏结束
                playSoundManager.stopBgm();
                playSoundManager.playGameOver();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                playSoundManager.shutdown();
                executorService.shutdown();
                System.out.println("Game Over!");


                //跳转到排行榜gui
                ScoreController controller = new ScoreController(userName, score);
                Main.cardPanel.add(controller.getScoreGUI().getMainPanel());
                Main.cardLayout.last(Main.cardPanel);
            }

        };

        /*
          以固定延迟时间进行执行
          本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }



    //***********************
    //      Action 各部分
    //***********************

    protected void checkSkillDuration() {
        if (heroAircraft.getEffectTimer()>0){
            heroAircraft.effectTimerUpdate(timeInterval);
        }else if(!heroAircraft.isReset){
            heroAircraft.resetStrategy();
        }
    }

    protected void checkForBossSpawn() {

        int deltaScore = score - lastBossScore;
        if (deltaScore > bossInterval && !isBossExist) {
            lastBossScore = score;
            AbstractEnemy enemy;
            EnemyFactory factory;
            factory = new BossFactory();
            enemy = factory.createEnemy(bossHp);
            enemyAircrafts.add(enemy);
            isBossExist = true;
            playSoundManager.stopBgm();
            playSoundManager.playBgmBoss() ;
        } else if (isBossExist) {
            lastBossScore = score;
        }
    }


    protected boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected void shootAction() {
        for (AbstractEnemy enemy : enemyAircrafts) {
            List<BaseBullet> bullets = enemy.executeStrategy();
            enemyBullets.addAll(bullets);
            for (AbstractFlyingObject bullet: bullets){
                heroAircraft.addObserver(bullet);
            }
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.executeStrategy());
    }

    protected void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    protected void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    protected void itemsMoveAction() {
        for (BaseItem item : droppedItems) {
            item.forward();
        }
    }
    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    protected void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                // 英雄机撞到敌人子弹
                // 英雄机损失一定生命值
                playSoundManager.playBulletHit();
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    playSoundManager.playBulletHit();
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        if(enemyAircraft instanceof BossEnemy)
                        {
                            playSoundManager.stopBgmBoss();
                            playSoundManager.playBgm();
                            isBossExist = false;
                            System.out.println("Boss crashed");
                        }
                        List<BaseItem> item = enemyAircraft.spawnItems();
                        droppedItems.addAll(item);

                        score += enemyAircraft.getScores();

                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    if(enemyAircraft instanceof BossEnemy)
                    {
                        isBossExist = false;
                        playSoundManager.stopBgmBoss();
                        playSoundManager.playBgm();
                        System.out.println("Boss crashed");
                    }
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        for (BaseItem item : droppedItems) {
            if (item.notValid()) {
                continue;
            }
            if (heroAircraft.crash(item)) {
                if (item instanceof BombItem){
                    playSoundManager.playBombExplosion();
                }else{
                    playSoundManager.playGetSupply();
                }
                item.activateEffect(heroAircraft);
                score += heroAircraft.getScores();
                item.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        droppedItems.removeIf(AbstractFlyingObject::notValid);
        heroAircraft.removeInvalid();
    }


    //***********************
    //      Paint 各部分
    //***********************
    public void setBackground(BufferedImage image){
        this.background = image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(background, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(background, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, droppedItems);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    protected void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    protected void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
        y = y +20;
        float skillDuration = ((float)heroAircraft.getEffectTimer())/ 1000;
        if (skillDuration > 0) {
            g.drawString("SKILL:" + skillDuration + "s", x, y);
        }else{
            g.drawString("SKILL: 0s", x, y);
        }
    }


}
