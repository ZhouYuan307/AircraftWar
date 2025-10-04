package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class SuperEliteFactory implements EnemyFactory{
    @Override
    public AbstractEnemy createEnemy() {
        return new SuperElite(
                (int)(ImageManager.SUPER_ELITE_IMAGE.getWidth()*0.5+Math.random() * (Main.WINDOW_WIDTH - ImageManager.SUPER_ELITE_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT * 0.05),
                0,
                5,
                50
        );
    }
}
