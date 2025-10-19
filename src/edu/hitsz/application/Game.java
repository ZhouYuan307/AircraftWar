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
public class Game extends JPanel {

    //name
    private final String userName;

    //音乐相关
    private final PlaySoundManager playSoundManager;



    //背景图片
    private BufferedImage background;
    private int backGroundTop = 0;

    //Scheduled 线程池，用于任务调度
    private final ScheduledExecutorService executorService;

    //时间间隔(ms)，控制刷新频率
    private final int timeInterval = 40;

    //飞行对象
    private final HeroAircraft heroAircraft;
    private final List<AbstractEnemy> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseItem> droppedItems;

    //参数配置
    //屏幕中出现的敌机最大数量
    private final int enemyMaxNumber = 5;
    //当前得分
    private int score = 0;
    //当前时刻
    private int time = 0;
    //boss生成间隔
    private final int bossInterval = 800;
    // 记录上一次生成boss时的分数基数
    private int lastBossScore = 0;
    //周期（ms)指示子弹的发射、敌机的产生频率
    private final int cycleDuration = 600;
    private int cycleTime = 0;
    //boss状态
    private boolean isBossExist = false;

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


    private static AbstractEnemy getEnemy() {
        AbstractEnemy enemy;
        EnemyFactory factory;
        Random random = new Random();
        double enemyType = random.nextDouble();

        if (enemyType < 0.5) {
            factory = new MobEnemyFactory();
            enemy = factory.createEnemy();
        } else if (enemyType <0.9) {
            factory = new EliteEnemyFactory();
            enemy = factory.createEnemy();
        } else{
            factory = new SuperEliteFactory();
            enemy = factory.createEnemy();
        }
        return enemy;
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
                    checkForBossSpawn();
                }


                // 飞机射出子弹
                shootAction();
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

    private void checkSkillDuration() {
        if (heroAircraft.getEffectTimer()>0){
            heroAircraft.effectTimerUpdate(timeInterval);
        }else if(!heroAircraft.isReset){
            heroAircraft.resetStrategy();
        }
    }

    private void checkForBossSpawn() {

        int currentInterval = score / bossInterval;
        int lastInterval = lastBossScore / bossInterval;
        if (currentInterval > lastInterval && !isBossExist) {
            lastBossScore = score;
            AbstractEnemy enemy;
            EnemyFactory factory;
            factory = new BossFactory();
            enemy = factory.createEnemy();
            enemyAircrafts.add(enemy);
            isBossExist = true;
            playSoundManager.stopBgm();
            playSoundManager.playBgmBoss() ;
            System.out.println("Boss!");
        }
    }


    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for (AbstractEnemy enemy : enemyAircrafts) {
            List<BaseBullet> bullets = enemy.executeStrategy();
            enemyBullets.addAll(bullets);
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.executeStrategy());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void itemsMoveAction() {
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
    private void crashCheckAction() {
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
                        // TODO 获得分数，产生道具补给，并产生boss
                        if(enemyAircraft instanceof BossEnemy)
                        {
                            playSoundManager.stopBgmBoss();
                            playSoundManager.playBgm();
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

        // Todo: 我方获得道具，道具生效
        for (BaseItem item : droppedItems) {
            if (item.notValid()) {
                continue;
            }
            if (heroAircraft.crash(item)) {
                // Todo 英雄机撞到道具，获得增益
                if (item instanceof BombItem){
                    playSoundManager.playBombExplosion();
                }else{
                    playSoundManager.playGetSupply();
                }
                item.activateEffect(heroAircraft);
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
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        droppedItems.removeIf(AbstractFlyingObject::notValid);
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

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
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

    private void paintScoreAndLife(Graphics g) {
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
