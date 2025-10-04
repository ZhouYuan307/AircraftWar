package edu.hitsz.aircraft;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import java.util.List;

public class HeroAircraftTest {

    private HeroAircraft hero;

    @BeforeEach
    public void setUp() {
        //reset
        hero = HeroAircraft.getInstance();
        hero.init(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);
    }

    @Test
    public void shoot_BulletCount_Test() {
        List<BaseBullet> bullets = hero.shoot();
        assertEquals(1, bullets.size(), "子弹数量应该为1");
    }

    @Test
    public void shoot_BulletType_Test() {
        List<BaseBullet> bullets = hero.shoot();
        BaseBullet bullet = bullets.get(0);
        assertInstanceOf(HeroBullet.class, bullet, "子弹应为HeroBullet类型");
    }

    @Test
    public void shoot_BulletDirection_Test() {
        List<BaseBullet> bullets = hero.shoot();
        BaseBullet bullet = bullets.get(0);
        assertTrue(bullet.getSpeedY() < 0, "子弹应该向上飞行");
    }


    @Test
    public void heal_FullHealth_Test() {
        hero.heal(1);
        assertEquals(100, hero.getHp(), "满血治疗不应改变生命值");
    }

    @Test
    public void heal_BoundaryHeal_Test() {
        hero.decreaseHp(1);
        hero.heal(1);
        assertEquals(100, hero.getHp(), "边界治疗应正确恢复");
    }

    @Test
    public void heal_NormalHeal_Test() {
        hero.decreaseHp(50);
        hero.heal(30);
        assertEquals(80, hero.getHp(), "检验治疗后hp是否正确");
    }

    @Test
    public void heal_OverHeal_Test() {
        hero.decreaseHp(50);
        hero.heal(30);
        hero.heal(30);
        assertEquals(100, hero.getHp(), "治疗不应超过最大生命值");
    }

    @Test
    public void decreaseHp_NormalDamage_Test() {
        hero.decreaseHp(30);
        assertEquals(70, hero.getHp(), "正常伤害应减少生命值");
    }

    @Test
    public void decreaseHp_NegativeDamage_Test() {
        hero.decreaseHp(30); // 先造成一些伤害
        hero.decreaseHp(-10);
        assertEquals(70, hero.getHp(), "负伤害不应改变生命值");
    }

    @Test
    public void decreaseHp_LethalDamage_Test() {
        hero.decreaseHp(120);
        assertEquals(0, hero.getHp(), "致命伤害生命值应为0而非负数");
    }

    @Test
    public void decreaseHp_AircraftInvalid_Test() {
        hero.decreaseHp(120);
        assertTrue(hero.notValid(), "飞机应该消失");
    }
}