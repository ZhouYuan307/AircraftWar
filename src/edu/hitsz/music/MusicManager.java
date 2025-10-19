package edu.hitsz.music;


import java.util.HashMap;
import java.util.Map;

/**
 * 音频管理器，统一管理音频文件和路径
 * 参考图片管理器的设计模式
 */
public class MusicManager {


    //背景音乐
    public static final String BGM = "bgm";
    public static final String BGM_BOSS = "bgm_boss";

    //音效
    public static final String BULLET_HIT = "bullet_hit";
    public static final String BOMB_EXPLOSION = "bomb_explosion";
    public static final String GAME_OVER = "game_over";
    public static final String GET_SUPPLY = "get_supply";


    // 单例实例
    private static MusicManager instance;

    // 存储音频文件路径的映射
    private final Map<String, String> musicMap;


    private MusicManager() {
        musicMap = new HashMap<>();
        // 初始化常用音频文件映射
        initializeMusicMap();
    }

    /**
     * 获取单例实例
     */
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    //初始化音频文件映射

    private void initializeMusicMap() {
        // 背景音乐
        musicMap.put(BGM, "src/videos/bgm.wav");
        musicMap.put(BGM_BOSS, "src/videos/bgm_boss.wav");

        // 音效
        musicMap.put(BULLET_HIT, "src/videos/bullet_hit.wav");
        musicMap.put(BOMB_EXPLOSION, "src/videos/bomb_explosion.wav");
        musicMap.put(GAME_OVER, "src/videos/game_over.wav");
        musicMap.put(GET_SUPPLY, "src/videos/get_supply.wav");
    }

    //返回路径
    public String getMusicPath(String musicName) {
        return musicMap.get(musicName);
    }

    //添加新的音频文件映射
    public void addMusic(String musicName, String fileName) {
        musicMap.put(musicName, fileName);
    }

    //检查音频文件是否存在
    public boolean containsMusic(String musicName) {
        return musicMap.containsKey(musicName);
    }

    //移除音频文件映射
    public void removeMusic(String musicName) {
        musicMap.remove(musicName);
    }
}