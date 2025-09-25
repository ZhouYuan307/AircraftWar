package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.item.BaseItem;
import edu.hitsz.item.HealthItem;
import edu.hitsz.item.ItemFactory;


public class EliteEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return new EliteEnemy(
                (int)(Math.random() * (Main.WINDOW_WIDTH - ImageManager.Elite_ENEMY_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT * 0.05),
                0,
                10,
                30
        );
    }
}
