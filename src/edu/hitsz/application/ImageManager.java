package edu.hitsz.application;


import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.item.AttackItem;
import edu.hitsz.item.BombItem;
import edu.hitsz.item.BulletPlusItem;
import edu.hitsz.item.HealthItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE_EASY;
    public static BufferedImage BACKGROUND_IMAGE_NORMAL;
    public static BufferedImage BACKGROUND_IMAGE_HARD;

    public static BufferedImage HERO_IMAGE;
    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;
    public static BufferedImage MOB_ENEMY_IMAGE;
    public static BufferedImage Elite_ENEMY_IMAGE;
    public static BufferedImage HEALTH_ITEM_IMAGE;
    public static BufferedImage BOMB_ITEM_IMAGE;
    public static BufferedImage ATTACK_ITEM_IMAGE;
    public static BufferedImage SUPER_ELITE_IMAGE;
    public static BufferedImage BOSS_IMAGE;
    public static BufferedImage BULLET_PLUS_ITEM_IMAGE;



    static {
        try {

            BACKGROUND_IMAGE_EASY = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
            BACKGROUND_IMAGE_NORMAL = ImageIO.read(new FileInputStream("src/images/bg2.jpg"));
            BACKGROUND_IMAGE_HARD = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));

            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));
            Elite_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            HEALTH_ITEM_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            BOMB_ITEM_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            ATTACK_ITEM_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            SUPER_ELITE_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            BOSS_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));
            BULLET_PLUS_ITEM_IMAGE =  ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));

            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), Elite_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HealthItem.class.getName(), HEALTH_ITEM_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombItem.class.getName(),BOMB_ITEM_IMAGE);
            CLASSNAME_IMAGE_MAP.put(AttackItem.class.getName(),ATTACK_ITEM_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperElite.class.getName(),SUPER_ELITE_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(),BOSS_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BulletPlusItem.class.getName(),BULLET_PLUS_ITEM_IMAGE);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static BufferedImage get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

}
