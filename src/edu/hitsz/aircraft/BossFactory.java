package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.EnemyBulletFactory;
import edu.hitsz.strategy.ShootCircle;

public class BossFactory implements EnemyFactory{
    @Override
    public AbstractEnemy createEnemy() {
        return new BossEnemy(
                (int)(ImageManager.BOSS_IMAGE.getWidth()*0.5+Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT * 0.2),
                3,
                0,
                600,
                new ShootCircle(),
                new EnemyBulletFactory()
        );
    }
}
