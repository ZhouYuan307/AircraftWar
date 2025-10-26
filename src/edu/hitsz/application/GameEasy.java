package edu.hitsz.application;

public class GameEasy extends Game{
    public GameEasy(String musicMode, String userName) {
        super(musicMode, userName);
        this.gameLevel = 0;
    }

    @Override
    protected void checkForBossSpawn() {}

    @Override
    protected void increaseDifficulty() {}

}
