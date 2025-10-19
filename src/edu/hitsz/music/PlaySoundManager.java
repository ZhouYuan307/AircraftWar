package edu.hitsz.music;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaySoundManager {

    private boolean isMusicEnabled;
    public PlaySoundManager(boolean isMusicEnabled) {
        this.isMusicEnabled = isMusicEnabled;
    }

    // 专门的音效线程池
    private final ExecutorService soundExecutor = Executors.newCachedThreadPool(
            new BasicThreadFactory.Builder().namingPattern("sound-effect-%d").daemon(true).build()
    );


    //循环音效控制，加入线程池前将对象映射给字符串，以期通过字符串停止
    private final Map<String, MusicThread> activeLoopSounds = new ConcurrentHashMap<>();


    public void playBulletHit() {
        if (isMusicEnabled) {
            playOnceSound(MusicManager.BULLET_HIT);
        }
    }

    public void playBombExplosion() {
        if(isMusicEnabled) {
            playOnceSound(MusicManager.BOMB_EXPLOSION);
        }

    }

    public void playGameOver() {
        if(isMusicEnabled) {
            playOnceSound(MusicManager.GAME_OVER);
        }
    }

    public void playGetSupply() {
        if(isMusicEnabled) {
            playOnceSound(MusicManager.GET_SUPPLY);
        }
    }

    public void playBgm(){
        if(isMusicEnabled) {
            startLoopSound(MusicManager.BGM);
        }
    }

    public void stopBgm(){
        stopLoopSound(MusicManager.BGM);
    }

    public void playBgmBoss(){
        if(isMusicEnabled) {
            startLoopSound(MusicManager.BGM_BOSS);
        }
    }

    public void stopBgmBoss(){
        stopLoopSound(MusicManager.BGM_BOSS);
    }

    private void playOnceSound(String soundName) {
        soundExecutor.submit(() -> {
            new MusicThread(soundName, MusicThread.PlayMode.ONCE).start();
        });
    }

    //播放循环音效
    public void startLoopSound(String soundType) {
        stopLoopSound(soundType); // 先停止已有的
        MusicThread loopSound = new MusicThread(soundType, MusicThread.PlayMode.LOOP);
        activeLoopSounds.put(soundType, loopSound);
        soundExecutor.submit(loopSound);
    }

    //停止特定循环音效
    public void stopLoopSound(String soundType) {
        MusicThread existing = activeLoopSounds.remove(soundType);
        if (existing != null) {
            existing.stopMusic();
        }
    }

    //停止所有循环音效
    public void stopAllLoopSounds() {
        for (MusicThread sound : activeLoopSounds.values()) {
            sound.stopMusic();
        }
        activeLoopSounds.clear();
    }


    public void shutdown() {
        stopAllLoopSounds();
        soundExecutor.shutdown();
    }
}
