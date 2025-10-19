package edu.hitsz.music;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicThread extends Thread {

    // 播放模式枚举
    public enum PlayMode {
        ONCE,       // 播放一次
        LOOP        // 循环播放
    }

    private final String filename;
    private AudioFormat audioFormat;
    private byte[] samples;
    private PlayMode playMode;
    private volatile boolean isPlaying;

    //构造函数 - 使用音频名称
    public MusicThread(String musicName) {
        this(musicName, PlayMode.ONCE); // 默认播放一次
    }

    //构造函数 - 使用音频名称和播放模式
    public MusicThread(String musicName, PlayMode playMode) {
        this.filename = MusicManager.getInstance().getMusicPath(musicName);
        this.playMode = playMode;
        this.isPlaying = true;
        loadMusic();
    }


    /**
     * 加载音频数据
     */
    public void loadMusic() {
        try {
            //定义一个AudioInputStream用于接收输入的音频数据，使用AudioSystem来获取音频的音频输入流
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
            //用AudioFormat来获取AudioInputStream的格式
            audioFormat = stream.getFormat();
            samples = getSamples(stream);
            stream.close();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("不支持的音频格式: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("读取音频文件失败: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * 获取音频样本数据
     */
    public byte[] getSamples(AudioInputStream stream) {
        int size = (int) (stream.getFrameLength() * audioFormat.getFrameSize());
        byte[] samples = new byte[size];
        DataInputStream dataInputStream = new DataInputStream(stream);
        try {
            dataInputStream.readFully(samples);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return samples;
    }

    /**
     * 播放音频
     */
    public void play(InputStream source) {
        int size = (int) (audioFormat.getFrameSize() * audioFormat.getSampleRate());
        byte[] buffer = new byte[size];
        //源数据行SoureDataLine是可以写入数据的数据行
        SourceDataLine dataLine = null;
        //获取受数据行支持的音频格式DataLine.info
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try {
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat, size);
            dataLine.start();

            do {
                // 重置输入流到开始位置（对于循环播放）
                if (source.markSupported()) {
                    source.mark(Integer.MAX_VALUE);
                }

                int numBytesRead = 0;
                while (numBytesRead != -1 && isPlaying) {
                    numBytesRead = source.read(buffer, 0, buffer.length);
                    if (numBytesRead != -1) {
                        dataLine.write(buffer, 0, numBytesRead);
                    }
                }

                // 重置流以便重新播放
                if (source.markSupported()) {
                    source.reset();
                }

            } while (playMode == PlayMode.LOOP && isPlaying);

        } catch (LineUnavailableException e) {
            System.err.println("音频设备不可用");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("播放音频时发生IO错误");
            e.printStackTrace();
        } finally {
            if (dataLine != null) {
                dataLine.drain();
                dataLine.close();
            }
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //停止播放
    public void stopMusic() {
        this.isPlaying = false;
        this.interrupt();
    }

    //设置播放模式

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    //获取播放模式

    public PlayMode getPlayMode() {
        return playMode;
    }

    public boolean isPlaying(){
        return this.isPlaying && this.isAlive();
    }

    @Override
    public void run() {
        if (samples == null) {
            System.err.println("音频数据未加载，无法播放: " + filename);
            return;
        }

        InputStream stream = new ByteArrayInputStream(samples);
        play(stream);
    }
}


