package edu.hitsz.aircraft;


import edu.hitsz.bullet.BulletFactory;
import edu.hitsz.item.BaseItem;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

public abstract class AbstractEnemy extends AbstractAircraft {



    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp, ShootStrategy strategy, BulletFactory bulletFactory) {
        super(locationX, locationY, speedX, speedY, hp, strategy, bulletFactory);
    }

    public abstract List<BaseItem> spawnItems();

    public abstract int getScores();
}
