package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.EnemyBulletFactory;
import edu.hitsz.strategy.ShootNull;
import edu.hitsz.strategy.ShootStraight;

public class MobEnemyFactory implements EnemyFactory {
    @Override
    public AbstractEnemy createEnemy() {
        return new MobEnemy(
                (int)(ImageManager.MOB_ENEMY_IMAGE.getWidth()*0.5+Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                10,
                30,
                new ShootNull(),
                new EnemyBulletFactory()
        );
    }
}