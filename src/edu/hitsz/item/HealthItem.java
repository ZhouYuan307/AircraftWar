package edu.hitsz.item;


import edu.hitsz.aircraft.HeroAircraft;

/**
 * 回血道具类
 * 拾取后恢复英雄机的生命值
 */
public class HealthItem extends BaseItem {

    public HealthItem(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 激活回血效果
     * 恢复英雄机的生命值
     */
    @Override
    public void activateEffect(HeroAircraft heroAircraft) {
        int healAmount = 20;
        heroAircraft.heal(healAmount);
    }

}