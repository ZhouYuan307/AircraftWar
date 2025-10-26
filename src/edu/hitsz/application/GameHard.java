package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.BossFactory;
import edu.hitsz.aircraft.EnemyFactory;

public class GameHard extends Game {
    private int bossCounter = 0;
    public GameHard(String musicMode, String userName) {
        super(musicMode, userName);
        this.gameLevel = 2;
    }

    @Override
    protected void checkForBossSpawn() {

        int deltaScore = score - lastBossScore;
        if (deltaScore > bossInterval && !isBossExist) {
            if (bossInterval > 300) {
                bossInterval -= 30;
                System.out.println("-------Boss生成间隔降低！-------\n"+"Boss生成间隔："+bossInterval+"\n");
            }
            int bossHp = 600 + bossCounter * 100;
            lastBossScore = score;
            AbstractEnemy enemy;
            EnemyFactory factory;
            factory = new BossFactory();
            enemy = factory.createEnemy(bossHp);
            enemyAircrafts.add(enemy);
            isBossExist = true;
            playSoundManager.stopBgm();
            playSoundManager.playBgmBoss();
            bossCounter++;
            System.out.println("-------Boss血量提升！-------\n"+"Boss血量："+bossHp+"\n");
        } else if (isBossExist) {
            lastBossScore = score;
        }
    }


}
