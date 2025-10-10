package edu.hitsz.item;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ShootCircle;

public class BulletPlusItem extends BaseItem{
    public BulletPlusItem(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activateEffect(HeroAircraft heroAircraft) {
        System.out.print("BulletPlus active!");
        heroAircraft.setStrategy(new ShootCircle());
        heroAircraft.setShootInterval(1500);
    }
}
