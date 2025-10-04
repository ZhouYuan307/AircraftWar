package edu.hitsz.aircraft;


import edu.hitsz.item.BaseItem;

import java.util.List;

public abstract class AbstractEnemy extends AbstractAircraft {

    /**攻击方式 */

    //子弹数量
    protected int shootNum;
    //子弹伤害
    protected int power = 30;
    //子弹射击方向 (向上发射：-1，向下发射：1)
    protected int direction = 1;

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    public abstract List<BaseItem> spawnItems();

    public abstract int getScores();
}
