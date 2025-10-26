package edu.hitsz.item;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ShootCircle;

public class BulletPlusItem extends BaseItem{
    public BulletPlusItem(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activateEffect(HeroAircraft heroAircraft) {
        heroAircraft.setStrategy(new ShootCircle());
        heroAircraft.saveInterval();
        heroAircraft.setShootInterval(1500);
        heroAircraft.isReset = false;
        heroAircraft.setEffectTimer(10000);
    }
}
