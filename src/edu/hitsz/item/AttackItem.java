package edu.hitsz.item;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ShootMultiStraight;

/**
 * 炸弹道具类
 */
public class AttackItem extends BaseItem {

    public AttackItem(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activateEffect(HeroAircraft heroAircraft) {
        System.out.print("FireSupply active!");
        heroAircraft.setStrategy(new ShootMultiStraight());
    }

}