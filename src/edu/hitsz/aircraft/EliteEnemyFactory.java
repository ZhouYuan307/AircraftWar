package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.EnemyBulletFactory;
import edu.hitsz.strategy.ShootMultiStraight;
import edu.hitsz.strategy.ShootStraight;


public class EliteEnemyFactory implements EnemyFactory {
    @Override
    public AbstractEnemy createEnemy() {
        return new EliteEnemy(
                (int)(ImageManager.Elite_ENEMY_IMAGE.getWidth()*0.5+Math.random() * (Main.WINDOW_WIDTH - ImageManager.Elite_ENEMY_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT * 0.05),
                0,
                5,
                30,
                new ShootStraight(),
                new EnemyBulletFactory()
        );
    }
}
